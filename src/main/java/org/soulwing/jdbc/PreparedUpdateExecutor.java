/*
 * File created on Aug 5, 2015
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

import org.soulwing.jdbc.logger.JdbcLogger;

/**
 * An {@link JdbcExecutor} that executes a prepared update statement.
 *
 * @author Carl Harris
 */
class PreparedUpdateExecutor
    extends AbstractPreparedStatementExecutor<Integer, PreparedStatement> {

  /**
   * Constructs a new instance
   * @param psc prepared statement creator for the staement to execute
   * @param parameters values for placeholders in statement
   * @param logger statement logger
   */
  public PreparedUpdateExecutor(
      PreparedStatementCreator<PreparedStatement> psc,
      Parameter[] parameters, JdbcLogger logger) {
    super(psc, parameters, logger);
  }

  /**
   * Executes the prepared statement using {@link PreparedStatement#executeUpdate}.
   * @param statement the statement to execute
   * @return number of rows updated (or created) by the statement
   * @throws SQLException
   */
  @Override
  protected Integer doExecute(PreparedStatement statement) throws
      SQLException {
    return statement.executeUpdate();
  }

}
