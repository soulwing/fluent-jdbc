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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.fail;

import java.sql.CallableStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Unit tests for {@link CallBuilder}.
 *
 * @author Carl Harris
 */
public class CallBuilderTest {

  private static final String LABEL = "some label";

  private static final int INDEX = -1;

  private static final String SQL = "some SQL";

  private static final boolean EXECUTE_RESULT = true;

  private static final int UPDATE_COUNT = -1;

  private static final Object RESULT = new Object();

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private PreparedStatementCreator<CallableStatement> psc;

  @Mock
  private DataSource dataSource;

  @Mock
  private CallableStatement statement;

  @Mock
  private ResultSetHandler<?> handler;

  @Mock
  private RowMapper<?> rowMapper;

  @Mock
  private ResultSet resultSet;

  private CallBuilder call;

  @Before
  public void setUp() throws Exception {
    call = new CallBuilder(dataSource, psc);
  }

  @Test
  public void testExecuteAndClose() throws Exception {
    context.checking(executeExpectations(EXECUTE_RESULT));
    context.checking(closeExpectations());
    assertThat(call.execute(), is(equalTo(EXECUTE_RESULT)));
    call.close();
  }

  @Test(expected = IllegalStateException.class)
  public void testGetUpdateCountFailsWhenNotExecuted() throws Exception {
    call.getUpdateCount();
  }

  @Test(expected = IllegalStateException.class)
  public void testGetResultSetFailsWhenNotExecuted() throws Exception {
    call.getResultSet();
  }

  @Test(expected = IllegalStateException.class)
  public void testGetMoreResultsFailsWhenNotExecuted() throws Exception {
    call.getMoreResults();
  }

  @Test(expected = IllegalStateException.class)
  public void testHandleResultFailsWhenNotExecuted() throws Exception {
    call.handleResult(handler);
  }

  @Test(expected = IllegalStateException.class)
  public void testRetrieveListRowMapperFailsWhenNotExecuted() throws Exception {
    call.retrieveList(rowMapper);
  }

  @Test(expected = IllegalStateException.class)
  public void testRetrieveListLabelFailsWhenNotExecuted() throws Exception {
    call.retrieveList(LABEL, Object.class);
  }

  @Test(expected = IllegalStateException.class)
  public void testRetrieveListIndexFailsWhenNotExecuted() throws Exception {
    call.retrieveList(INDEX, Object.class);
  }

  @Test(expected = IllegalStateException.class)
  public void testGetOutParameterLabelFailsWhenNotExecuted() throws Exception {
    call.getOutParameter(LABEL, Object.class);
  }

  @Test(expected = IllegalStateException.class)
  public void testGetOutParameterIndexFailsWhenNotExecuted() throws Exception {
    call.getOutParameter(INDEX, Object.class);
  }

  @Test
  public void testGetUpdateCount() throws Exception {
    context.checking(executeExpectations(EXECUTE_RESULT));
    context.checking(new Expectations() {
      {
        oneOf(statement).getUpdateCount();
        will(returnValue(UPDATE_COUNT));
      }
    });

    call.execute();
    assertThat(call.getUpdateCount(), is(equalTo(UPDATE_COUNT)));
  }

  @Test
  public void testGetMoreResults() throws Exception {
    context.checking(executeExpectations(EXECUTE_RESULT));
    context.checking(new Expectations() {
      {
        oneOf(statement).getMoreResults();
        will(returnValue(true));
      }
    });

    call.execute();
    assertThat(call.getMoreResults(), is(equalTo(true)));
  }

  @Test
  public void testGetResultSet() throws Exception {
    context.checking(executeExpectations(EXECUTE_RESULT));
    context.checking(new Expectations() {
      {
        oneOf(statement).getResultSet();
        will(returnValue(resultSet));
      }
    });

    call.execute();
    assertThat(call.getResultSet(), is(sameInstance(resultSet)));
  }

  @Test
  public void testGetOutParameterLabel() throws Exception {
    context.checking(executeExpectations(EXECUTE_RESULT));
    context.checking(new Expectations() {
      {
        oneOf(statement).getObject(LABEL, Object.class);
        will(returnValue(RESULT));
      }
    });

    call.execute();
    assertThat(call.getOutParameter(LABEL, Object.class),
        is(sameInstance(RESULT)));
  }

  @Test
  public void testGetOutParameterIndex() throws Exception {
    context.checking(executeExpectations(EXECUTE_RESULT));
    context.checking(new Expectations() {
      {
        oneOf(statement).getObject(INDEX, Object.class);
        will(returnValue(RESULT));
      }
    });

    call.execute();
    assertThat(call.getOutParameter(INDEX, Object.class),
        is(sameInstance(RESULT)));
  }

  @Test(expected = IllegalStateException.class)
  public void testHandleResultWhenNoResult() throws Exception {
    context.checking(executeExpectations(EXECUTE_RESULT));
    context.checking(new Expectations() {
      {
        oneOf(statement).getResultSet();
        will(returnValue(null));
      }
    });

    call.execute();
    call.handleResult(handler);
  }

  @Test
  public void testHandleResult() throws Exception {
    context.checking(executeExpectations(EXECUTE_RESULT));
    context.checking(new Expectations() {
      {
        oneOf(statement).getResultSet();
        will(returnValue(resultSet));
        oneOf(handler).handleResult(resultSet);
        will(returnValue(RESULT));
        oneOf(resultSet).close();
      }
    });

    call.execute();
    assertThat(call.handleResult(handler), is(sameInstance(RESULT)));
  }

  @Test
  public void testRetrieveListWithRowMapper() throws Exception {
    context.checking(executeExpectations(EXECUTE_RESULT));
    context.checking(resultSetExpectations());
    context.checking(new Expectations() {
      {
        oneOf(rowMapper).mapRow(resultSet, 1);
        will(returnValue(RESULT));
        oneOf(resultSet).close();
      }
    });

    call.execute();
    assertThat(call.retrieveList(rowMapper), contains(RESULT));
  }

  @Test
  public void testRetrieveValueWithRowMapper() throws Exception {
    context.checking(executeExpectations(EXECUTE_RESULT));
    context.checking(resultSetExpectations());
    context.checking(new Expectations() {
      {
        oneOf(rowMapper).mapRow(resultSet, 1);
        will(returnValue(RESULT));
        oneOf(resultSet).close();
      }
    });

    call.execute();
    assertThat(call.retrieveValue(rowMapper), is(sameInstance(RESULT)));
  }

  @Test
  public void testRetrieveListWithColumnLabel() throws Exception {
    context.checking(executeExpectations(EXECUTE_RESULT));
    context.checking(resultSetExpectations());
    context.checking(new Expectations() {
      {
        oneOf(resultSet).getObject(LABEL, Object.class);
        will(returnValue(RESULT));
        oneOf(resultSet).close();
      }
    });

    call.execute();
    assertThat(call.retrieveList(LABEL, Object.class), contains(RESULT));
  }

  @Test
  public void testRetrieveListWithColumnIndex() throws Exception {
    context.checking(executeExpectations(EXECUTE_RESULT));
    context.checking(resultSetExpectations());
    context.checking(new Expectations() {
      {
        oneOf(resultSet).getObject(INDEX, Object.class);
        will(returnValue(RESULT));
        oneOf(resultSet).close();
      }
    });

    call.execute();
    assertThat(call.retrieveList(INDEX, Object.class), contains(RESULT));
  }

  @Test
  public void testRetrieveValueWithColumnLabel() throws Exception {
    context.checking(executeExpectations(EXECUTE_RESULT));
    context.checking(resultSetExpectations());
    context.checking(new Expectations() {
      {
        oneOf(resultSet).getObject(LABEL, Object.class);
        will(returnValue(RESULT));
        oneOf(resultSet).close();
      }
    });

    call.execute();
    assertThat(call.retrieveValue(LABEL, Object.class),
        is(sameInstance(RESULT)));
  }

  @Test
  public void testRetrieveValueWithColumnIndex() throws Exception {
    context.checking(executeExpectations(EXECUTE_RESULT));
    context.checking(resultSetExpectations());
    context.checking(new Expectations() {
      {
        oneOf(resultSet).getObject(INDEX, Object.class);
        will(returnValue(RESULT));
        oneOf(resultSet).close();
      }
    });

    call.execute();
    assertThat(call.retrieveValue(INDEX, Object.class),
        is(sameInstance(RESULT)));
  }

  private Expectations resultSetExpectations() throws Exception {
    return new Expectations() {
      {
        oneOf(statement).getResultSet();
        will(returnValue(resultSet));
        exactly(2).of(resultSet).next();
        will(onConsecutiveCalls(returnValue(true), returnValue(false)));
      }
    };
  }

  private Expectations executeExpectations(final boolean executeResult,
      final Parameter... parameters) throws Exception {
    return new Expectations() {
      {
        oneOf(psc).prepareStatement(dataSource);
        will(returnValue(statement));
        for (int index = 0; index < parameters.length; index++) {
          oneOf(statement).setObject(index, parameters[index].getValue(),
              parameters[index].getType());
        }
        oneOf(statement).execute();
        will(returnValue(executeResult));
      }
    };
  }

  private Expectations closeExpectations() throws Exception {
    return new Expectations() {
      {
        oneOf(psc).close();
      }
    };
  }

}
