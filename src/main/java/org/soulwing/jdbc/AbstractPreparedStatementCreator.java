/*
 * File created on Aug 10, 2015
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;

/**
 * An abstract base for {@link PreparedStatementCreator} implementations.
 * <p>
 * This implementation lazily performs the statement preparation and caches
 * the associated connection and statement objects until the {@link #close()}
 * method is invoked.  It is thread safe.
 *
 * @author Carl Harris
 */
abstract class AbstractPreparedStatementCreator<T extends PreparedStatement>
    implements PreparedStatementCreator<T> {

  private final Lock lock = new ReentrantLock();

  private final String sql;

  private Connection connection;

  private volatile T statement;

  /**
   * Constructs a new instance.
   * @param sql the SQL statement to prepare
   */
  AbstractPreparedStatementCreator(String sql) {
    this.sql = sql;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getStatementText() {
    return sql;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public T prepareStatement(DataSource dataSource)
      throws SQLException {
    if (statement == null) {
      lock.lock();
      try {
        if (statement == null) {
          connection = dataSource.getConnection();
          statement = prepareStatement(connection, sql);
        }
      }
      finally {
        lock.unlock();
      }
    }
    return statement;
  }

  /**
   * Prepares a statement on the given connection.
   * <p>
   * This method is invoked exactly once to prepare the configured SQL
   * statement, when needed.
   *
   * @param connection connection on which the statement is to be prepared
   * @param sql the SQL statement to prepare
   * @return prepared statement
   * @throws SQLException as needed
   */
  protected abstract T prepareStatement(Connection connection,
      String sql) throws SQLException;

  /**
   * {@inheritDoc}
   */
  @Override
  public void close() {
    JdbcUtils.closeQuietly(statement);
    JdbcUtils.closeQuietly(connection);
  }

}
