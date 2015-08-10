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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;

import javax.sql.DataSource;

import org.hsqldb.jdbc.JDBCDataSource;

/**
 * An abstraction for an HSQLDB test database.
 *
 * @author Carl Harris
 */
class TestDatabase {

  private final String ID = UUID.randomUUID().toString();

  private final JDBCDataSource dataSource = new JDBCDataSource();

  public TestDatabase() {
    dataSource.setUrl("jdbc:hsqldb:mem:" + ID);
    dataSource.setUser("sa");
    dataSource.setPassword("");
    dataSource.setProperties(new Properties());
  }

  public void close() throws SQLRuntimeException {
    try {
      Connection connection = dataSource.getConnection();
      connection.createStatement().execute("SHUTDOWN");
      connection.close();
    }
    catch (SQLException ex) {
      throw new SQLRuntimeException(ex);
    }
  }

  public DataSource getDataSource() {
    return dataSource;
  }

}
