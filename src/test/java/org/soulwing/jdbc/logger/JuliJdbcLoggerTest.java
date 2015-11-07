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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.jdbc.JUnitRuleClassImposterizingMockery;
import org.soulwing.jdbc.Parameter;

/**
 * Unit tests for {@link JuliJdbcLogger}.
 *
 * @author Carl Harris
 */
public class JuliJdbcLoggerTest {

  private static final String SQL = "some SQL text";

  private static final String PARAMETER_TEXT_0 = "parameter text 0";

  private static final String PARAMETER_TEXT_1 = "parameter text 1";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleClassImposterizingMockery();

  @Mock
  private Logger delegate;

  @Mock
  private Parameter parameter0;

  @Mock
  private Parameter parameter1;

  private JuliJdbcLogger logger;

  @Before
  public void setUp() throws Exception {
    logger = new JuliJdbcLogger(delegate);
  }

  @Test
  public void testWriteStatement() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(delegate).isLoggable(Level.FINE);
        will(returnValue(true));
        oneOf(delegate).fine(SQL);
      }
    });

    logger.writeStatement(SQL);
  }

  @Test
  public void testWriteStatementFiltered() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(delegate).isLoggable(Level.FINE);
        will(returnValue(false));
      }
    });

    logger.writeStatement(SQL);
  }

  @Test
  public void testWriteParameters() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(delegate).isLoggable(Level.FINEST);
        will(returnValue(true));
        oneOf(parameter0).toString(0);
        will(returnValue(PARAMETER_TEXT_0));
        oneOf(delegate).finest(PARAMETER_TEXT_0);
        oneOf(parameter1).toString(1);
        will(returnValue(PARAMETER_TEXT_1));
        oneOf(delegate).finest(PARAMETER_TEXT_1);
      }
    });

    logger.writeParameters(new Parameter[] { parameter0, parameter1 });
  }

  @Test
  public void testWriteParametersFiltered() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(delegate).isLoggable(Level.FINEST);
        will(returnValue(false));
      }
    });

    logger.writeParameters(new Parameter[] { parameter0, parameter1 });
  }

}
