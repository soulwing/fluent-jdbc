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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.soulwing.jdbc.logger.JdbcLogger;
import org.soulwing.jdbc.source.SQLSource;

/**
 * A concrete {@link JdbcQuery} implementation.
 *
 * @author Carl Harris
 */
class QueryBuilder<T> implements JdbcQuery<T> {

  private final Class<T> type;
  private final DataSource dataSource;
  private final JdbcLogger logger;

  private PreparedStatementCreator<PreparedStatement> psc;
  private ResultSetHandler<T> handler;
  private ResultSetHandler<T> innerHandler;
  private boolean repeatable;
  private boolean executed;

  /**
   * Constructs a new instance.
   * @param type data type returned by this query
   * @param dataSource data source from which a connection will be obtained
   * @param logger statement logger
   */
  public QueryBuilder(Class<T> type, DataSource dataSource, JdbcLogger logger) {
    this.type = type;
    this.dataSource = dataSource;
    this.logger = logger;
  }

  @Override
  public JdbcQuery<T> using(String sql) {
    assertNotExecuted();
    this.psc = StatementPreparer.with(sql);
    return this;
  }

  @Override
  public JdbcQuery<T> using(SQLSource source) {
    assertNotExecuted();
    return using(SourceUtils.getSingleStatement(source));
  }

  @Override
  public JdbcQuery<T> handlingResultWith(ResultSetHandler<T> handler) {
    assertNotExecuted();
    this.handler = handler;
    this.innerHandler = null;
    return this;
  }

  @Override
  public JdbcQuery<T> extractingColumn() {
    return extractingColumn(1);
  }

  @Override
  public JdbcQuery<T> extractingColumn(int index) {
    assertNotExecuted();
    this.handler = null;
    this.innerHandler = new ColumnExtractingResultSetHandler<>(
        ColumnExtractor.with(index, type));
    return this;
  }

  @Override
  public JdbcQuery<T> extractingColumn(String label) {
    assertNotExecuted();
    this.handler = null;
    this.innerHandler = new ColumnExtractingResultSetHandler<>(
        ColumnExtractor.with(label, type));
    return this;
  }

  @Override
  public JdbcQuery<T> mappingRowsWith(RowMapper<T> rowMapper) {
    assertNotExecuted();
    this.handler = null;
    this.innerHandler = new RowMappingResultSetHandler<>(rowMapper);
    return this;
  }

  @Override
  public JdbcQuery<T> repeatedly() {
    assertNotExecuted();
    this.repeatable = true;
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<T> retrieveList(Parameter... parameters) {
    return (List<T>) retrieve(handler != null ?
        handler : new MultipleRowHandler<>(innerHandler), parameters);
  }

  @Override
  @SuppressWarnings("unchecked")
  public T retrieveValue(Parameter... parameters) {
    return (T) retrieve(handler != null ?
        handler : new SingleRowHandler<>(innerHandler), parameters);
  }

  @Override
  public void execute(Parameter... parameters) {
    retrieve(handler != null ? handler : new MultipleRowHandler<>(innerHandler),
        parameters);
  }

  /**
   * Execute the query and retrieve the result.
   * <p>
   * If this builder is configured for repeated execution, the JDBC connection
   * and statement resources will remain open when this method returns.
   *
   * @param handler result handler that will produce the result
   * @param params values for statement placeholders
   * @return result produced by {@code handler} for the {@link ResultSet}
   *    returned by the query execution
   */
  public Object retrieve(ResultSetHandler<?> handler, Parameter... params) {
    assertReady();
    final PreparedQueryExecutor executor =
        new PreparedQueryExecutor(psc, params, logger);

    ResultSet rs = null;
    try {
      rs = executor.execute(dataSource);
      return handler.handleResult(rs);
    }
    catch (SQLException ex) {
      throw new SQLRuntimeException(ex);
    }
    finally {
      executed = true;
      JdbcUtils.closeQuietly(rs);
      if (!repeatable) {
        close();
      }
    }
  }

  @Override
  public void close() {
    JdbcUtils.closeQuietly(psc);
  }

  private void assertReady() {
    if (executed && !repeatable) {
      throw new IllegalStateException(
          "query has been executed and was not configured as repeatable");
    }
    if (psc == null) {
      throw new IllegalArgumentException(
          "no SQL statement or source has been configured");
    }
    if (handler == null && innerHandler == null) {
      throw new IllegalArgumentException(
          "no result handler, column extractor, or row mapper has been configured");
    }
  }

  private void assertNotExecuted() {
    if (executed) {
      throw new IllegalStateException(
          "query cannot be reconfigured after is has been executed");
    }
  }

}
