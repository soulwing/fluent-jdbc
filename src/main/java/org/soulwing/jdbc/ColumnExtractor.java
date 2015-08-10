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
 * A closure that extracts a single column value from a {@link ResultSet}.
 *
 * @author Carl Harris
 */
class ColumnExtractor<T> {

  private final Object columnId;
  private final Class<T> type;

  private ColumnExtractor(Object columnId, Class<T> type) {
    this.columnId = columnId;
    this.type = type;
  }

  /**
   * Creates an extractor for the first column of a result set.
   * @param type data type of the column
   * @return extractor
   */
  public static <T> ColumnExtractor<T> with(Class<T> type) {
    return new ColumnExtractor<>(1, type);
  }

  /**
   * Creates an extractor for the given column of a result set.
   * @param columnIndex (index values begin at 1)
   * @param type data type of the column
   * @return extractor
   */
  public static <T> ColumnExtractor<T> with(int columnIndex, Class<T> type) {
    return new ColumnExtractor<>(columnIndex, type);
  }

  /**
   * Creates an extractor for the given column of a result set.
   * @param columnLabel label associated with the column
   * @param type data type of the column
   * @return extractor
   */
  public static <T> ColumnExtractor<T> with(String columnLabel, Class<T> type) {
    return new ColumnExtractor<>(columnLabel, type);
  }

  /**
   * Extracts the value of the configured column from the current row of
   * the given result set.
   * @param rs subject result set
   * @return column value
   * @throws SQLException
   */
  public T extract(ResultSet rs) throws SQLException {
    if (columnId instanceof String) {
      return ResultSetAccessor.with(rs).get((String) columnId, type);
    }
    else {
      return ResultSetAccessor.with(rs).get((int) columnId, type);
    }
  }

}
