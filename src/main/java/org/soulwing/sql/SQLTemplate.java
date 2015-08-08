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

import java.util.List;

import org.soulwing.sql.source.SQLSource;

/**
 * A template for performing SQL operations, inspired by Spring's 
 * {@code JdbcTemplate}.
 *
 * @author Carl Harris
 */
public interface SQLTemplate {

  /**
   * Executes a single SQL (DDL) statement.
   * @param sql the SQL statement to execute
   */
  void execute(String sql);

  /**
   * Executes a single SQL (DDL) statement.
   * @param source source for the SQL statement to execute
   */
  void execute(SQLSource source);

  /**
   * Executes the sequence of SQL statements produced by the given source.
   * @param source source of the SQL statements to execute
   */
  void executeScript(SQLSource source);

  /**
   * Executes a query and extracts results using the given extractor.
   * <p>
   * This method is designed to allow efficient repeated execution of the
   * same SQL statement (using the given prepared statement creator).  For
   * statements that will be executed just once, the methods that take an
   * SQL string or source may be more convenient.
   *
   * @param psc prepared statement creator
   * @param parameters parameter values for query placeholders
   * @param extractor result set extractor to invoke
   * @return result returned by {@code extractor}
   */
  <T> T query(PreparedStatementCreator psc, Parameter[] parameters,
      ResultSetExtractor<T> extractor);

  /**
   * Executes a query and extracts results using the given extractor.
   * @param sql the SQL statement to execute
   * @param parameters parameter values for query placeholders
   * @param extractor result set extractor to invoke
   * @return result returned by {@code extractor}
   */
  <T> T query(String sql, Parameter[] parameters,
      ResultSetExtractor<T> extractor);

  /**
   * Executes a query and extracts results using the given extractor.
   * @param source source for the SQL statement to execute
   * @param parameters parameter values for query placeholders
   * @param extractor result set extractor to invoke
   * @return result returned by {@code extractor}
   */
  <T> T query(SQLSource source, Parameter[] parameters,
      ResultSetExtractor<T> extractor);

  /**
   * Executes a query statement to produce a collection of objects.
   * <p>
   * This method is designed to allow efficient repeated execution of the
   * same SQL statement (using the given prepared statement creator).  For
   * statements that will be executed just once, the methods that take an
   * SQL string or source may be more convenient.
   *
   * @param psc prepared statement creator
   * @param parameters substitution parameters for placeholders in the query
   * @param rowMapper an object that maps a row in a {@link java.sql.ResultSet}
   *   to an instance of type {@code T}
   * @return the list of objects produced by the row mapper for this query
   */
  <T> List<T> query(PreparedStatementCreator psc, Parameter[] parameters,
      RowMapper<T> rowMapper);

  /**
   * Executes a query statement to produce a collection of objects.
   * @param sql SQL for the statement to execute
   * @param params substitution parameters for placeholders in the query
   * @param rowMapper an object that maps a row in a {@link java.sql.ResultSet}
   *   to an instance of type {@code T}
   * @return the list of objects produced by the row mapper for this query
   */
  <T> List<T> query(String sql, Parameter[] params, RowMapper<T> rowMapper);

  /**
   * Executes a query statement to produce a collection of objects.
   * @param source source for the SQL statement to execute
   * @param params substitution parameters for placeholders in the query
   * @param rowMapper an object that maps a row in a {@link java.sql.ResultSet}
   *   to an instance of type {@code T}
   * @return the list of objects produced by the row mapper for this query
   */
  <T> List<T> query(SQLSource source, Parameter[] params, RowMapper<T> rowMapper);

  /**
   * Executes a query statement to produce a single object.
   * <p>
   * This method is designed to allow efficient repeated execution of the
   * same SQL statement (using the given prepared statement creator).  For
   * statements that will be executed just once, the methods that take an
   * SQL string or source may be more convenient.
   *
   * @param psc prepared statement creator
   * @param extractor column extractor for the desired column
   * @param parameters substitution parameters for placeholders in the query
   * @return the object produced by the row mapper for this query
   */
  <T> T queryForObject(PreparedStatementCreator psc,
      ColumnExtractor<T> extractor, Parameter... parameters);

  /**
   * Executes a query statement to produce a single object
   * @param sql SQL for the statement to execute
   * @param extractor column extractor for the desired column
   * @param parameters substitution parameters for placeholders in the query
   * @return the object produced by the row mapper for this query
   */
  <T> T queryForObject(String sql, ColumnExtractor<T> extractor,
      Parameter... parameters);

  /**
   * Executes a query statement to produce a single object
   * @param source SQL for the statement to execute
   * @param extractor column extractor for the desired column
   * @param parameters substitution parameters for placeholders in the query
   * @return the object produced by the row mapper for this query
   */
  <T> T queryForObject(SQLSource source, ColumnExtractor<T> extractor,
      Parameter... parameters);

  /**
   * Executes a query statement to produce a single object
   * <p>
   * This method is designed to allow efficient repeated execution of the
   * same SQL statement (using the given prepared statement creator).  For
   * statements that will be executed just once, the methods that take an
   * SQL string or source may be more convenient.
   *
   * @param psc prepared statement creator
   * @param parameters substitution parameters for placeholders in the query
   * @param rowMapper an object that maps the row in a
   *    {@link java.sql.ResultSet} to an instance of type {@code T}
   * @return the object produced by the row mapper for this query
   */
  <T> T queryForObject(PreparedStatementCreator psc, Parameter[] parameters,
      RowMapper<T> rowMapper);

  /**
   * Executes a query statement to produce a single object
   * @param sql SQL for the statement to execute
   * @param parameters substitution parameters for placeholders in the query
   * @param rowMapper an object that maps the row in a
   *    {@link java.sql.ResultSet} to an instance of type {@code T}
   * @return the object produced by the row mapper for this query
   */
  <T> T queryForObject(String sql, Parameter[] parameters,
      RowMapper<T> rowMapper);

  /**
   * Executes a query statement to produce a single object
   * @param source source for the SQL statement to execute
   * @param parameters substitution parameters for placeholders in the query
   * @param rowMapper an object that maps the row in a
   *    {@link java.sql.ResultSet} to an instance of type {@code T}
   * @return the object produced by the row mapper for this query
   */
  <T> T queryForObject(SQLSource source, Parameter[] parameters,
      RowMapper<T> rowMapper);

  /**
   * Executes an SQL insert, update, or delete statement.
   * <p>
   * This method is designed to allow efficient repeated execution of the same
   * statement with different parameters.  When using this method, the prepared
   * statement is created lazily and cached so that it can be used again without
   * needing to repeatedly parse the SQL it contains.
   * <p>
   * Note that when using this method, the connection associated with the
   * prepared statement remains open until the
   * {@link PreparedStatementCreator#close()} method is invoked on {@code psc}.
   *
   * @param psc prepared statement creator
   * @param params substitution parameters for placeholders in the statement
   * @return the number of rows affected by executing the statement
   */
  int update(PreparedStatementCreator psc, Parameter... params);

  /**
   * Executes an SQL insert, update, or delete statement.
   * @param sql SQL for the the statement to execute
   * @param params substitution parameters for placeholders in the statement
   * @return the number of rows affected by executing the statement
   */
  int update(String sql, Parameter... params);

  /**
   * Executes an SQL insert, update, or delete statement.
   * @param source source for the SQL for the the statement to execute
   * @param params substitution parameters for placeholders in the statement
   * @return the number of rows affected by executing the statement
   */
  int update(SQLSource source, Parameter... params);

  /**
   * Executes a call to a stored procedure.
   * @param sql SQL for the call
   * @param params substitution parameters that specify the arguments for the 
   *    call
   * @return the output parameters specified for the call; the returned
   */
  CallResult call(String sql, Parameter... params);

  /**
   * Executes a call to a stored procedure.
   * @param source source for the SQL to call
   * @param params substitution parameters that specify the arguments for the
   *    call
   * @return the output parameters specified for the call
   */
  CallResult call(SQLSource source, Parameter... params);

}
