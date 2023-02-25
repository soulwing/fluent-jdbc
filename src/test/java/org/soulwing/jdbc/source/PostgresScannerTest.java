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
import static org.hamcrest.Matchers.containsString;
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
 * Unit tests for {@link PostgresScanner}.
 *
 * @author Carl Harris
 */
public class PostgresScannerTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private ErrorReporter errorReporter;

  @Test
  public void testDollarQuotedStringWithEmptyTag() throws Exception {
    validateInput("$$ Dollar-quoted string ' ' $$", Token.Type.LITERAL);
  }

  @Test
  public void testDollarQuotedStringWithNonEmptyTag() throws Exception {
    validateInput("$tag$ Dollar-quoted string ' ' $tag$", Token.Type.LITERAL);
  }

  @Test
  public void testDollarQuotedStringWithNestedEmptyTag() throws Exception {
    validateInput("$tag$ $$ Nested dollar-quoted string $$ $tag$", Token.Type.LITERAL);
  }

  @Test
  public void testDollarQuotedStringWithNestedNonEmptyTag() throws Exception {
    validateInput("$tag$ $other$ Nested dollar-quoted string $other$ $tag$", Token.Type.LITERAL);
  }

  @Test
  public void testDollarQuotedStringWithMismatchedTag() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(errorReporter).error(with(0), with(any(Integer.class)),
            with(containsString("unterminated")));
      }
    });
    validateInput("$tag$ Dollar quoted string ' ' $otherTag$", Token.Type.LITERAL);
  }

  @Test
  public void testDollarQuotedStringWithBareDollarSign() throws Exception {
    validateInput("$tag$ Dollar quoted string $ $tag$", Token.Type.LITERAL);
  }

  @Test
  public void testDollarQuotedStringWithBareDollarSigns() throws Exception {
    validateInput("$tag$ Dollar quoted string $ $t $ta $tagg $tag$", Token.Type.LITERAL);
  }

  private void validateInput(String input, Token.Type expectedType)
      throws IOException {
    final List<Token> tokens = PostgresScanner.INSTANCE.scanTokens(
        sourceReaderWith(input), errorReporter);
    assertThat(tokens.size(), is(equalTo(2)));
    assertThat(tokens.get(0).getType(), is(expectedType));
    assertThat(tokens.get(0).getLexeme(), is(equalTo(input)));
    assertThat(tokens.get(1).getType(), is(Token.Type.EOF));
  }

  private SourceReader sourceReaderWith(String s) {
    return new DelegatingSourceReader(new StringReader(s));
  }


}
