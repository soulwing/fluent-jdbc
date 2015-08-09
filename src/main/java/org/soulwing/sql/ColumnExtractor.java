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

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

/**
 * A closure that extracts a single column value from a {@link ResultSet}.
 *
 * @author Carl Harris
 */
public class ColumnExtractor<T> {

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
      return extract(rs, rs.findColumn((String) columnId));
    }
    else {
      return extract(rs, (int) columnId);
    }
  }

  @SuppressWarnings("unchecked")
  private T extract(ResultSet rs, int columnIndex) throws SQLException {
    if (String.class.equals(type)) {
      return (T) rs.getString(columnIndex);
    }
    if (int.class.equals(type) || Integer.class.equals(type)) {
      return (T) (Integer) rs.getInt(columnIndex);
    }
    if (long.class.equals(type) || Long.class.equals(type)) {
      return (T) (Long) rs.getLong(columnIndex);
    }
    if (boolean.class.equals(type) || Boolean.class.equals(type)) {
      return (T) (Boolean) rs.getBoolean(columnIndex);
    }
    if (double.class.equals(type) || Double.class.equals(type)) {
      return (T) (Double) rs.getDouble(columnIndex);
    }
    if (float.class.equals(type) || Float.class.equals(type)) {
      return (T) (Float) rs.getFloat(columnIndex);
    }
    if (short.class.equals(type) || Short.class.equals(type)) {
      return (T) (Short) rs.getShort(columnIndex);
    }
    if (byte.class.equals(type) || Byte.class.equals(type)) {
      return (T) (Byte) rs.getByte(columnIndex);
    }
    if (Date.class.isAssignableFrom(type)) {
      return (T) rs.getDate(columnIndex);
    }
    if (Time.class.isAssignableFrom(type)) {
      return (T) rs.getTime(columnIndex);
    }
    return rs.getObject(columnIndex, type);
  }

}
