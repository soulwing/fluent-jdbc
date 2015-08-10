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

import org.soulwing.jdbc.source.SQLSource;

/**
 * An SQL update operation.
 * <p>
 * This API uses the builder pattern to allow an update operation to be easily
 * configured and executed, using a fluent expression.
 * <p>
 * An instance of this type is created using the
 * {@link JdbcOperations#update update} method on the {@link JdbcOperations}
 * interface.  The update operation must be configured with a statement to
 * execute via the {@link #using(String) using} method.
 * <p>
 * Additionally, the update operation can be configured for single execution
 * (default) or repeated execution.  When an update is configured for repeated
 * execution (using {@link #repeatedly()}), the underlying statement and
 * connection objects remain open after the {@link #execute(Parameter...)},
 * method is invoked, allowing the update operation to be executed again with
 * different parameter values.  An update operation that is configured
 * for repeated execution must be closed when it is no longer needed, by
 * invoking the {@link #close()} method explicitly or by enclosing it in a
 * <em>try-with-resources</em> construct.
 * <p>
 * After the update operation has been configured, it can be executed using
 * the {@link #execute(Parameter...)} method.
 * <p>
 * Examples:
 * <p>
 * Updating many rows with a single statement execution:
 * <pre>
 * {@code
 * int count = sqlTemplate.update()
 *     .using("DELETE FROM person WHERE status = ?")
 *     .execute(Parameter.with("INACTIVE");
 * }</pre>
 * <p>
 * Repeatedly executing the same update operation with different parameters:
 * <pre>
 * {@code
 * try (JdbcUpdate updater = sqlTemplate.update()
 *     .using("UPDATE person SET age = age + 1 WHERE id = ?")
 *     .repeatedly()) {
 *   updater.execute(Parameter.with(1L));
 *   updater.execute(Parameter.with(4L));
 * }
 * }</pre>
 *
 * @author Carl Harris
 */
public interface JdbcUpdate extends AutoCloseable {

  /**
   * Configures this update to execute the given SQL statement.
   * @param sql the SQL statement to execute
   * @return this update object
   */
  JdbcUpdate using(String sql);

  /**
   * Configures this update to execute the given SQL statement.
   * @param source for the SQL statement to execute
   * @return this update object
   */
  JdbcUpdate using(SQLSource source);

  /**
   * Configures this update for repeated execution.
   * <p>
   * An update that is configured as repeatable must be explicitly closed by
   * the caller using {@link #close()} when no longer needed.
   *
   * @return this query
   */
  JdbcUpdate repeatedly();

  /**
   * Executes this update.
   * @param parameters values for placeholders in the SQL statement
   * @return number of rows affected by the update
   */
  int execute(Parameter... parameters);

  /**
   * Closes the JDBC resources associated with this update.
   * <p>
   * After an update is closed, its {@link #execute(Parameter...)} method may
   * not be subsequently invoked.
   */
  void close();

}
