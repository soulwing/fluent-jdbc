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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.soulwing.sql.source.SQLSource;

/**
 * A concrete {@link SQLQuery} implementation.
 *
 * @author Carl Harris
 */
class QueryBuilder<T> implements SQLQuery<T> {

  private final Class<T> type;
  private final DataSource dataSource;

  private PreparedStatementCreator psc;
  private ResultSetHandler<T> handler;
  private ResultSetHandler<T> innerHandler;
  private boolean repeatable;
  private boolean executed;

  /**
   * Constructs a new instance.
   * @param type data type returned by this query
   * @param dataSource data source from which a connection will be obtained
   *
   */
  public QueryBuilder(Class<T> type, DataSource dataSource) {
    this.type = type;
    this.dataSource = dataSource;
  }

  @Override
  public SQLQuery<T> using(String sql) {
    assertNotExecuted();
    this.psc = StatementPreparer.with(sql);
    return this;
  }

  @Override
  public SQLQuery<T> using(SQLSource source) {
    return using(SourceUtils.getSingleStatement(source));
  }

  @Override
  public SQLQuery<T> handlingResultWith(ResultSetHandler<T> handler) {
    assertNotExecuted();
    this.handler = handler;
    this.innerHandler = null;
    return this;
  }

  @Override
  public SQLQuery<T> extractingColumn() {
    return extractingColumn(1);
  }

  @Override
  public SQLQuery<T> extractingColumn(int index) {
    this.handler = null;
    this.innerHandler = new ColumnExtractingResultSetHandler<>(
        ColumnExtractor.with(index, type));
    return this;
  }

  @Override
  public SQLQuery<T> extractingColumn(String label) {
    this.handler = null;
    this.innerHandler = new ColumnExtractingResultSetHandler<>(
        ColumnExtractor.with(label, type));
    return this;
  }

  @Override
  public SQLQuery<T> mappingRowsWith(RowMapper<T> rowMapper) {
    assertNotExecuted();
    this.handler = null;
    this.innerHandler = new RowMappingResultSetHandler<>(rowMapper);
    return this;
  }

  @Override
  public SQLQuery<T> repeatedly() {
    assertNotExecuted();
    this.repeatable = true;
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<T> retrieveList(Parameter... parameters) {
    assertReady();
    try {
      return (List<T>) query(parameters,
          handler != null ? handler : new MultipleRowHandler<>(innerHandler));
    }
    finally {
      executed = true;
      if (!repeatable) {
        close();
      }
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public T retrieveValue(Parameter... parameters) {
    assertReady();
    try {
      return (T) query(parameters,
          handler != null ? handler : new SingleRowHandler<>(innerHandler));
    }
    finally {
      if (!repeatable) {
        close();
      }
    }
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

  private Object query(Parameter[] params, ResultSetHandler<?> extractor) {
    final PreparedQueryExecutor executor =
        new PreparedQueryExecutor(psc,
            Arrays.asList(params));

    ResultSet rs = null;
    try {
      rs = executor.execute(dataSource);
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
  public void close() {
    SQLUtils.closeQuietly(psc);
  }

}
