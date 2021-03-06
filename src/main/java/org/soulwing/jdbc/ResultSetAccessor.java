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

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * A {@link ValueAccessor} that delegates to a {@link ResultSet}.
 * @author Carl Harris
 */
class ResultSetAccessor extends ValueAccessor {

  private final ResultSet delegate;

  private ResultSetAccessor(ResultSet delegate) {
    this.delegate = delegate;
  }

  public static ResultSetAccessor with(ResultSet delegate) {
    return new ResultSetAccessor(delegate);
  }

  @Override
  public String getString(int columnIndex) throws SQLException {
    return delegate.getString(columnIndex);
  }

  @Override
  public boolean getBoolean(int columnIndex) throws SQLException {
    return delegate.getBoolean(columnIndex);
  }

  @Override
  public byte getByte(int columnIndex) throws SQLException {
    return delegate.getByte(columnIndex);
  }

  @Override
  public short getShort(int columnIndex) throws SQLException {
    return delegate.getShort(columnIndex);
  }

  @Override
  public int getInt(int columnIndex) throws SQLException {
    return delegate.getInt(columnIndex);
  }

  @Override
  public long getLong(int columnIndex) throws SQLException {
    return delegate.getLong(columnIndex);
  }

  @Override
  public float getFloat(int columnIndex) throws SQLException {
    return delegate.getFloat(columnIndex);
  }

  @Override
  public double getDouble(int columnIndex) throws SQLException {
    return delegate.getDouble(columnIndex);
  }

  @Override
  public Date getDate(int columnIndex) throws SQLException {
    return delegate.getDate(columnIndex);
  }

  @Override
  public Time getTime(int columnIndex) throws SQLException {
    return delegate.getTime(columnIndex);
  }

  @Override
  public Timestamp getTimestamp(int columnIndex) throws SQLException {
    return delegate.getTimestamp(columnIndex);
  }

  @Override
  public Blob getBlob(int columnIndex) throws SQLException {
    return delegate.getBlob(columnIndex);
  }

  @Override
  public Clob getClob(int columnIndex) throws SQLException {
    return delegate.getClob(columnIndex);
  }

  @Override
  public NClob getNClob(int columnIndex) throws SQLException {
    return delegate.getNClob(columnIndex);
  }

  @Override
  public Object getObject(String label, Class<?> type) throws SQLException {
    return delegate.getObject(label, type);
  }

  @Override
  public String getString(String columnLabel) throws SQLException {
    return delegate.getString(columnLabel);
  }

  @Override
  public boolean getBoolean(String columnLabel) throws SQLException {
    return delegate.getBoolean(columnLabel);
  }

  @Override
  public byte getByte(String columnLabel) throws SQLException {
    return delegate.getByte(columnLabel);
  }

  @Override
  public short getShort(String columnLabel) throws SQLException {
    return delegate.getShort(columnLabel);
  }

  @Override
  public int getInt(String columnLabel) throws SQLException {
    return delegate.getInt(columnLabel);
  }

  @Override
  public long getLong(String columnLabel) throws SQLException {
    return delegate.getLong(columnLabel);
  }

  @Override
  public float getFloat(String columnLabel) throws SQLException {
    return delegate.getFloat(columnLabel);
  }

  @Override
  public double getDouble(String columnLabel) throws SQLException {
    return delegate.getDouble(columnLabel);
  }

  @Override
  public Date getDate(String columnLabel) throws SQLException {
    return delegate.getDate(columnLabel);
  }

  @Override
  public Time getTime(String columnLabel) throws SQLException {
    return delegate.getTime(columnLabel);
  }

  @Override
  public Timestamp getTimestamp(String columnLabel) throws SQLException {
    return delegate.getTimestamp(columnLabel);
  }

  @Override
  public Blob getBlob(String columnLabel) throws SQLException {
    return delegate.getBlob(columnLabel);
  }

  @Override
  public Clob getClob(String columnLabel) throws SQLException {
    return delegate.getClob(columnLabel);
  }

  @Override
  public NClob getNClob(String columnLabel) throws SQLException {
    return delegate.getNClob(columnLabel);
  }

  @Override
  public Object getObject(int index, Class<?> type) throws SQLException {
    return delegate.getObject(index, type);
  }

}
