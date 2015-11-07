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

import java.io.PrintWriter;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.jdbc.JUnitRuleClassImposterizingMockery;
import org.soulwing.jdbc.Parameter;

/**
 * Unit tests for {@link PrintWriterJdbcLogger}.
 *
 * @author Carl Harris
 */
public class PrintWriterJdbcLoggerTest {

  private static final String SQL = "some SQL text";

  private static final String PARAMETER_TEXT_0 = "parameter text 0";

  private static final String PARAMETER_TEXT_1 = "parameter text 1";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleClassImposterizingMockery();

  @Mock
  private PrintWriter delegate;

  @Mock
  private Parameter parameter0;

  @Mock
  private Parameter parameter1;

  private PrintWriterJdbcLogger logger;

  @Before
  public void setUp() throws Exception {
    logger = new PrintWriterJdbcLogger(delegate, true);
  }

  @Test
  public void testWriteStatement() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(delegate).println(SQL);
        oneOf(delegate).flush();
      }
    });

    logger.writeStatement(SQL);
  }

  @Test
  public void testWriteParameters() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(parameter0).toString(0);
        will(returnValue(PARAMETER_TEXT_0));
        oneOf(delegate).println(PARAMETER_TEXT_0);
        oneOf(parameter1).toString(1);
        will(returnValue(PARAMETER_TEXT_1));
        oneOf(delegate).println(PARAMETER_TEXT_1);
        exactly(2).of(delegate).flush();
      }
    });

    logger.writeParameters(new Parameter[] { parameter0, parameter1 });
  }

}
