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

import org.soulwing.sql.source.SQLSource;

/**
 * An SQL query operation.
 *
 * @author Carl Harris
 */
public interface SQLUpdate extends AutoCloseable {

  /**
   * Configures this update to execute the given SQL statement.
   * @param sql the SQL statement to execute
   * @return this update object
   */
  SQLUpdate using(String sql);

  /**
   * Configures this update to execute the given SQL statement.
   * @param source for the SQL statement to execute
   * @return this update object
   */
  SQLUpdate using(SQLSource source);

  /**
   * Configures this update for repeated execution.
   * <p>
   * An update that is configured as repeatable must be explicitly closed by
   * the caller using {@link #close()} when no longer needed.
   *
   * @return this query
   */
  SQLUpdate repeatedly();

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
