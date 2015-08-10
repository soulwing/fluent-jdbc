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

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Rule;

/**
 * An abstract base for tests of {@link AbstractPreparedStatementExecutor}
 * subtypes.
 *
 * @author Carl Harris
 */
public abstract class AbstractPreparedStatementExecutorTest
    <T, E extends PreparedStatement> {

  protected abstract AbstractPreparedStatementExecutor<T, E> newExecutor(
      PreparedStatementCreator<E> psc, List<Parameter> parameters);

  protected abstract Expectations doExecuteExpectations() throws Exception;

  protected T validateExecute(Mockery context, final E statement) throws Exception {
    final Parameter parameter1 = context.mock(Parameter.class, "parameter1");
    final Parameter parameter2 = context.mock(Parameter.class, "parameter2");
    final DataSource dataSource = context.mock(DataSource.class);
    final PreparedStatementCreator psc =
        context.mock(PreparedStatementCreator.class);

    final List<Parameter> parameters = new ArrayList<>();
    parameters.add(parameter1);
    parameters.add(parameter2);

    context.checking(doExecuteExpectations());
    context.checking(new Expectations() {
      {
        oneOf(psc).prepareStatement(dataSource);
        will(returnValue(statement));
        for (int i = 0; i < parameters.size(); i++) {
          oneOf(parameters.get(i)).inject(i + 1, statement);
        }
      }
    });

    return newExecutor(psc, parameters).execute(dataSource);
  }

}
