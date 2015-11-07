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

import org.apache.commons.logging.Log;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.jdbc.JUnitRuleClassImposterizingMockery;
import org.soulwing.jdbc.Parameter;

/**
 * Unit tests for {@link JclJdbcLogger}.
 *
 * @author Carl Harris
 */
public class JclJdbcLoggerTest {

  private static final String SQL = "some SQL text";

  private static final String PARAMETER_TEXT_0 = "parameter text 0";

  private static final String PARAMETER_TEXT_1 = "parameter text 1";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleClassImposterizingMockery();

  @Mock
  private Log delegate;

  @Mock
  private Parameter parameter0;

  @Mock
  private Parameter parameter1;

  private JclJdbcLogger logger;

  @Before
  public void setUp() throws Exception {
    logger = new JclJdbcLogger(delegate);
  }

  @Test
  public void testWriteStatement() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(delegate).isDebugEnabled();
        will(returnValue(true));
        oneOf(delegate).debug(SQL);
      }
    });

    logger.writeStatement(SQL);
  }

  @Test
  public void testWriteStatementFiltered() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(delegate).isDebugEnabled();
        will(returnValue(false));
      }
    });

    logger.writeStatement(SQL);
  }

  @Test
  public void testWriteParameters() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(delegate).isTraceEnabled();
        will(returnValue(true));
        oneOf(parameter0).toString(0);
        will(returnValue(PARAMETER_TEXT_0));
        oneOf(delegate).trace(PARAMETER_TEXT_0);
        oneOf(parameter1).toString(1);
        will(returnValue(PARAMETER_TEXT_1));
        oneOf(delegate).trace(PARAMETER_TEXT_1);
      }
    });

    logger.writeParameters(new Parameter[] { parameter0, parameter1 });
  }

  @Test
  public void testWriteParametersFiltered() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(delegate).isTraceEnabled();
        will(returnValue(false));
      }
    });

    logger.writeParameters(new Parameter[] { parameter0, parameter1 });
  }

}
