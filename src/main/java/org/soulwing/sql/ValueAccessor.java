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
package org.soulwing.sql;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * An object that provides methods for accessing a data value in a JDBC
 * object such as a {@link ResultSet}.
 *
 * @author Carl Harris
 */
abstract class ValueAccessor {

  @SuppressWarnings("unchecked")
  public <T> T get(int index, Class<T> type)
      throws SQLException {
    if (String.class.equals(type)) {
      return (T) this.getString(index);
    }
    if (int.class.equals(type) || Integer.class.equals(type)) {
      return (T) (Integer) this.getInt(index);
    }
    if (long.class.equals(type) || Long.class.equals(type)) {
      return (T) (Long) this.getLong(index);
    }
    if (boolean.class.equals(type) || Boolean.class.equals(type)) {
      return (T) (Boolean) this.getBoolean(index);
    }
    if (double.class.equals(type) || Double.class.equals(type)) {
      return (T) (Double) this.getDouble(index);
    }
    if (float.class.equals(type) || Float.class.equals(type)) {
      return (T) (Float) this.getFloat(index);
    }
    if (short.class.equals(type) || Short.class.equals(type)) {
      return (T) (Short) this.getShort(index);
    }
    if (byte.class.equals(type) || Byte.class.equals(type)) {
      return (T) (Byte) this.getByte(index);
    }
    if (Date.class.isAssignableFrom(type)) {
      return (T) this.getDate(index);
    }
    if (Time.class.isAssignableFrom(type)) {
      return (T) this.getTime(index);
    }
    return (T) this.getObject(index, type);
  }

  @SuppressWarnings("unchecked")
  public <T> T get(String label, Class<T> type)
      throws SQLException {
    if (String.class.equals(type)) {
      return (T) this.getString(label);
    }
    if (int.class.equals(type) || Integer.class.equals(type)) {
      return (T) (Integer) this.getInt(label);
    }
    if (long.class.equals(type) || Long.class.equals(type)) {
      return (T) (Long) this.getLong(label);
    }
    if (boolean.class.equals(type) || Boolean.class.equals(type)) {
      return (T) (Boolean) this.getBoolean(label);
    }
    if (double.class.equals(type) || Double.class.equals(type)) {
      return (T) (Double) this.getDouble(label);
    }
    if (float.class.equals(type) || Float.class.equals(type)) {
      return (T) (Float) this.getFloat(label);
    }
    if (short.class.equals(type) || Short.class.equals(type)) {
      return (T) (Short) this.getShort(label);
    }
    if (byte.class.equals(type) || Byte.class.equals(type)) {
      return (T) (Byte) this.getByte(label);
    }
    if (Date.class.isAssignableFrom(type)) {
      return (T) this.getDate(label);
    }
    if (Time.class.isAssignableFrom(type)) {
      return (T) this.getTime(label);
    }
    return (T) this.getObject(label, type);
  }


  abstract String getString(String label) throws SQLException;

  abstract String getString(int index) throws SQLException;

  abstract int getInt(String label) throws SQLException;

  abstract int getInt(int index) throws SQLException;

  abstract long getLong(String label) throws SQLException;

  abstract long getLong(int index) throws SQLException;

  abstract boolean getBoolean(String label) throws SQLException;

  abstract boolean getBoolean(int index) throws SQLException;

  abstract byte getByte(String label) throws SQLException;

  abstract byte getByte(int index) throws SQLException;

  abstract short getShort(String label) throws SQLException;

  abstract short getShort(int index) throws SQLException;

  abstract double getDouble(String label) throws SQLException;

  abstract double getDouble(int index) throws SQLException;

  abstract float getFloat(String label) throws SQLException;

  abstract float getFloat(int index) throws SQLException;

  abstract Timestamp getTimestamp(String label) throws SQLException;

  abstract Timestamp getTimestamp(int index) throws SQLException;

  abstract Date getDate(String label) throws SQLException;

  abstract Date getDate(int index) throws SQLException;

  abstract Time getTime(String label) throws SQLException;

  abstract Time getTime(int index) throws SQLException;

  abstract Object getObject(String label, Class<?> type) throws SQLException;

  abstract Object getObject(int index, Class<?> type) throws SQLException;

}
