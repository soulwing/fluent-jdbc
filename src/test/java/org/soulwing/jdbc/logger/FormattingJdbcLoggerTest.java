/*
 * File created on Nov 7, 2015
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
package org.soulwing.jdbc.logger;

import static org.hamcrest.Matchers.sameInstance;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.jdbc.Parameter;
import org.soulwing.jdbc.source.SQLFormatter;

/**
 * Unit tests for {@link FormattingJdbcLogger}.
 *
 * @author Carl Harris
 */
public class FormattingJdbcLoggerTest {

  private static final String SQL = "some SQL text";

  private static final String FORMATTED_SQL = "some formatted SQL text";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private JdbcLogger delegate;

  @Mock
  private SQLFormatter formatter;

  private FormattingJdbcLogger logger;

  @Before
  public void setUp() throws Exception {
    logger = new FormattingJdbcLogger(delegate, formatter);
  }

  @Test
  public void testWriteStatement() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(formatter).format(SQL);
        will(returnValue(FORMATTED_SQL));
        oneOf(delegate).writeStatement(FORMATTED_SQL);
      }
    });
    logger.writeStatement(SQL);
  }

  @Test
  public void testWriteParameters() throws Exception {
    final Parameter[] parameters = new Parameter[0];
    context.checking(new Expectations() {
      {
        oneOf(delegate).writeParameters(with(sameInstance(parameters)));
      }
    });

    logger.writeParameters(parameters);
  }

}
