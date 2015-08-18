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

import java.sql.SQLException;

import javax.sql.DataSource;

import org.soulwing.jdbc.logger.JdbcLogger;

/**
 * A simple JDBC {@link java.sql.Statement} executor.
 *
 * @author Carl Harris
 */
class StatementExecutor implements JdbcExecutor<Void> {

  private final PreparedStatementCreator psc;
  private final JdbcLogger logger;

  /***
   * Constructs a new instance.
   * @param psc prepared statement creator
   * @param logger statement logger
   */
  public StatementExecutor(PreparedStatementCreator psc,
      JdbcLogger logger) {
    this.psc = psc;
    this.logger = logger;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Void execute(DataSource dataSource) throws SQLException {
    logger.writeStatement(psc.getStatementText());
    psc.prepareStatement(dataSource).execute();
    return null;
  }

}
