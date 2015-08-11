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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.sql.DataSource;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.jdbc.source.SQLSource;

/**
 * Unit tests for {@link UpdateBuilder}.
 * @author Carl Harris
 */
public class UpdateBuilderTest {

  private static final String SQL = "some SQL";

  private static final int UPDATE_COUNT = -1;

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

  private UpdateBuilder updater;

  @Before
  public void setUp() throws Exception {
    updater = new UpdateBuilder(dataSource);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExecuteFailsWhenNoStatementConfigured() throws Exception {
    updater.execute();
  }

  @Test
  public void testExecuteClosesWhenNotRepeatable() throws Exception {
    context.checking(prepareStatementExpectations());
    context.checking(executeStatementExpectations(UPDATE_COUNT));
    context.checking(closeExpectations());

    assertThat(updater.using(SQL).execute(), is(equalTo(UPDATE_COUNT)));

    validateThrowsIllegalStateException(new Runnable() {
      public void run() {
        updater.execute();
      }
    });
  }

  @Test
  public void testCannotReconfigureAfterExecute() throws Exception {
    context.checking(prepareStatementExpectations());
    context.checking(executeStatementExpectations(UPDATE_COUNT));
    context.checking(closeExpectations());

    updater.using(SQL).execute();

    validateThrowsIllegalStateException(new Runnable() {
      public void run() {
        updater.using(SQL);
      }
    });

    validateThrowsIllegalStateException(new Runnable() {
      public void run() {
        updater.repeatedly();
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
  public void testExecuteDoesNotCloseWhenRepeatable() throws Exception {
    context.checking(prepareStatementExpectations());

    context.checking(executeStatementExpectations(UPDATE_COUNT));
    context.checking(executeStatementExpectations(UPDATE_COUNT + 1));

    updater.using(SQL).repeatedly();
    assertThat(updater.execute(), is(equalTo(UPDATE_COUNT)));
    assertThat(updater.execute(), is(equalTo(UPDATE_COUNT + 1)));
  }

  @Test
  public void testCloseWhenRepeatable() throws Exception {
    context.checking(prepareStatementExpectations());
    context.checking(executeStatementExpectations(UPDATE_COUNT));
    context.checking(closeExpectations());

    updater.using(SQL).repeatedly().execute();
    updater.close();
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

  private Expectations executeStatementExpectations(final int updateCount,
      final Parameter... parameters) throws Exception {
    return new Expectations() {
      {
        for (int index = 0; index < parameters.length; index++) {
          oneOf(statement).setObject(index, parameters[index].getValue(),
              parameters[index].getType());
        }
        oneOf(statement).executeUpdate();
        will(returnValue(updateCount));
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
