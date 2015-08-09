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
package org.soulwing.sql;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Rule;
import org.junit.Test;

/**
 * Unit tests for {@link PreparedQueryExecutor}.
 *
 * @author Carl Harris
 */
public class PreparedQueryExecutorTest
    extends AbstractPreparedStatementExecutorTest<ResultSet> {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery() {
    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  @Mock
  private ResultSet resultSet;

  @Override
  protected AbstractPreparedStatementExecutor newExecutor(
      PreparedStatementCreator psc, List<Parameter> parameters) {
    return new PreparedQueryExecutor(psc, parameters);
  }

  @Override
  protected Expectations doExecuteExpectations(
      final PreparedStatement statement) throws Exception {
    return new Expectations() {
      {
        oneOf(statement).executeQuery();
        will(returnValue(resultSet));
      }
    };
  }

  @Test
  public void testExecute() throws Exception {
    assertThat(validateExecute(context), is(sameInstance(resultSet)));
  }

}
