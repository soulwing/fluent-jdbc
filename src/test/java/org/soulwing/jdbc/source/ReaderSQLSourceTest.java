/*
 * File created on Aug 8, 2015
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

/**
 * Tests for {@link ReaderSQLSource}.
 *
 * @author Carl Harris
 */
public class ReaderSQLSourceTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Test
  public void testBeginTransaction() throws Exception {
    final String input = "BEGIN TRANSACTION";
    validateInput(input);
  }

  @Test
  public void testBlock() throws Exception {
    final String input = "BEGIN something END";
    validateInput(input);
  }

  @Test
  public void testBlockWithNestedBlock() throws Exception {
    final String input = "BEGIN something BEGIN something else END END";
    validateInput(input);
  }

  @Test
  public void testBlockWithDeeplyNestedBlock() throws Exception {
    final String input = "BEGIN BEGIN BEGIN BEGIN END END END END";
    validateInput(input);
  }

  @Test
  public void testBlockWithNestedEndIf() throws Exception {
    final String input = "BEGIN END IF END";
    validateInput(input);
  }

  @Test
  public void testBlockWithNestedEndLoop() throws Exception {
    final String input = "BEGIN END LOOP END";
    validateInput(input);
  }

  @Test
  public void testBlockWithNestedEndCase() throws Exception {
    final String input = "BEGIN END CASE END";
    validateInput(input);
  }

  private void validateInput(String input) {
    final StringReader reader = new StringReader(input);
    ReaderSQLSource source = new ReaderSQLSource(reader, DefaultScanner.INSTANCE);
    assertThat(source.next(), is(equalTo(input)));
  }

  @Test
  public void testReadUTF8Source() throws Exception {
    ReaderSQLSource source = new ReaderSQLSource(
        resourceReader("testSource.sql", "UTF-8"));
    validateStatementSequence(source);
    source.close();
  }

  @Test
  public void testReadUTF16Source() throws Exception {
    ReaderSQLSource source = new ReaderSQLSource(
        resourceReader("testSource-UTF16.sql", "UTF-16"));
    validateStatementSequence(source);
    source.close();
  }

  @Test
  public void testReadSourceWithNoTerminator() throws Exception {
    ReaderSQLSource source = new ReaderSQLSource(
        resourceReader("testSourceWithNoTerminator.sql", "UTF-8"));
    assertThat(source.next(), is(equalTo(
        "CREATE TABLE foo (text VARCHAR(255))")));
    assertThat(source.next(), is(nullValue()));
    source.close();
  }


  private Reader resourceReader(String name, String encoding)
      throws IOException {
    URL location = getClass().getResource(name);
    if (location == null) {
      throw new FileNotFoundException(name);
    }
    return new BufferedReader(new
        InputStreamReader(location.openStream(), encoding));
  }

  private void validateStatementSequence(ReaderSQLSource source) {
    assertThat(source.next(), is(equalTo(
        "CREATE TABLE foo (\n  text VARCHAR(255)\n)")));
    assertThat(source.next(), is(equalTo(
        "INSERT INTO foo VALUES('bar')")));
    assertThat(source.next(), is(equalTo(
        "SELECT *\nFROM foo")));
    assertThat(source.next(), is(equalTo(
        "DROP TABLE foo")));
    assertThat(source.next(), is(nullValue()));
  }

}
