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
package org.soulwing.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * An object responsible for creating and caching a {@link PreparedStatement}.
 * @param <T> the prepared statement subtype supported by this creator
 * @author Carl Harris
 */
interface PreparedStatementCreator<T extends PreparedStatement>
    extends AutoCloseable {

  /**
   * Gets the text of the SQL statement associated with this creator.
   * @return statement text
   */
  String getStatementText();

  /**
   * Prepares a statement for the SQL associated with this creator.
   * @param dataSource connection to use
   * @return prepared statement; if a statement has already been prepared, the
   *    previously created prepared statement should returned
   * @throws SQLException
   */
  T prepareStatement(DataSource dataSource) throws SQLException;

  /**
   * Closes the statement and associated connection.
   * @throws SQLException
   */
  void close() throws SQLException;

}
