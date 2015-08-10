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
package org.soulwing.jdbc;

import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * A callback that executes some JDBC operation.
 *
 * @author Carl Harris
 */
interface JdbcExecutor<T> {

  /**
   * Executes some JDBC operation.
   * @param dataSource data source from which a connection is to be obtained
   * @return any result of the operation
   * @throws SQLException as needed
   */
  T execute(DataSource dataSource) throws SQLException;

}
