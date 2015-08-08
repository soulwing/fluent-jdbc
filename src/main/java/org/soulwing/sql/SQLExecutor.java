/*
 * File created on May 5, 2014 
 *
 * Copyright (c) 2014 Virginia Polytechnic Institute and State University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.soulwing.sql;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * A callback that executes some JDBC operation using a {@link Connection}.
 *
 * @author Carl Harris
 */
public interface SQLExecutor<T> {

  /**
   * Executes some JDBC operation by obtaining a connection from the given
   * connection.
   * @param dataSource the connection to use for the operation
   * @return any result of the operation
   * @throws SQLException
   */
  T execute(DataSource dataSource) throws SQLException;

  /**
   * Closes any JDBC resources associated with this executor.
   * @throws SQLException
   */
  void close() throws SQLException;

}
