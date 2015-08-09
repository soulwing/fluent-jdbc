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

import java.util.List;

import org.soulwing.sql.source.SQLSource;

/**
 * An SQL query operation.
 * <p>
 * This API uses the builder pattern to allow a query to be easily configured
 * and executed.  It supports queries that return many rows as well as those
 * that return a single row.  The API is designed to allow a fluent specification
 * of the query configuration.
 * <p>
 * An instance of this type is created using the {@link SQLOperations#query query}
 * or {@link SQLOperations#queryForType(Class) queryForType} method on the
 * {@link SQLOperations} interface.  The user of this interface must configure
 * at least two characteristics:
 * <ol>
 * <li> the SQL statement to execute, specified via the {@link #using(String) using}
 *      method
 * <li> the manner in which the results are to be handled; either
 *      {@linkplain #mappingRowsWith(RowMapper) mapping rows to objects},
 *      {@linkplain #extractingColumn(int) extracting a column value}, or using a
 *      {@linkplain #handlingResultWith(ResultSetHandler) result set handler}
 * </ol>
 * <p>
 * Additionally, the query can be configured for single execution (default)
 * or repeated execution.  When a query is configured for repeated execution
 * (using {@link #repeatedly()}), the underlying statement and connection
 * objects remain open after a result is retrieved, allowing the query to be
 * executed again with different parameter values.  A query that is configured
 * for repeated execution must be closed when it is no longer needed, by
 * invoking the {@link #close()} method explicitly or by enclosing it in a
 * <em>try-with-resources</em> construct.
 * <p>
 * After a query has been fully configured, a result can be retrieved using
 * either
 * <ul>
 * <li> {@link #retrieveList(Parameter...)} for a query that returns multiple
 *      rows, OR
 * <li> {@link #retrieveValue(Parameter...)} for a query that returns <em>exactly
 *      one</em> row
 * </ul>
 * <p>
 * Examples:
 * <p>
 * Retrieving a list of objects that correspond to the rows returned by
 * a query using a {@link RowMapper}:
 * <pre>
 * {@code
 * // A mapper that maps column values of a person row to a Person object
 * RowMapper<Person> personMapper = new RowMapper<Person>() { ... };
 *
 * List<Person> results = sqlTemplate.queryForType(Person.class)
 *     .using("SELECT * FROM person ORDER BY name")
 *     .mappingRowsWith(personMapper)
 *     .retrieveList();
 * }</pre>
 * <p>
 * Retrieving a list of values for a given column for all matching rows:
 * <pre>
 * {@code
 * List<String> names = sqlTemplate.queryForType(String.class)
 *     .using("SELECT * FROM person ORDER BY name")
 *     .extractingColumn("name")
 *     .retrieveList();
 * }</pre>
 * <p>
 * Retrieving a single object using a {@link RowMapper}:
 * <pre>
 * {@code
 * // A mapper that maps column values of a person row to a Person object
 * RowMapper<Person> personMapper = new RowMapper<Person>() { ... };
 *
 * long id = 3;  // ID of the person to retrieve
 * Person person = sqlTemplate.queryForType(Person.class)
 *     .using("SELECT * FROM person WHERE id = ?")
 *     .mappingRowsWith(personMapper)
 *     .retrieveValue(Parameter.with(id));
 * }</pre>
 * <p>
 * Retrieving a single value of a column:
 * <pre>
 * {@code
 * int averageAge = sqlTemplate.queryForType(int.class)
 *     .using("SELECT AVG(age) FROM person")
 *     .extractingColumn()
 *     .retrieveValue();
 * }</pre>
 * <p>
 * Repeatedly executing a query with different parameters:
 * <pre>
 * {@code
 * try (SQLQuery<String> query = sqlTemplate.queryForType(String.class)
 *     .using("SELECT name FROM person WHERE name LIKE ?")
 *     .extractingColumn()
 *     .repeatedly()) {
 *   System.out.format("matching names: %s",
 *       query.retrieveList("%en%"));
 *   System.out.format("matching names: %s",
 *       query.retrieveList("%sh%"));
 * }
 * }</pre>
 * <p>
 * Processing the returned result set using a {@link ResultSetHandler}:
 * <pre>
 * {@code
 * sqlTemplate.query()
 *     .using("SELECT * FROM person ORDER BY id")
 *     .handlingResultWith(new ResultSetMapper<Void>() {
 *        public void handleResult(ResultSet rs) throws SQLException {
 *          while (rs.next()) {
 *            exporter.exportPerson(rs.getLong("id"), rs.getLong("name"));
 *          }
 *          return null;
 *        }
 *     })
 *     .retrieveValue();
 * }</pre>
 *
 * @author Carl Harris
 */
public interface SQLQuery<T> extends AutoCloseable {

  /**
   * Configures this query to execute the given SQL statement.
   * @param sql the SQL statement to execute
   * @return this query
   */
  SQLQuery<T> using(String sql);

  /**
   * Configures this query to execute the given SQL statement.
   * @param source source for the SQL statement to execute
   * @return this query
   */
  SQLQuery<T> using(SQLSource source);

  /**
   * Configures this query to use the given result set handler.
   * <p>
   * Invoking this method replaces any existing configured row mapper or
   * column extractor with the given result set handler.
   *
   * @param handler result set extractor
   * @return this query
   */
  SQLQuery<T> handlingResultWith(ResultSetHandler<T> handler);

  /**
   * Configures this query to extract the value of the first column.
   * <p>
   * Invoking this method replaces any existing configured result set extractor
   * or row mapper with the given column extractor.
   *
   * @return this query
   */
  SQLQuery<T> extractingColumn();

  /**
   * Configures this query to extract the value of the given column.
   * <p>
   * Invoking this method replaces any existing configured result set extractor
   * or row mapper with the given column extractor.
   *
   * @param index column index (index values start at 1)
   * @return this query
   */
  SQLQuery<T> extractingColumn(int index);

  /**
   * Configures this query to extract the value of the given column.
   * <p>
   * Invoking this method replaces any existing configured result set extractor
   * or row mapper with the given column extractor.
   *
   * @param label column label
   * @return this query
   */
  SQLQuery<T> extractingColumn(String label);

  /**
   * Configures this query to use the given row mapper.
   * <p>
   * Invoking this method replaces any existing configured result set extractor
   * or row mapper with the given column extractor.
   *
   * @param rowMapper row mapper
   * @return this query
   */
  SQLQuery<T> mappingRowsWith(RowMapper<T> rowMapper);

  /**
   * Configures this query for repeated execution.
   * <p>
   * A query that is configured as repeatable must be explicitly closed by
   * the caller using {@link #close()} when no longer needed.
   *
   * @return this query
   */
  SQLQuery<T> repeatedly();

  /**
   * Retrieves the list of values for all rows matched by this query.
   * @param parameters values for query placeholders
   * @return list of values of type {@code T} that were extracted/mapped by
   *   this query
   */
  List<T> retrieveList(Parameter... parameters);

  /**
   * Retrieves the value for the single row matched by this query.
   * @param parameters values for query placeholders
   * @return value of type {@code T} that was extracted/mapped by this query
   * @throws SQLNoResultException if no row was matched by this query
   * @throws SQLNonUniqueResultException if more than one row was matched by
   *    this query
   */
  T retrieveValue(Parameter... parameters);

  /**
   * Closes the JDBC resources associated with this query.
   * <p>
   * After a query is closed, its retrieval methods may not be subsequently
   * invoked.
   */
  void close();

}
