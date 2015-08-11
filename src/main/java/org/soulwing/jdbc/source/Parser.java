/*
 * File created on Aug 11, 2015
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
 * A simple partial parser for "generic" SQL.
 *
 * @author Carl Harris
 */
class Parser implements Closeable {

  private final Reader reader;
  private final Lexer lexer;

  Parser(Reader reader) {
    this.reader = reader;
    this.lexer = new Lexer(reader);
  }

  String next() throws IOException {
    String statement = doNext();
    while (statement != null && statement.isEmpty()) {
      statement = doNext();
    }
    return statement;
  }

  private String doNext() throws IOException {
    StringBuilder statement = new StringBuilder();
    Lexer.Token token = lexer.next();
    if (token == null) return null;
    while (token != null && token.type != Lexer.Token.Type.END_OF_STATEMENT) {
      if (token.type == Lexer.Token.Type.WHITESPACE) {
        statement.append(' ');
      }
      else if (token.type != Lexer.Token.Type.COMMENT) {
        statement.append(token.value);
      }
      token = lexer.next();
    }
    return statement.toString().trim();
  }

  @Override
  public void close() throws IOException {
    lexer.close();
  }

}
