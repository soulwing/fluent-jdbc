/*
 * File created on Aug 5, 2015
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
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

/**
 * An abstract base for executing {@link PreparedStatement} objects.
 *
 * @author Carl Harris
 */
abstract class AbstractPreparedStatementExecutor<T, E extends PreparedStatement>
    implements JdbcExecutor<T> {

  private final PreparedStatementCreator<E> psc;
  private final List<Parameter> parameters;

  private E statement;

  public AbstractPreparedStatementExecutor(PreparedStatementCreator<E> psc,
      List<Parameter> parameters) {
    this.psc = psc;
    this.parameters = parameters;
  }

  @Override
  public T execute(DataSource dataSource) throws SQLException {
    statement = psc.prepareStatement(dataSource);
    int index = 1;
    for (Parameter parameter : parameters) {
      parameter.inject(index++, statement);
    }
    return doExecute(statement);
  }

  /**
   * Gets the statement that was prepared and executed
   * @return statement
   * @throws IllegalStateException if the statement has not been executed
   */
  public E getStatement() {
    if (statement == null) {
      throw new IllegalStateException("no statement has been executed");
    }
    return statement;
  }

  /**
   * Gets the list of statement parameters.
   * @return parameters
   */
  public List<Parameter> getParameters() {
    return parameters;
  }

  /**
   * Executes the given statement.
   * @param statement the statement to execute
   * @return return value from statement execution
   * @throws SQLException
   */
  protected abstract T doExecute(E statement) throws SQLException;

}
