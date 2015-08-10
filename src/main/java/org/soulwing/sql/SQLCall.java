/*
 * File created on Aug 8, 2015
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
import java.util.List;

import org.soulwing.sql.source.SQLSource;

/**
 * A SQL call operation.
 * <p>
 * The {@link #execute(Parameter...)} method be invoked repeatedly with
 * different parameter values.  When the call operation is no longer needed,
 * it <strong>must</strong> be closed by either calling the {@link #close()}
 * method or by nesting it within a {@code try-with-resources} construct.
 * <p>
 * The call operation supports stored procedures that return values through
 * any combination of
 * <ul>
 * <li> zero or more implicitly returned {@link ResultSet} objects,
 * <li> an optional update count, and
 * <li> zero or more parameters registered for OUT or INOUT semantics.
 * </ul>
 * While this API supports all of the possible return value mechanisms that
 * are inherent with JDBC, the actual support provided for stored procedures
 * varies greatly between JDBC vendors, with each vendor choosing which
 * mechanism to support.  It is important to understand the capabilities of
 * the specific JDBC vendor in order to effectively make use of this API.
 * <p>
 * For maximum portability, on return from {@link #execute} the caller should
 * first process all results sets and update counts, before retrieving the
 * values of registered OUT/INOUT parameters.
 *
 * @author Carl Harris
 */
public interface SQLCall extends AutoCloseable {

  /**
   * Executes this call operation.
   * @param parameters values for placeholders in the statement; may be
   *    any combination of IN, OUT, or INOUT parameters supported by the
   *    target called procedure
   * @return {@code true} if the first result is a {@link ResultSet},
   *    {@code false} if it is an update count or there are no results
   *
   */
  boolean execute(Parameter... parameters);

  /**
   * Closes the JDBC resources associated with this call operation.
   * <p>
   * After a call operation is closed, its {@link #execute(Parameter...) execute}
   * method may not be subsequently invoked.
   */
  void close();

  /**
   * Gets the number of rows inserted/updated.
   * @return update count or -1 if no update count is available
   * @see {@link java.sql.Statement#getUpdateCount()}
   */
  int getUpdateCount();

  /**
   * Gets a flag indicating the next result type available.
   * <p>
   * Invoking this method implicitly closes the last {@link ResultSet} returned
   * from the {@link #getResultSet()} method.
   * @return {@code true} if the next result is a {@link ResultSet},
   *     {@code false} if it is an update count or there are no more results
   * @see {@link java.sql.Statement#getMoreResults()}
   */
  boolean getMoreResults();

  /**
   * Gets the current result set.
   * @return result set or {@code null} if the current result is not a result set
   * @see {@link java.sql.Statement#getResultSet()}
   */
  ResultSet getResultSet();

  /**
   * Gets a list containing all parameter values returned by the stored
   * procedure.
   * <p>
   * For maximum portability, this method should not be invoked until
   * after all result sets and update counts for the execution have been
   * retrieved/processed.
   *
   * @return list of all OUT and INOUT parameters, in the order in which
   *    they appear in the statement as placeholders
   */
  List<Parameter> getOutParameters();

  /**
   * Processes the current result set using the given handler.
   * @param handler the subject handler
   * @return object produced by {@code handler}
   */
  <T> T handleResult(ResultSetHandler<T> handler);

  /**
   * Retrieves all values of a given column in the current result set.
   * @param columnLabel column label (name)
   * @return list of values for the specified column
   */
  <T> List<T> retrieveList(String columnLabel, Class<T> type);

  /**
   * Retrieves all values of a given column in the current result set.
   * @param columnIndex column index (starts at 1)
   * @return list of values for the specified column
   */
  <T> List<T> retrieveList(int columnIndex, Class<T> type);

  /**
   * Retrieves and maps all rows in the current result set using the given
   * row mapper.
   * @param rowMapper the subject row mapper
   * @return list of objects produced by {@link RowMapper}
   */
  <T> List<T> retrieveList(RowMapper<T> rowMapper);

  /**
   * Retrieves a column value from the current result set, which must contain
   * a exactly one row.
   *
   * @param columnLabel column label (name)
   * @return value of the specified column
   * @throws SQLNoResultException if the result contains no rows
   * @throws SQLNonUniqueResultException if the result contains more than one
   *    row
   */
  <T> T retrieveValue(String columnLabel, Class<T> type);

  /**
   * Retrieves a column value from the current result set, which must contain
   * a exactly one row.
   *
   * @param columnIndex column index (starts at 1)
   * @return value of the specified column
   * @throws SQLNoResultException if the result contains no rows
   * @throws SQLNonUniqueResultException if the result contains more than one
   *    row
   */
  <T> T retrieveValue(int columnIndex, Class<T> type);

  /**
   * Retrieves and maps a single row in current result set, which must contain
   * exactly one row.
   * @param rowMapper the subject row mapper
   * @return object produced by {@link RowMapper} for the row
   */
  <T> T retrieveValue(RowMapper<T> rowMapper);

}
