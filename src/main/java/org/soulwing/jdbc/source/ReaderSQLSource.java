/*
 * File created on Aug 5, 2015
 *
 * Copyright (c) 2015 Carl Harris, Jr
 * and others as noted
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.soulwing.jdbc.source;

import java.io.IOException;
import java.io.Reader;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * An {@link SQLSource} that reads statements from a {@link Reader}.
 *
 * @author Carl Harris
 */
public class ReaderSQLSource implements SQLSource {

  private static final Set<Token.Type> SPECIAL_ENDS = EnumSet.of(
      Token.Type.IF,
      Token.Type.LOOP,
      Token.Type.CASE);

  private final SQLInputErrorReporter reporter = new SQLInputErrorReporter();

  private final SourceReader reader;
  private final Scanner scanner;

  private List<Token> tokens;
  private int cursor;

  public ReaderSQLSource(Reader reader) {
    this(reader, DefaultScanner.INSTANCE);
  }

  public ReaderSQLSource(Reader reader, Scanner scanner) {
    this(new DelegatingSourceReader(reader), scanner);
  }

  ReaderSQLSource(SourceReader reader, Scanner scanner) {
    this.reader = reader;
    this.scanner = scanner;
  }

  @Override
  public String next() throws SQLInputException {
    if (reporter.hasError()) {
      throw reporter.getException();
    }
    if (tokens == null) {
      if (scanner == null) {
        throw new IllegalArgumentException("scanner is required");
      }
      try {
        tokens = scanner.scanTokens(reader, reporter);
      }
      catch (IOException ex) {
        reporter.error(reader.getStart(), reader.getCurrent(), ex.getMessage());
      }
      if (reporter.hasError()) {
        throw reporter.getException();
      }
    }
    String statement = reassembleStatement();
    while (statement != null && statement.isEmpty()) {
      statement = reassembleStatement();
    }
    return statement;
  }

  private String reassembleStatement() {
    if (cursor >= tokens.size()) {
      return null;
    }
    final StringBuilder statement = new StringBuilder();
    boolean done = false;
    while (cursor < tokens.size() && !done) {
      final Token token = tokens.get(cursor++);
      final Token.Type type = token.getType();
      if (type == Token.Type.BEGIN) {
        statement.append(token.getLexeme());
        if (!isBeginTransaction()) {
          skipToMatchingEnd(token, statement);
        }
      }
      else {
        done = type == Token.Type.SEMICOLON || type == Token.Type.EOF;
        if (!done) {
          statement.append(token.getLexeme());
        }
      }
    }
    return statement.toString().trim();
  }

  private void skipToMatchingEnd(Token begin, StringBuilder statement) {
    boolean done = false;
    while (cursor < tokens.size() && !done) {
      final Token token = tokens.get(cursor++);
      statement.append(token.getLexeme());
      if (token.getType() == Token.Type.END) {
        done = !isSpecialEnd();
      }
      else if (token.getType() == Token.Type.BEGIN) {
        skipToMatchingEnd(token, statement);
      }
    }
    if (!done) {
      throw new SQLInputException(begin.getOffset(), begin.getLength(),
          "mismatched BEGIN");
    }
  }

  private boolean isBeginTransaction() {
    int start = cursor;
    Token token = null;
    while (cursor < tokens.size()) {
      token = tokens.get(cursor++);
      if (token.getType() != Token.Type.WHITESPACE) break;
    }
    cursor = start;
    return token != null
        && token.getType() == Token.Type.TRANSACTION;
  }

  private boolean isSpecialEnd() {
    int start = cursor;
    Token token = null;
    while (cursor < tokens.size()) {
      token = tokens.get(cursor++);
      if (token.getType() != Token.Type.WHITESPACE) break;
    }
    cursor = start;
    return token != null && SPECIAL_ENDS.contains(token.getType());
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

}
