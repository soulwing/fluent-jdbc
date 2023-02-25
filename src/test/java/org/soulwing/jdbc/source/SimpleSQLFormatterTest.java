/*
 * File created on Feb 25, 2023
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

import org.junit.Test;

/**
 * Unit tests for {@link SimpleSQLFormatter}.
 *
 * @author Carl Harris
 */
public class SimpleSQLFormatterTest {

  private final SimpleSQLFormatter formatter = new SimpleSQLFormatter();

  @Test
  public void testFormatNormalizeWhitespace() throws Exception {
    assertThat(formatter.format("  CREATE TABLE a_table (\n  a_column VARCHAR(3)\n)  \n"),
        is(equalTo("CREATE TABLE a_table ( a_column VARCHAR(3) )")));
  }

  @Test
  public void testFormatRemoveLineComment() throws Exception {
    assertThat(formatter.format("CREATE TABLE a_table (a_column VARCHAR(3)) -- line comment"),
        is(equalTo("CREATE TABLE a_table (a_column VARCHAR(3))")));
  }


  @Test
  public void testFormatRemoveBlockComment() throws Exception {
    assertThat(formatter.format("/* a block comment*/   CREATE TABLE a_table (a_column VARCHAR(3))"),
        is(equalTo("CREATE TABLE a_table (a_column VARCHAR(3))")));
  }

}