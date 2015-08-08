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
package org.soulwing.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

/**
 * An abstract base for executing {@link PreparedStatement} objects.
 *
 * @author Carl Harris
 */
abstract class AbstractPreparedStatementExecutor<T> implements SQLExecutor<T> {

  private final PreparedStatementCreator psc;
  private final List<Parameter> parameters;


  public AbstractPreparedStatementExecutor(PreparedStatementCreator psc,
      List<Parameter> parameters) {
    this.psc = psc;
    this.parameters = parameters;
  }

  @Override
  public T execute(DataSource dataSource) throws SQLException {
    PreparedStatement statement = psc.getPreparedStatement();
    if (statement == null) {
      statement = psc.prepareStatement(dataSource.getConnection());
    }
    int index = 1;
    for (Parameter parameter : parameters) {
      parameter.inject(index++, statement);
    }
    return doExecute(statement);
  }

  @Override
  public void close() throws SQLException {
    psc.close();
  }

  protected abstract T doExecute(PreparedStatement statement)
      throws SQLException;
}
