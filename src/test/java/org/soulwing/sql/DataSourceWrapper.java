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

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * A wrapper for a {@link DataSource} with connection management API.
 *
 * @author Carl Harris
 */
public class DataSourceWrapper implements DataSource {

  private final Set<Connection> connections = new HashSet<>();

  private final DataSource dataSource;

  public DataSourceWrapper(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public void connectionClosed(Connection connection) {
    connections.remove(connection);
  }

  public boolean hasOpenConnections() {
    return !connections.isEmpty();
  }

  @Override
  public Connection getConnection() throws SQLException {
    final Connection connection =
        new ConnectionWrapper(dataSource.getConnection(), this);
    connections.add(connection);
    return connection;
  }

  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    final Connection connection = new ConnectionWrapper(
        dataSource.getConnection(username, password), this);
    connections.add(connection);
    return connection;
  }

  @Override
  public PrintWriter getLogWriter() throws SQLException {
    return dataSource.getLogWriter();
  }

  @Override
  public void setLogWriter(PrintWriter out) throws SQLException {
    dataSource.setLogWriter(out);
  }

  @Override
  public void setLoginTimeout(int seconds) throws SQLException {
    dataSource.setLoginTimeout(seconds);
  }

  @Override
  public int getLoginTimeout() throws SQLException {
    return dataSource.getLoginTimeout();
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return dataSource.getParentLogger();
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return dataSource.unwrap(iface);
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return dataSource.isWrapperFor(iface);
  }
}
