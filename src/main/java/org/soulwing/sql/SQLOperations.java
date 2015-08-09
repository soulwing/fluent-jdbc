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

import org.soulwing.sql.source.SQLSource;

/**
 * A high level API for performing SQL operations using JDBC.
 * <p>
 * The design of this interface was heavily influenced by the
 * {@code JdbcTemplate} abstraction in the Spring Framework, however it
 * differs significantly, particularly in the manner in which queries and
 * updates are invoked.  Nonetheless, users of {@code JdbcTemplate} should
 * find it familiar enough to easily learn.
 *
 * @author Carl Harris
 */
public interface SQLOperations {

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
   * Creates a query that returns nothing.
   * <p>
   * This is a synonym for {@link #queryForType(Class)} with {@link Void}
   * as the specified type.  A query obtained via this method is typically
   * configured to use a {@link ResultSetHandler} to process returned rows.
   *
   * @return query object that can be configured and executed to retrieve
   *    rows from the database
   */
  SQLQuery<Void> query();

  /**
   * Creates a query for objects of a given type.
   * @param type subject type of the query
   * @return a query that can be configured and executed to retreive rows
   *    from the database and return objects of type {@code T}.
   */
  <T> SQLQuery<T> queryForType(Class<T> type);

  /**
   * Creates an updater.
   * @return an updater that can be configured and executed to update rows in
   *    the database.
   */
  SQLUpdate update();

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
