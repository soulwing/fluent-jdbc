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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import org.soulwing.jdbc.source.SQLSource;

/**
 * A {@link PreparedStatementCreator} that creates {@link CallableStatement}
 * objects.
 *
 * @author Carl Harris
 */
class CallPreparer extends AbstractPreparedStatementCreator<CallableStatement> {

  private CallPreparer(String sql) {
    super(sql);
  }

  /**
   * Creates a new statement preparer for the given SQL statement.
   * @param sql the SQL statement to prepare
   * @return statement preparer
   */
  public static CallPreparer with(String sql) {
    return new CallPreparer(sql);
  }

  /**
   * Creates a new statement preparer for the given SQL source
   * @param source source for the SQL statement to prepare
   * @return statement preparer
   */
  public static CallPreparer with(SQLSource source) {
    return new CallPreparer(SourceUtils.getSingleStatement(source));
  }

  /**
   * {@inheritDoc}
   */
  protected CallableStatement prepareStatement(Connection connection,
      String sql) throws SQLException {
    return connection.prepareCall(sql);
  }

}
