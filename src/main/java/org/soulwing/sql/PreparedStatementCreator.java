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
package org.soulwing.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.soulwing.sql.source.SQLSource;

/**
 * A lazy creator for a {@link PreparedStatement}.
 *
 * @author Carl Harris
 */
public class PreparedStatementCreator {

  private final Lock lock = new ReentrantLock();

  private final String sql;

  private Connection connection;

  private volatile PreparedStatement statement;

  private PreparedStatementCreator(String sql) {
    this.sql = sql;
  }

  public static PreparedStatementCreator with(String sql) {
    return new PreparedStatementCreator(sql);
  }

  public static PreparedStatementCreator with(SQLSource source) {
    return new PreparedStatementCreator(SourceUtils.getSingleStatement(source));
  }

  /**
   * Gets the previously prepared statement, if any.
   * @return prepared statement or {@code null} if no statement has been
   *    prepared
   */
  public PreparedStatement getPreparedStatement() {
    return statement;
  }

  /**
   * Prepares a statement for the SQL associated with this creator.
   * @param connection connection to use
   * @return prepared statement; if the SQL has already been prepared, the
   *    previously created prepared statement is returned
   * @throws SQLException
   */
  public PreparedStatement prepareStatement(Connection connection)
      throws SQLException {
    if (statement == null) {
      lock.lock();
      try {
        if (statement == null) {
          this.connection = connection;
          this.statement = connection.prepareStatement(sql);
        }
      }
      finally {
        lock.unlock();
      }
    }
    return statement;
  }

  /**
   * Closes the statement and connection associated with this creator.
   */
  public void close() {
    SQLUtils.closeQuietly(statement);
    SQLUtils.closeQuietly(connection);
  }

}
