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

/**
 * A token recognized by a scanner for an SQL source.
 *
 * @author Carl Harris
 */
public class Token {

  public enum Type {
    LEFT_PAREN,
    RIGHT_PAREN,
    LEFT_BRACE,
    RIGHT_BRACE,
    BEGIN,
    END,
    IF,
    LOOP,
    CASE,
    TRANSACTION,
    SEMICOLON,
    LITERAL,
    IDENTIFIER,
    COMMENT,
    WHITESPACE,
    EOF;
  }

  private final Type type;
  private final String lexeme;
  private final int offset;
  private final int length;

  public Token(Type type, String lexeme, int offset) {
    this.type = type;
    this.lexeme = lexeme;
    this.offset = offset;
    this.length = lexeme.length();
  }

  public Type getType() {
    return type;
  }

  public String getLexeme() {
    return lexeme;
  }

  public int getOffset() {
    return offset;
  }

  public int getLength() {
    return length;
  }

  @Override
  public String toString() {
    return String.format("%s(i=%d, l=%d, s='%s')", type.name(), offset, length, lexeme.trim());
  }
}
