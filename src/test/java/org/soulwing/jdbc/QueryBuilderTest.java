/*
 * File created on Aug 11, 2015
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
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.jdbc.logger.JdbcLogger;
import org.soulwing.jdbc.source.SQLSource;

/**
 * Unit tests for {@link QueryBuilder}.
 *
 * @author Carl Harris
 */
public class QueryBuilderTest {

  private static final String SQL = "some SQL";

  private static final Object RESULT = new Object();

  private static final int COLUMN_INDEX = -1;

  private static final String COLUMN_LABEL = "someLabel";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private DataSource dataSource;

  @Mock
  private Connection connection;

  @Mock
  private PreparedStatement statement;

  @Mock
  private ResultSet resultSet;

  @Mock
  private SQLSource source;

  @Mock
  private ResultSetHandler<Object> handler;

  @Mock
  private RowMapper<Object> rowMapper;

  @Mock
  private JdbcLogger logger;

  private QueryBuilder<Object> query;

  @Before
  public void setUp() throws Exception {
    query = new QueryBuilder<>(Object.class, dataSource, logger);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRetrieveFailsWhenNoStatementConfigured() throws Exception {
    query.handlingResultWith(handler);
    query.retrieve(handler);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRetrieveFailsWhenNoHandlerConfigured() throws Exception {
    query.using(SQL);
    query.retrieve(handler);
  }

  @Test
  public void testRetrieveClosesWhenNotRepeatable() throws Exception {
    context.checking(prepareStatementExpectations());
    context.checking(executeStatementExpectations());
    context.checking(handlerExpectations());
    context.checking(closeExpectations());

    query.using(SQL).handlingResultWith(handler);
    query.retrieve(handler);

    try {
      query.retrieve(handler);
      fail("expected IllegalStateException");
    }
    catch (IllegalStateException ex) {
      assert true;
    }
  }

  @Test
  public void testCannotReconfigureAfterRetrieve() throws Exception {
    context.checking(prepareStatementExpectations());
    context.checking(executeStatementExpectations());
    context.checking(handlerExpectations());
    context.checking(closeExpectations());

    query.using(SQL).handlingResultWith(handler);
    query.retrieve(handler);

    validateThrowsIllegalStateException(new Runnable() {
      public void run() {
        query.using(SQL);
      }
    });

    validateThrowsIllegalStateException(new Runnable() {
      public void run() {
        query.using(source);
      }
    });

    validateThrowsIllegalStateException(new Runnable() {
      public void run() {
        query.handlingResultWith(handler);
      }
    });

    validateThrowsIllegalStateException(new Runnable() {
      public void run() {
        query.extractingColumn();
      }
    });

    validateThrowsIllegalStateException(new Runnable() {
      public void run() {
        query.extractingColumn(1);
      }
    });

    validateThrowsIllegalStateException(new Runnable() {
      public void run() {
        query.extractingColumn("label");
      }
    });

    validateThrowsIllegalStateException(new Runnable() {
      public void run() {
        query.mappingRowsWith(null);
      }
    });

    validateThrowsIllegalStateException(new Runnable() {
      public void run() {
        query.repeatedly();
      }
    });
  }

  private void validateThrowsIllegalStateException(Runnable runnable) {
    try {
      runnable.run();
      fail("expected IllegalStateException");
    }
    catch (IllegalStateException ex) {
      assert true;
    }
  }

  @Test
  public void testRetrieveDoesNotCloseWhenRepeatable() throws Exception {
    context.checking(prepareStatementExpectations());

    context.checking(executeStatementExpectations());
    context.checking(executeStatementExpectations());

    context.checking(handlerExpectations());
    context.checking(handlerExpectations());

    query.using(SQL).handlingResultWith(handler).repeatedly();
    query.retrieve(handler);
    query.retrieve(handler);
  }

  @Test
  public void testRetrieveListWithRowMapper() throws Exception {
    context.checking(prepareStatementExpectations());
    context.checking(executeStatementExpectations());
    context.checking(closeExpectations());
    context.checking(new Expectations() {
      {
        exactly(2).of(resultSet).next();
        will(onConsecutiveCalls(returnValue(true), returnValue(false)));
        oneOf(rowMapper).mapRow(resultSet, 1);
        will(returnValue(RESULT));
        oneOf(resultSet).close();
      }
    });

    assertThat(query.using(SQL).mappingRowsWith(rowMapper).retrieveList(),
        contains(RESULT));
  }

  @Test
  public void testRetrieveValueWithRowMapper() throws Exception {
    context.checking(prepareStatementExpectations());
    context.checking(executeStatementExpectations());
    context.checking(closeExpectations());
    context.checking(new Expectations() {
      {
        exactly(2).of(resultSet).next();
        will(onConsecutiveCalls(returnValue(true), returnValue(false)));
        oneOf(rowMapper).mapRow(resultSet, 1);
        will(returnValue(RESULT));
        oneOf(resultSet).close();
      }
    });

    assertThat(query.using(SQL).mappingRowsWith(rowMapper).retrieveValue(),
        is(sameInstance(RESULT)));
  }

  @Test
  public void testRetrieveListWithColumnIndexExtractor() throws Exception {
    context.checking(prepareStatementExpectations());
    context.checking(executeStatementExpectations());
    context.checking(closeExpectations());
    context.checking(new Expectations() {
      {
        exactly(2).of(resultSet).next();
        will(onConsecutiveCalls(returnValue(true), returnValue(false)));
        oneOf(resultSet).getObject(COLUMN_INDEX, Object.class);
        will(returnValue(RESULT));
        oneOf(resultSet).close();
      }
    });

    assertThat(query.using(SQL).extractingColumn(COLUMN_INDEX).retrieveList(),
        contains(RESULT));
  }

  @Test
  public void testRetrieveListWithColumnLabelExtractor() throws Exception {
    context.checking(prepareStatementExpectations());
    context.checking(executeStatementExpectations());
    context.checking(closeExpectations());
    context.checking(new Expectations() {
      {
        exactly(2).of(resultSet).next();
        will(onConsecutiveCalls(returnValue(true), returnValue(false)));
        oneOf(resultSet).getObject(COLUMN_LABEL, Object.class);
        will(returnValue(RESULT));
        oneOf(resultSet).close();
      }
    });

    assertThat(query.using(SQL).extractingColumn(COLUMN_LABEL).retrieveList(),
        contains(RESULT));
  }

  @Test
  public void testCloseWhenRepeatable() throws Exception {
    context.checking(prepareStatementExpectations());
    context.checking(executeStatementExpectations());
    context.checking(handlerExpectations());
    context.checking(closeExpectations());

    query.using(SQL).handlingResultWith(handler).repeatedly();
    query.retrieve(handler);
    query.close();
  }

  private Expectations prepareStatementExpectations() throws Exception {
    return new Expectations() {
      {
        oneOf(dataSource).getConnection();
        will(returnValue(connection));
        oneOf(connection).prepareStatement(with(SQL));
        will(returnValue(statement));
      }
    };
  }

  private Expectations executeStatementExpectations(
      final Parameter... parameters) throws Exception {
    return new Expectations() {
      {
        for (int index = 0; index < parameters.length; index++) {
          oneOf(statement).setObject(index, parameters[index].getValue(),
              parameters[index].getType());
        }
        oneOf(logger).writeStatement(SQL);
        oneOf(logger).writeParameters(with(any(Parameter[].class)));
        oneOf(statement).executeQuery();
        will(returnValue(resultSet));
      }
    };
  }

  private Expectations handlerExpectations()
      throws Exception {
    return new Expectations() {
      {
        oneOf(handler).handleResult(resultSet);
        will(returnValue(RESULT));
        oneOf(resultSet).close();
      }
    };
  }

  private Expectations closeExpectations() throws Exception {
    return new Expectations() {
      {
        oneOf(statement).close();
        oneOf(connection).close();
      }
    };
  }

}
