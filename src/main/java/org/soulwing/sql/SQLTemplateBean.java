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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.soulwing.sql.source.SQLSource;

/**
 * An {@link SQLTemplate} implemented as an injectable bean.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class SQLTemplateBean implements SQLTemplate {

  @Inject
  protected DataSourceProvider dataSourceProvider;

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(String sql) {
    final PreparedStatementCreator psc = StatementPreparer.with(sql);
    final StatementExecutor executor = new StatementExecutor(psc);
    try {
      executor.execute(dataSourceProvider.getDataSource());
    }
    catch (SQLException ex) {
      throw new SQLRuntimeException(ex);
    }
    finally {
      SQLUtils.closeQuietly(psc);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(SQLSource source) {
    execute(SourceUtils.getSingleStatement(source));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void executeScript(SQLSource source) {
    try {
      String sql = source.next();
      while (sql != null) {
        execute(sql);
        sql = source.next();
      }
    }
    finally {
      SQLUtils.closeQuietly(source);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T query(PreparedStatementCreator psc, Parameter[] params,
      ResultSetExtractor<T> extractor) {
    final PreparedQueryExecutor executor =
        new PreparedQueryExecutor(psc,
            Arrays.asList(params));

    ResultSet rs = null;
    try {
      rs = executor.execute(dataSourceProvider.getDataSource());
      return extractResultSet(rs, extractor);
    }
    catch (SQLException ex) {
      throw new SQLRuntimeException(ex);
    }
    finally {
      SQLUtils.closeQuietly(rs);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T query(String sql, Parameter[] params,
      ResultSetExtractor<T> extractor) {
    PreparedStatementCreator psc = StatementPreparer.with(sql);
    try {
      return query(psc, params, extractor);
    }
    finally {
      SQLUtils.closeQuietly(psc);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T query(SQLSource source, Parameter[] parameters,
      ResultSetExtractor<T> extractor) {
    return query(SourceUtils.getSingleStatement(source), parameters, extractor);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> List<T> query(PreparedStatementCreator psc,
      ColumnExtractor<T> extractor, Parameter... parameters) {
    return query(psc, parameters, new MultipleRowExtractor<>(
        new ColumnExtractingResultSetExtractor<>(extractor)));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> List<T> query(String sql, ColumnExtractor<T> extractor,
      Parameter... parameters) {
    return query(sql, parameters, new MultipleRowExtractor<>(
        new ColumnExtractingResultSetExtractor<>(extractor)));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> List<T> query(SQLSource source, ColumnExtractor<T> extractor,
      Parameter... parameters) {
    return query(SourceUtils.getSingleStatement(source), extractor, parameters);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> List<T> query(PreparedStatementCreator psc, Parameter[] params,
      RowMapper<T> rowMapper) {
    return query(psc, params, new MultipleRowExtractor<>(
        new RowMappingResultSetExtractor<>(rowMapper)));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> List<T> query(String sql, Parameter[] params,
      RowMapper<T> rowMapper) {
    return query(sql, params, new MultipleRowExtractor<>(
        new RowMappingResultSetExtractor<>(rowMapper)));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> List<T> query(SQLSource source, Parameter[] params,
      RowMapper<T> rowMapper) {
    return query(SourceUtils.getSingleStatement(source), params, rowMapper);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T queryForObject(PreparedStatementCreator psc,
      ColumnExtractor<T> extractor, Parameter... params) {
    return query(psc, params, new SingleRowExtractor<>(
        new ColumnExtractingResultSetExtractor<>(extractor)));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T queryForObject(String sql, ColumnExtractor<T> extractor,
      Parameter... parameters) {
    return query(sql, parameters, new SingleRowExtractor<>(
        new ColumnExtractingResultSetExtractor<>(extractor)));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T queryForObject(SQLSource source, ColumnExtractor<T> extractor,
      Parameter... parameters) {
    return queryForObject(SourceUtils.getSingleStatement(source), extractor,
        parameters);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T queryForObject(PreparedStatementCreator psc,
      Parameter[] parameters, RowMapper<T> rowMapper) {
    return query(psc, parameters, new SingleRowExtractor<>(
        new RowMappingResultSetExtractor<>(rowMapper)));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T queryForObject(String sql, Parameter[] parameters,
      final RowMapper<T> rowMapper) {
    return query(sql, parameters, new SingleRowExtractor<>(
        new RowMappingResultSetExtractor<>(rowMapper)));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T queryForObject(SQLSource source, Parameter[] parameters,
      RowMapper<T> rowMapper) {
    return queryForObject(SourceUtils.getSingleStatement(source), parameters,
        rowMapper);
  }

  @Override
  public int update(PreparedStatementCreator psc, Parameter... params) {
    final PreparedUpdateExecutor executor = new PreparedUpdateExecutor(
        psc, Arrays.asList(params));

    try {
      return executor.execute(dataSourceProvider.getDataSource());
    }
    catch (SQLException ex) {
      throw new SQLRuntimeException(ex);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int update(String sql, Parameter... params) {
    final PreparedStatementCreator psc = StatementPreparer.with(sql);
    try {
      return update(psc, params);
    }
    finally {
      SQLUtils.closeQuietly(psc);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int update(SQLSource source, Parameter... params) {
    return update(SourceUtils.getSingleStatement(source), params);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CallResult call(String sql, Parameter... params) {
    final CallableStatementExecutor executor = new CallableStatementExecutor(
        sql, Arrays.asList(params));
    try {
      executor.execute(dataSourceProvider.getDataSource());
    }
    catch (SQLException ex) {
      throw new SQLRuntimeException(ex);
    }
    return executor;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CallResult call(SQLSource source, Parameter... params) {
    return call(SourceUtils.getSingleStatement(source), params);
  }

  private <T> T extractResultSet(ResultSet rs, ResultSetExtractor<T> extractor) {
    try {
      return extractor.extract(rs);
    }
    catch (SQLException ex) {
      throw new SQLRuntimeException(ex);
    }
    finally {
      SQLUtils.closeQuietly(rs);
    }
  }

}
