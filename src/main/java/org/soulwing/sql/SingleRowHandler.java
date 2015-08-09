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

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A {@link ResultSetHandler} that extracts a result from (what must be)
 * a {@link ResultSet} containing exactly one row.
 *
 * @author Carl Harris
 */
class SingleRowHandler<T> implements ResultSetHandler<T> {

  private final ResultSetHandler<T> delegate;

  public SingleRowHandler(ResultSetHandler<T> delegate) {
    this.delegate = delegate;
  }

  @Override
  public T handleResult(ResultSet rs) throws SQLException {
    if (rs.next()) {
      final T result = delegate.handleResult(rs);
      if (rs.next()) {
        throw new SQLNonUniqueResultException();
      }
      return result;
    }
    else {
      throw new SQLNoResultException();
    }
  }

}
