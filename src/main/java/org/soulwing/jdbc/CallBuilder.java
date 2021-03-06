/*
 * File created on Aug 10, 2015
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

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.soulwing.jdbc.logger.JdbcLogger;

/**
 * A concrete {@link JdbcCall} implementation.
 *
 * @author Carl Harris
 */
class CallBuilder implements JdbcCall {

  private final DataSource dataSource;
  private final PreparedStatementCreator<CallableStatement> psc;
  private final JdbcLogger logger;

  private CallableStatementExecutor executor;

  /**
   * Constructs a new instance.
   * @param dataSource dataSource from which connections will be obtained as
   *    needed
   * @param psc prepared statement creator for the call
   * @param logger statement logger
   */
  public CallBuilder(DataSource dataSource,
      PreparedStatementCreator<CallableStatement> psc, JdbcLogger logger) {
    this.dataSource = dataSource;
    this.psc = psc;
    this.logger = logger;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean execute(Parameter... parameters) {
    executor = new CallableStatementExecutor(psc, parameters, logger);
    try {
      return executor.execute(dataSource);
    }
    catch (SQLException ex) {
      throw new SQLRuntimeException(ex);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void close() {
    JdbcUtils.closeQuietly(psc);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getUpdateCount() {
    try {
      return getExecutor().getStatement().getUpdateCount();
    }
    catch (SQLException ex) {
      throw new SQLRuntimeException(ex);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean getMoreResults() {
    try {
      return getExecutor().getStatement().getMoreResults();
    }
    catch (SQLException ex) {
      throw new SQLRuntimeException(ex);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ResultSet getResultSet() {
    try {
      return getExecutor().getStatement().getResultSet();
    }
    catch (SQLException ex) {
      throw new SQLRuntimeException(ex);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> List<T> retrieveList(String columnLabel, Class<T> type) {
    return handleResult(new MultipleRowHandler<>(
        new ColumnExtractingResultSetHandler<>(
            ColumnExtractor.with(columnLabel, type))));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> List<T> retrieveList(int columnIndex, Class<T> type) {
    return handleResult(new MultipleRowHandler<>(
        new ColumnExtractingResultSetHandler<>(
            ColumnExtractor.with(columnIndex, type))));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> List<T> retrieveList(RowMapper<T> rowMapper) {
    return handleResult(new MultipleRowHandler<>(
        new RowMappingResultSetHandler<>(rowMapper)));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T retrieveValue(String columnLabel, Class<T> type) {
    return handleResult(new SingleRowHandler<>(
        new ColumnExtractingResultSetHandler<>(
            ColumnExtractor.with(columnLabel, type))));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T retrieveValue(int columnIndex, Class<T> type) {
    return handleResult(new SingleRowHandler<>(
        new ColumnExtractingResultSetHandler<>(
            ColumnExtractor.with(columnIndex, type))));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T retrieveValue(RowMapper<T> rowMapper) {
    return handleResult(new SingleRowHandler<>(
        new RowMappingResultSetHandler<>(rowMapper)));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T handleResult(ResultSetHandler<T> handler) {
    ResultSet rs = null;
    try {
      rs = getExecutor().getStatement().getResultSet();
      if (rs == null) {
        throw new IllegalStateException("no result set is available");
      }
      return handler.handleResult(rs);
    }
    catch (SQLException ex) {
      throw new SQLRuntimeException(ex);
    }
    finally {
      JdbcUtils.closeQuietly(rs);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T getOutParameter(int parameterIndex, Class<T> type) {
    try {
      return CallableStatementAccessor.with(getExecutor().getStatement())
          .get(parameterIndex, type);
    }
    catch (SQLException ex) {
      throw new SQLRuntimeException(ex);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T getOutParameter(String parameterName, Class<T> type) {
    try {
      return CallableStatementAccessor.with(getExecutor().getStatement())
          .get(parameterName, type);
    }
    catch (SQLException ex) {
      throw new SQLRuntimeException(ex);
    }
  }

  private CallableStatementExecutor getExecutor() {
    if (executor == null) {
      throw new IllegalStateException("call has not been executed");
    }
    return executor;
  }

}
