/*
 * File created on May 5, 2014 
 *
 * Copyright (c) 2014 Carl Harris, Jr
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
 *
 */
package org.soulwing.sql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

/**
 * An executor that executes {@link CallableStatement} objects.
 *
 * @author Carl Harris
 */
class CallableStatementExecutor implements SQLExecutor<Boolean>, CallResult {

  private final String sql;
  private final List<Parameter> parameters;
  private Connection connection;
  private CallableStatement call;

  /**
   * Constructs a new instance.
   * @param sql SQL statement to execute
   * @param parameters parameters for the statement
   */
  public CallableStatementExecutor(String sql, List<Parameter> parameters) {
    this.sql = sql;
    this.parameters = parameters;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Boolean execute(DataSource dataSource) throws SQLException {
    this.connection = dataSource.getConnection();
    this.call = connection.prepareCall(sql);
    int index = 1;
    for (Parameter parameter : parameters) {
      parameter.inject(index++, call);
    }

    return call.execute();
  }

  @Override
  public int getUpdateCount() {
    try {
      return call.getUpdateCount();
    }
    catch (SQLException ex) {
      throw new SQLRuntimeException(ex);
    }
  }

  @Override
  public boolean getMoreResults() {
    try {
      return call.getMoreResults();
    }
    catch (SQLException ex) {
      throw new SQLRuntimeException(ex);
    }
  }

  @Override
  public ResultSet getResultSet() {
    try {
      return call.getResultSet();
    }
    catch (SQLException ex) {
      throw new SQLRuntimeException(ex);
    }
  }

  @Override
  public <T> T extractResultSet(ResultSetHandler<T> extractor) {
    ResultSet rs = null;
    try {
      rs = call.getResultSet();
      return extractor.handleResult(rs);
    }
    catch (SQLException ex) {
      throw new SQLRuntimeException(ex);
    }
    finally {
      SQLUtils.closeQuietly(rs);
    }
  }

  @Override
  public <T> List<T> mapResultSet(RowMapper<T> rowMapper) {
    return extractResultSet(new MultipleRowHandler<>(
        new RowMappingResultSetHandler<>(rowMapper)));
  }

  @Override
  public <T> T get(ColumnExtractor<T> columnExtractor) {
    return extractResultSet(new SingleRowHandler<>(
        new ColumnExtractingResultSetHandler<>(columnExtractor)));
  }

  @Override
  public <T> T get(RowMapper<T> rowMapper) {
    return extractResultSet(new SingleRowHandler<>(
        new RowMappingResultSetHandler<>(rowMapper)));
  }

  @Override
  public List<Parameter> getOutParameters()  {
    try {
      List<Parameter> returnValues = new ArrayList<>();
      int index = 1;
      for (Parameter parameter : parameters) {
        if (parameter.isOut()) {
          returnValues.add(Parameter.with(parameter.getType(),
              call.getObject(index)));
        }
        index++;
      }
      return returnValues;
    }
    catch (SQLException ex) {
      throw new SQLRuntimeException(ex);
    }
  }

  @Override
  public void close() {
    if (call == null) return;
    SQLUtils.closeQuietly(call);
    SQLUtils.closeQuietly(connection);
  }

}
