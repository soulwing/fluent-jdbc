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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

/**
 * Unit tests for {@link DefaultScanner}.
 *
 * @author Carl Harris
 */
public class ScannerTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private ErrorReporter errorReporter;

  @Test
  public void testEmptyInput() throws Exception {
    final List<Token> tokens = DefaultScanner.INSTANCE.scanTokens(
        sourceReaderWith(""), errorReporter);
    assertThat(tokens.size(), is(equalTo(1)));
    assertThat(tokens.get(0).getType(), is(Token.Type.EOF));
  }

  @Test
  public void testScanWhitespace() throws Exception {
    final String input = "\0\011\012\013\014\015 ";
    validateInput(input, Token.Type.WHITESPACE);
  }

  @Test
  public void testScanSemicolon() throws Exception {
    validateInput(";", Token.Type.SEMICOLON);
  }

  @Test
  public void testScanLeftParen() throws Exception {
    validateInput("(", Token.Type.LEFT_PAREN);
  }

  @Test
  public void testScanRightParen() throws Exception {
    validateInput(")", Token.Type.RIGHT_PAREN);
  }

  @Test
  public void testScanLeftBrace() throws Exception {
    validateInput("{", Token.Type.LEFT_BRACE);
  }

  @Test
  public void testScanRightBrace() throws Exception {
    validateInput("}", Token.Type.RIGHT_BRACE);
  }

  @Test
  public void testScanLineComment() throws Exception {
    validateInput("-- comment", Token.Type.COMMENT);
  }

  @Test
  public void testScanBlockComment() throws Exception {
    validateInput("/* comment */", Token.Type.COMMENT);
  }

  @Test
  public void testScanBlockCommentWithNesting() throws Exception {
    validateInput("/* outer /* nested /* more nested comment */ comment */ comment */",
        Token.Type.COMMENT);
  }

  @Test
  public void testString() throws Exception {
    validateInput("'string '' string'", Token.Type.LITERAL);
  }

  @Test
  public void testUnterminatedString() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(errorReporter).error(0, 2, "unterminated string");
      }
    });
    DefaultScanner.INSTANCE.scanTokens(sourceReaderWith("'"), errorReporter);
  }

  @Test
  public void testOperatorChars() throws Exception {
    validateInput("+@$%^&*/!`~=#", Token.Type.LITERAL);
  }

  @Test
  public void testBareMinus() throws Exception {
    validateInput("-", Token.Type.LITERAL);
  }

  @Test
  public void testBareSlash() throws Exception {
    validateInput("/", Token.Type.LITERAL);
  }

  @Test
  public void testIdentifier() throws Exception {
    validateInput("AZaz_09", Token.Type.IDENTIFIER);
  }

  @Test
  public void testBEGIN() throws Exception {
    validateInput("BEGIN", Token.Type.BEGIN);
  }

  @Test
  public void testCASE() throws Exception {
    validateInput("CASE", Token.Type.CASE);
  }

  @Test
  public void testEND() throws Exception {
    validateInput("END", Token.Type.END);
  }

  @Test
  public void testIF() throws Exception {
    validateInput("IF", Token.Type.IF);
  }

  @Test
  public void testLOOP() throws Exception {
    validateInput("LOOP", Token.Type.LOOP);
  }

  @Test
  public void testQuotedIdentifier() throws Exception {
    validateInput("\"Table Name With Spaces\"", Token.Type.IDENTIFIER);
  }

  @Test
  public void testUnterminatedQuotedIdentifier() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(errorReporter).error(0, 2, "unterminated quoted identifier");
      }
    });
    DefaultScanner.INSTANCE.scanTokens(sourceReaderWith("\""), errorReporter);
  }

  @Test
  public void testNumber() throws Exception {
    final String input = "0123.45e-12";
    validateInput(input, Token.Type.LITERAL);
  }

  private void validateInput(String input, Token.Type expectedType)
      throws IOException {
    final List<Token> tokens = DefaultScanner.INSTANCE.scanTokens(
        sourceReaderWith(input), errorReporter);
    assertThat(tokens.size(), is(equalTo(2)));
    assertThat(tokens.get(0).getType(), is(expectedType));
    assertThat(tokens.get(0).getLexeme(), is(equalTo(input)));
    assertThat(tokens.get(1).getType(), is(Token.Type.EOF));
    assertThat(tokens.get(1).getLexeme().isEmpty(), is(true));
  }

  private SourceReader sourceReaderWith(String s) {
    return new DelegatingSourceReader(new StringReader(s));
  }


}
