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

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.soulwing.jdbc.source.SQLSource;

/**
 * Static utility methods for JDBC resources.
 *
 * @author Carl Harris
 */
class JdbcUtils {

  /**
   * Closes a JDBC resource without throwing {@link SQLException}.
   * @param connection the connection to close (may be null)
   */
  public static void closeQuietly(Connection connection) {
    if (connection == null) return;
    try {
      connection.close();
    }
    catch (SQLException ex) {
      ex.printStackTrace(System.out);
    }
  }

  /**
   * Closes a JDBC resource without throwing {@link SQLException}.
   * @param statement the statement to close (may be null)
   */
  public static void closeQuietly(Statement statement) {
    if (statement == null) return;
    try {
      statement.close();
    }
    catch (SQLException ex) {
      ex.printStackTrace(System.out);
    }
  }

  /**
   * Closes a JDBC resource without throwing {@link SQLException}.
   * @param rs the result set to close (may be null)
   */
  public static void closeQuietly(ResultSet rs) {
    if (rs == null) return;
    try {
      rs.close();
    }
    catch (SQLException ex) {
      ex.printStackTrace(System.out);
    }
  }

  /**
   * Closes a source without throwing {@link IOException}.
   * @param source the source to close (may be null)
   */
  public static void closeQuietly(SQLSource source) {
    if (source == null) return;
    try {
      source.close();
    }
    catch (IOException ex) {
      ex.printStackTrace(System.out);
    }
  }

  /**
   * Closes a prepared statement creator without throwing {@link SQLException}.
   * @param psc the prepared statement creator to close (may be null)
   */
  public static void closeQuietly(PreparedStatementCreator psc) {
    if (psc == null) return;
    try {
      psc.close();
    }
    catch (SQLException ex) {
      ex.printStackTrace(System.out);
    }
  }

}
