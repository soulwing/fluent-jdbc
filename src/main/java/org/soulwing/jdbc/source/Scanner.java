/*
 * File created on Feb 23, 2023
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
import java.util.LinkedList;
import java.util.List;

/**
 * A lexical scanner for standard SQL.
 *
 * @author Carl Harris
 */
class Scanner {

  public static final Scanner INSTANCE = new Scanner();

  Scanner() {}

  static class Request {
    public final SourceReader reader;
    public final ErrorReporter errorReporter;

    private Request(SourceReader reader, ErrorReporter errorReporter) {
      this.reader = reader;
      this.errorReporter = errorReporter;
    }

    public void close() throws IOException {
      reader.close();
    }

    public int getStart() {
      return reader.getStart();
    }

    public int getCurrent() {
      return reader.getCurrent();
    }

    public String getLexeme() {
      return reader.getLexeme();
    }

    public String getLexeme(int start, int end) {
      return reader.getLexeme(start, end);
    }

    public void markStart() {
      reader.markStart();
    }

    public boolean eof() {
      return reader.eof();
    }

    public boolean match(char expected) throws IOException {
      return reader.match(expected);
    }

    public char advance() throws IOException {
      return reader.advance();
    }

    public char peek() throws IOException {
      return reader.peek();
    }

    public int read() throws IOException {
      return reader.read();
    }

    public void mark(int readAheadLimit) throws IOException {
      reader.mark(readAheadLimit);
    }

    public void reset() throws IOException {
      reader.reset();
    }

    public void error(String message) {
      errorReporter.error(reader.getStart(), reader.getCurrent() - reader.getStart(), message);
    }
  }

  public final List<Token> scanTokens(SourceReader reader,
      ErrorReporter errorReporter) throws IOException {
    final Request request = new Request(reader, errorReporter);
    final List<Token> tokens = new LinkedList<>();
    while (!request.eof()) {
      final Token token = nextToken(request);
      if (token != null) {
        tokens.add(token);
      }
    }
    if (tokens.isEmpty() || tokens.get(tokens.size() - 1).getType() != Token.Type.EOF) {
      request.markStart();
      tokens.add(tokenOf(Token.Type.EOF, request));
    }
    return tokens;
  }

  protected Token nextToken(Request request) throws IOException {
    request.markStart();
    final char c = request.advance();
    return scanToken(c, request);
  }

  protected Token scanToken(char c, Request request) throws IOException {
    if (c == SourceReader.EOF)
      return tokenOf(Token.Type.EOF, request);
    else if (c == '(')
      return tokenOf(Token.Type.LEFT_PAREN, request);
    else if (c == ')')
      return tokenOf(Token.Type.RIGHT_PAREN, request);
    else if (c == '{')
      return tokenOf(Token.Type.LEFT_BRACE, request);
    else if (c == '}')
      return tokenOf(Token.Type.RIGHT_BRACE, request);
    else if (c == ';')
      return tokenOf(Token.Type.SEMICOLON, request);
    else if (c == '-') {
      if (request.match('-')) {
        return lineComment(request);
      }
      else {
        return operator(request);
      }
    }
    else if (c == '/') {
      if (request.match('*')) {
        return blockComment(request);
      }
      else {
        return operator(request);
      }
    }
    else if (c == '"')
      return quotedIdentifier(request);
    else if (c == '\'')
      return string(request);
    else if (isWhitespace(c))
      return whitespace(request);
    else if (isIdentifierStart(c)) {
      return identifier(request);
    }
    else if (isNumeric(c)) {
      return number(request);
    }
    else if (isOperator(c)) {
      return operator(request);
    }

    request.error("unrecognized input '" + c + "'");
    return null;
  }

  protected Token blockComment(Request request) throws IOException {
    // consume the '*' (the '/' is already consumed)
    request.advance();
    while (!request.eof()) {
      final char c = request.advance();
      if (c == '/' && request.peek() == '*') {
        // recursively consume a nested comment (but ignore the returned token)
        blockComment(request);
      }
      else if (c == '*' && request.peek() == '/') {
        // consume the '/' (the '*' is already consumed)
        request.advance();
        return tokenOf(Token.Type.COMMENT, request);
      }
    }
    request.error("unterminated block comment");
    return null;
  }

  protected Token lineComment(Request request) throws IOException {
    while (!request.eof()) {
      final char c = request.advance();
      if (c == '\n') break;
    }
    return tokenOf(Token.Type.COMMENT, request);
  }

  protected Token whitespace(Request request) throws IOException {
    while (!request.eof()) {
      final char c = request.peek();
      if (!isWhitespace(c)) break;
      request.advance();
    }
    return tokenOf(Token.Type.WHITESPACE, request);
  }

  protected Token string(Request request) throws IOException {
    while (!request.eof()) {
      final char c = request.advance();
      if (c == '\'') {
        if (!request.match('\'')) {
          return tokenOf(Token.Type.LITERAL, request);
        }
      }
    };
    request.error("unterminated string");
    return null;
  }

  protected Token identifier(Request request) throws IOException {
    while (!request.eof()) {
      final char c = request.peek();
      if (!isIdentifierChar(c)) break;
      request.advance();
    }
    final String word = request.getLexeme(0, request.getCurrent() - request.getStart());
    Token.Type type = ReservedWords.toToken(word);
    if (type == null) {
      type = Token.Type.IDENTIFIER;
    }
    return tokenOf(type, request);
  }

  protected Token quotedIdentifier(Request request) throws IOException {
    while (!request.eof()) {
      final char c = request.advance();
      if (c == '"') {
        return tokenOf(Token.Type.IDENTIFIER, request);
      }
    };
    request.error("unterminated quoted identifier");
    return null;
  }

  protected Token number(Request request) throws IOException {
    while (!request.eof()) {
      final char c = request.peek();
      if (!isNumberChar(c)) break;
      request.advance();
    }
    return tokenOf(Token.Type.LITERAL, request);
  }

  protected Token operator(Request request) throws IOException {
    while (!request.eof()) {
      final char c = request.peek();
      if (!isOperator(c)) break;
      request.advance();
    }
    return tokenOf(Token.Type.LITERAL, request);
  }

  protected Token tokenOf(Token.Type type, Request request) {
    return new Token(type, request.getLexeme(), request.getStart());
  }

  protected boolean isOperator(char c) {
    return !isAlphaNumeric(c) && !isWhitespace(c);
  }

  protected boolean isIdentifierStart(char c) {
    return isAlpha(c) || c == '_';
  }

  protected boolean isIdentifierChar(char c) {
    return isAlphaNumeric(c) || c == '_';
  }

  protected boolean isNumberChar(char c) {
    return isNumeric(c) || c == '.' || c == '-' || c == '+' || c == 'e' || c == 'E';
  }

  protected boolean isAlphaNumeric(char c) {
    return isAlpha(c) || isNumeric(c);
  }

  protected boolean isAlpha(char c) {
    return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
  }

  protected boolean isNumeric(char c) {
    return c >= '0' && c <= '9';
  }

  protected boolean isWhitespace(char c) {
    return c == '\0' || Character.isSpaceChar(c) || Character.isWhitespace(c);
  }

}
