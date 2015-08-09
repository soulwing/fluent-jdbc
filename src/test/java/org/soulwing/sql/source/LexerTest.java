/*
 * File created on Aug 6, 2015
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
package org.soulwing.sql.source;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.junit.Test;
import org.soulwing.sql.source.Lexer.Token;

/**
 * Unit tests for the {@link Lexer}.
 * @author Carl Harris
 */
public class LexerTest {

  private static final String TEST_PATH = "lexerTest/";

  @Test
  public void testBasicStatement() throws Exception {
    Lexer lexer = new Lexer(resourceReader("basicStatement.sql"));
    Token token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.STATEMENT_TEXT)));
    assertThat(token.value, is(equalTo((Object) "INSERT")));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.WHITESPACE)));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.STATEMENT_TEXT)));
    assertThat(token.value, is(equalTo((Object) "INTO")));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.WHITESPACE)));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.STATEMENT_TEXT)));
    assertThat(token.value, is(equalTo((Object) "something(col1,")));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.WHITESPACE)));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.STATEMENT_TEXT)));
    assertThat(token.value, is(equalTo((Object) "col2)")));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.WHITESPACE)));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.STATEMENT_TEXT)));
    assertThat(token.value, is(equalTo((Object) "VALUES(1,")));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.WHITESPACE)));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.STATEMENT_TEXT)));
    assertThat(token.value, is(equalTo((Object) "2)")));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.END_OF_STATEMENT)));
  }

  @Test
  public void testStatementWithLineComment() throws Exception {
    Lexer lexer = new Lexer(resourceReader("statementWithLineComment.sql"));
    Token token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.COMMENT)));
    assertThat(token.value, is(equalTo((Object) "-- this is a line comment")));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.STATEMENT_TEXT)));
    assertThat(token.value, is(equalTo((Object) "INSERT")));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.WHITESPACE)));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.STATEMENT_TEXT)));
    assertThat(token.value, is(equalTo((Object) "INTO")));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.WHITESPACE)));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.STATEMENT_TEXT)));
    assertThat(token.value, is(equalTo((Object) "something(col1,")));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.WHITESPACE)));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.STATEMENT_TEXT)));
    assertThat(token.value, is(equalTo((Object) "col2)")));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.WHITESPACE)));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.COMMENT)));
    assertThat(token.value, is(equalTo((Object) "-- this is another line comment")));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.WHITESPACE)));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.STATEMENT_TEXT)));
    assertThat(token.value, is(equalTo((Object) "VALUES(1,")));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.WHITESPACE)));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.STATEMENT_TEXT)));
    assertThat(token.value, is(equalTo((Object) "2)")));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.END_OF_STATEMENT)));
  }

  @Test
  public void testStatementWithBlockComment() throws Exception {
    Lexer lexer = new Lexer(resourceReader("statementWithBlockComment.sql"));
    Token token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.COMMENT)));
    assertThat(token.value, is(equalTo((Object) "/* this is a\nblock comment */")));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.WHITESPACE)));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.STATEMENT_TEXT)));
    assertThat(token.value, is(equalTo((Object) "INSERT")));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.WHITESPACE)));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.STATEMENT_TEXT)));
    assertThat(token.value, is(equalTo((Object) "INTO")));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.WHITESPACE)));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.STATEMENT_TEXT)));
    assertThat(token.value, is(equalTo((Object) "something(col1,")));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.WHITESPACE)));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.STATEMENT_TEXT)));
    assertThat(token.value, is(equalTo((Object) "col2)")));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.WHITESPACE)));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.COMMENT)));
    assertThat(token.value, is(equalTo((Object) "/* this is another block comment */")));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.WHITESPACE)));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.STATEMENT_TEXT)));
    assertThat(token.value, is(equalTo((Object) "VALUES(1,")));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.WHITESPACE)));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.STATEMENT_TEXT)));
    assertThat(token.value, is(equalTo((Object) "2)")));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.END_OF_STATEMENT)));
  }


  @Test
  public void testStatementWithStringLiteral() throws Exception {
    Lexer lexer = new Lexer(resourceReader("statementWithStringLiteral.sql"));
    Token token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.STATEMENT_TEXT)));
    assertThat(token.value, is(equalTo((Object) "INSERT")));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.WHITESPACE)));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.STATEMENT_TEXT)));
    assertThat(token.value, is(equalTo((Object) "INTO")));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.WHITESPACE)));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.STATEMENT_TEXT)));
    assertThat(token.value, is(equalTo((Object) "something(col1,")));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.WHITESPACE)));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.STATEMENT_TEXT)));
    assertThat(token.value, is(equalTo((Object) "col2)")));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.WHITESPACE)));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.STATEMENT_TEXT)));
    assertThat(token.value, is(equalTo((Object) "VALUES(")));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.STRING_LITERAL)));
    assertThat(token.value, is(equalTo((Object) "'string literal'")));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.STATEMENT_TEXT)));
    assertThat(token.value, is(equalTo((Object) ",")));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.WHITESPACE)));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.STRING_LITERAL)));
    assertThat(token.value, is(equalTo((Object) "'something''s going on here'")));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.STATEMENT_TEXT)));
    assertThat(token.value, is(equalTo((Object) ")")));
    token = lexer.next();
    assertThat(token.type, is(equalTo(Token.Type.END_OF_STATEMENT)));
  }

  private Reader resourceReader(String name) throws IOException {
    URL location = getClass().getClassLoader().getResource(TEST_PATH + name);
    if (location == null) {
      throw new FileNotFoundException(name);
    }
    return new BufferedReader(new
        InputStreamReader(location.openStream(), "UTF-8"));
  }

}
