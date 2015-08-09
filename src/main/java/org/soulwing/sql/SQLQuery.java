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
