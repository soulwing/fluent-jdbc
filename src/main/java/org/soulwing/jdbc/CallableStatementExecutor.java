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
package org.soulwing.jdbc;

import java.sql.CallableStatement;
import java.sql.SQLException;

import org.soulwing.jdbc.logger.JdbcLogger;

/**
 * An {@link JdbcExecutor} that executes a stored procedure call.
 *
 * @author Carl Harris
 */
class CallableStatementExecutor
    extends AbstractPreparedStatementExecutor<Boolean, CallableStatement> {

  /**
   * Constructs a new instance.
   * @param psc prepared statement creator for the call statement
   * @param parameters values for placeholders in the statement
   * @param logger statement logger
   */
  public CallableStatementExecutor(
      PreparedStatementCreator<CallableStatement> psc,
      Parameter[] parameters,
      JdbcLogger logger) {
    super(psc, parameters, logger);
  }

  @Override
  protected Boolean doExecute(CallableStatement statement) throws SQLException {
    return statement.execute();
  }

}
