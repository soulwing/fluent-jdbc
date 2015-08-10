/*
 * File created on Aug 9, 2015
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
package org.soulwing.jdbc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.jdbc.source.SQLSource;

/**
 * Unit tests for {@link SourceUtils}.
 *
 * @author Carl Harris
 */
public class SourceUtilsTest {

  private static final String STATEMENT = "some statement";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private SQLSource source;

  @Test
  public void testGetSingleStatement() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(source).next();
        will(returnValue(STATEMENT));
        oneOf(source).close();
      }
    });

    assertThat(SourceUtils.getSingleStatement(source), is(equalTo(STATEMENT)));
  }

  @Test(expected =  SQLNullStatementException.class)
  public void testGetSingleStatementWhenNoStatement() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(source).next();
        will(returnValue(null));
        oneOf(source).close();
      }
    });

    SourceUtils.getSingleStatement(source);
  }


}
