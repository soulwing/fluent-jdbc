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

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;

/**
 * A simple lexer for SQL text.
 *
 * @author Carl Harris
 */
class Lexer implements Closeable {

  static class Token {

    enum Type {
      END_OF_STATEMENT,
      STRING_LITERAL,
      COMMENT,
      WHITESPACE,
      STATEMENT_TEXT;
    }

    final Type type;

    final Object value;

    Token(Type type, Object value) {
      this.type = type;
      this.value = value;
    }

    Token(Type type) {
      this(type, null);
    }

  }

  private final Reader reader;
  private StringBuilder text;
  private Token nextToken;

  Lexer(Reader reader) {
    this.reader = reader;
  }

  Token next() throws IOException {
    Token token = null;
    if (nextToken != null) {
      token = nextToken;
      nextToken = null;
    }
    else {
      token = doNext();
      if (token == null) return null;
      text = new StringBuilder();
      while (token != null && token.type == Token.Type.STATEMENT_TEXT) {
        text.append((char) token.value);
        token = doNext();
      }

      if (text.length() > 0) {
        nextToken = token;
        token = new Token(Token.Type.STATEMENT_TEXT, text.toString());
      }
    }
    return token;
  }

  Token doNext() throws IOException {
    final StringBuilder text = new StringBuilder();
    int c = read();
    if (Character.isSpaceChar(c)
        || Character.isWhitespace(c)) {
      return new Token(Token.Type.WHITESPACE, readWhitespace(c));
    }
    else if (c == ';') {
      return new Token(Token.Type.END_OF_STATEMENT, ';');
    }
    else if (c == '-') {
      if (read() == '-') {
        return new Token(Token.Type.COMMENT, readLineComment());
      }
      unread();
      return new Token(Token.Type.STATEMENT_TEXT, (char) c);
    }
    else if (c == '/') {
      if (read() == '*') {
        return new Token(Token.Type.COMMENT, readBlockComment());
      }
      return new Token(Token.Type.STATEMENT_TEXT, (char) c);
    }
    else if (c == '\'') {
      return new Token(Token.Type.STRING_LITERAL, readStringLiteral());
    }
    else if (c != -1) {
      return new Token(Token.Type.STATEMENT_TEXT, (char) c);
    }
    else {
      return null;
    }
  }

  private int read() throws IOException {
    reader.mark(1);
    return reader.read();
  }

  private void unread() throws IOException {
    reader.reset();
  }

  private String readStatementText(int c) throws IOException {
    StringBuilder sb = new StringBuilder();

    return sb.toString();
  }
  private String readWhitespace(int c) throws IOException {
    StringBuilder sb = new StringBuilder();
    sb.append((char) c);
    c = read();
    while (Character.isSpaceChar((char) c)) {
      sb.append((char) c);
      c = read();
    }
    unread();
    return sb.toString();
  }

  private String readLineComment() throws IOException {
    StringBuilder sb = new StringBuilder();
    sb.append("--");
    int c = read();
    while (c != '\r' && c != '\n') {
      sb.append((char) c);
      c = read();
    }
    if (c == '\r') {
      c = read();
      if (c != '\n') {
        unread();
      }
    }
    return sb.toString();
  }

  private String readBlockComment() throws IOException {
    StringBuilder sb = new StringBuilder();
    sb.append("/*");
    int c = read();
    while (true) {
      sb.append((char) c);
      if (c == '*') {
        c = read();
        if (c == '/') {
          sb.append('/');
          break;
        }
        else {
          unread();
        }
      }
      c = read();
    }
    return sb.toString();
  }


  private String readStringLiteral() throws IOException {
    StringBuilder sb = new StringBuilder();
    sb.append('\'');
    int c = read();
    while (true) {
      if (c == '\'') {
        c = read();
        if (c == '\'') {
          sb.append("\'\'");
        }
        else {
          unread();
          break;
        }
      }
      else if (c == -1) {
        throw new IllegalStateException("unterminated string literal");
      }
      else {
        sb.append((char) c);
      }
      c = read();
    }
    sb.append('\'');
    return sb.toString();
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

}
