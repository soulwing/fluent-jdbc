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

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.sql.DataSource;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.jdbc.source.SQLSource;

/**
 * Unit tests for {@link StatementPreparer}.
 *
 * @author Carl Harris
 */
public class StatementPreparerTest {

  private static final String SQL = "some SQL";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private DataSource dataSource;

  @Mock
  private Connection connection;

  @Mock
  private PreparedStatement statement;

  @Mock
  private SQLSource source;

  @Test
  public void testWithSQLString() throws Exception {
    context.checking(prepareStatementExpectations());
    StatementPreparer preparer = StatementPreparer.with(SQL);
    preparer.prepareStatement(dataSource);

    // must not re-prepare statement
    preparer.prepareStatement(dataSource);
  }

  @Test
  public void testWithSQLSource() throws Exception {
    context.checking(prepareStatementExpectations());
    context.checking(new Expectations() {
      {
        oneOf(source).next();
        will(returnValue(SQL));
        oneOf(source).close();
      }
    });

    StatementPreparer preparer = StatementPreparer.with(source);
    preparer.prepareStatement(dataSource);

    // must not re-prepare statement
    preparer.prepareStatement(dataSource);
  }

  private Expectations prepareStatementExpectations() throws Exception {
    return new Expectations() {
      {
        oneOf(dataSource).getConnection();
        will(returnValue(connection));
        oneOf(connection).prepareStatement(SQL);
        will(returnValue(statement));
      }
    };
  }

}
