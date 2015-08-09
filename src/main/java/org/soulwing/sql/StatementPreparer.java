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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;

import org.soulwing.sql.source.SQLSource;

/**
 * A thread-safe lazy {@link PreparedStatementCreator}.
 *
 * @author Carl Harris
 */
class StatementPreparer implements PreparedStatementCreator {

  private final Lock lock = new ReentrantLock();

  private final String sql;

  private Connection connection;

  private volatile PreparedStatement statement;

  private StatementPreparer(String sql) {
    this.sql = sql;
  }

  /**
   * Creates a new statement preparer for the given SQL statement.
   * @param sql the SQL statement to prepare
   * @return statement preparer
   */
  public static StatementPreparer with(String sql) {
    return new StatementPreparer(sql);
  }

  /**
   * Creates a new statement preparer for the given SQL source
   * @param source source for the SQL statement to prepare
   * @return statement preparer
   */
  public static StatementPreparer with(SQLSource source) {
    return new StatementPreparer(SourceUtils.getSingleStatement(source));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PreparedStatement prepareStatement(DataSource dataSource)
      throws SQLException {
    if (statement == null) {
      lock.lock();
      try {
        if (statement == null) {
          connection = dataSource.getConnection();
          statement = connection.prepareStatement(sql);
        }
      }
      finally {
        lock.unlock();
      }
    }
    return statement;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void close() {
    SQLUtils.closeQuietly(statement);
    SQLUtils.closeQuietly(connection);
  }

}
