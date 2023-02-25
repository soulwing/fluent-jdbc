/*
 * File created on Feb 24, 2023
 *
 * Copyright (c) 2023 Carl Harris, Jr
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

/**
 * A lexical scanner for the Postgres SQL dialect.
 *
 * @author Carl Harris
 */
public class PostgresScanner extends Scanner {

  public static final Scanner INSTANCE = new PostgresScanner();

  private PostgresScanner() {}

  @Override
  protected Token scanToken(char c, Request request) throws IOException {
    Token token = null;
    if (c == '$') {
       token = dollarQuotedString(c, request);
    }
    if (token == null) {
      token = super.scanToken(c, request);
    }
    return token;
  }

  private Token dollarQuotedString(char c, Request request) throws IOException {
    final String tag = scanTag(request);
    if (tag == null) return null;
    while (!request.eof()) {
      c = request.advance();
      if (c == '$' && matchTag(tag, request)) {
        return tokenOf(Token.Type.LITERAL, request);
      }
    }
    request.error("unterminated dollar-quoted string");
    return null;
  }

  private String scanTag(Request request) throws IOException {
    final StringBuilder tag = new StringBuilder();
    while (!request.eof()) {
      if (request.match('$')) break;
      final char c = request.peek();
      if (!super.isIdentifierChar(c)) {
        request.error("invalid dollar-quoting tag");
        return null;
      }
      tag.append(c);
      request.advance();
    }
    return tag.toString();
  }

  private boolean matchTag(String tag, Request request) throws IOException {
    final StringBuilder foundTag = new StringBuilder();
    final int expectedLen = tag.length();
    int len = 0;
    request.mark(expectedLen + 1);
    while (!request.eof() && len < expectedLen + 1) {
      int c = request.read();
      if (c == '$') break;
      if (c != -1) {
        len++;
        foundTag.append((char) c);
      }
    }
    request.reset();
    final boolean found = tag.equals(foundTag.toString());
    if (found) {
      for (int i = 0; i < expectedLen + 1; i++) {
        request.advance();
      }
    }
    return found;
  }

  @Override
  protected boolean isIdentifierChar(char c) {
    return super.isIdentifierChar(c) || c == '$';
  }

}