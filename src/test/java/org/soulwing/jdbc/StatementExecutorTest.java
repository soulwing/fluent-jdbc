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

import java.sql.PreparedStatement;

import javax.sql.DataSource;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.jdbc.logger.JdbcLogger;

/**
 * Unit tests for {@link StatementExecutor}.
 *
 * @author Carl Harris
 */
public class StatementExecutorTest {

  private static final String TEXT = "some statement text";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private DataSource dataSource;

  @Mock
  private PreparedStatement statement;

  @Mock
  private JdbcLogger logger;

  @Mock
  private PreparedStatementCreator psc;

  @Test
  public void testExecute() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(psc).getStatementText();
        will(returnValue(TEXT));
        oneOf(logger).writeStatement(TEXT);
        oneOf(psc).prepareStatement(dataSource);
        will(returnValue(statement));
        oneOf(statement).execute();

      }
    });

    StatementExecutor executor = new StatementExecutor(psc, logger);
    executor.execute(dataSource);
  }

}
