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

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A closure that handles the result set returned by a query.
 * <p>
 * This interface provides the means for an {@link JdbcQuery} query to
 * process the results of a query in an arbitrary fashion.
 * <p>
 * While the {@link #handleResult(ResultSet)} method can return a value, it is
 * not always necessary.  When an implementation does not need to return a
 * value, simply use {@link Void} as the type parameter.
 * <p>
 * Example:
 * <pre>
 * {@code
 * ResultSetHandler<Void> handler = new ResultSetHandler<>() {
 *   public Void handleResult(ResultSet rs) throws SQLException {
 *     while (rs.next()) {
 *       exporter.exportPerson(rs.getLong("id"), rs.getString("name"));
 *     }
 *     return null;
 *   }
 * };
 * }</pre>
 * @param <T> the type of result returned by the handler
 * @author Carl Harris
 */
public interface ResultSetHandler<T> {

  /**
   * Handles the result set returned by a query.
   * @param rs the result set to process
   * @return any value of type {@code T}
   * @throws SQLException as needed
   */
  T handleResult(ResultSet rs) throws SQLException;

}
