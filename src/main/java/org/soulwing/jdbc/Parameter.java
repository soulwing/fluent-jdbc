/*
 * File created on May 5, 2014 
 *
 * Copyright (c) 2014 Carl Harris, Jr
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
 *
 */
package org.soulwing.jdbc;

import java.lang.reflect.Field;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * An SQL parameter injector.
 *
 * @author Carl Harris
 */
public class Parameter {

  private static final Field[] typeFields;

  private final int type;
  private final Object value;
  private final boolean in;
  private final boolean out;

  static {
    typeFields = Types.class.getFields();
  }

  /**
   * Constructs a new instance.
   * @param type parameter type
   * @param value parameter value
   */
  private Parameter(int type, Object value) {
    this(type, value, true, false);
  }

  /**
   * Constructs a new instance.
   * @param type parameter type
   * @param value parameter value
   * @param in flag indicating an input parameter
   * @param out flag indicating an output parameter
   */
  private Parameter(int type, Object value, boolean in, boolean out) {
    this.type = type;
    this.value = value;
    this.in = in;
    this.out = out;
  }

  /**
   * Creates a new input parameter with the given value.
   * <p>
   * Synonym for {@link #in(Object)}.
   *
   * @param value value of the parameter
   * @return parameter object
   */
  public static Parameter with(Object value) {
    return new Parameter(Types.NULL, value);
  }

  /**
   * Creates a new input parameter with the given value.
   * <p>
   * Synonym for {@link #in(int, Object)}.
   *
   * @param type SQL type of the parameter
   * @param value value of the parameter
   * @return parameter object
   */
  public static Parameter with(int type, Object value) {
    return new Parameter(type, value);
  }

  /**
   * Creates a new input parameter with the given value.
   * @param value value of the parameter
   * @return parameter object
   */
  public static Parameter in(Object value) {
    return in(Types.NULL, value);
  }

  /**
   * Creates a new input parameter with the given value.
   * <p>
   * Synonym for {@link #in(Object)}.
   *
   * @param type SQL type of the parameter
   * @param value value of the parameter
   * @return parameter object
   */
  public static Parameter in(int type, Object value) {
    return new Parameter(type, value, true, false);
  }

  /**
   * Creates a new output parameter with the given type.
   * @param type SQL type of the parameter
   * @return parameter object
   */
  public static Parameter out(int type) {
    return new Parameter(type, null, false, true);
  }

  /**
   * Creates a new input/output parameter with the given type.
   * @param type SQL type of the parameter
   * @param value value of the parameter
   * @return parameter object
   */
  public static Parameter inout(int type, Object value) {
    return new Parameter(type, value, true, true);
  }

  /**
   * Injects this parameter into the given prepared statement.
   * @param parameterIndex index of the statement placeholder (starts at 1)
   * @param statement target statement
   * @throws SQLException
   */
  public void inject(int parameterIndex, PreparedStatement statement)
      throws SQLException {
    if (statement instanceof CallableStatement && out) {
      ((CallableStatement) statement).registerOutParameter(parameterIndex, type);
    }
    if (!in) return;
    if (value instanceof BlobHandler) {
      final Blob blob = statement.getConnection().createBlob();
      ((BlobHandler) value).prepareBlob(blob);
      statement.setBlob(parameterIndex, blob);
    }
    else if (value instanceof ClobHandler) {
      final Clob clob = statement.getConnection().createClob();
      ((ClobHandler) value).prepareClob(clob);
      statement.setClob(parameterIndex, clob);
    }
    else if (value instanceof NClobHandler) {
      final NClob nClob = statement.getConnection().createNClob();
      ((NClobHandler) value).prepareNClob(nClob);
      statement.setNClob(parameterIndex, nClob);
    }
    else if (value == null) {
      statement.setNull(parameterIndex, type);
    }
    else if (type == Types.NULL) {
      statement.setObject(parameterIndex, value);
    }
    else {
      statement.setObject(parameterIndex, value, type);
    }
  }

  /**
   * Gets the {@code type} property.
   * @return property value
   */
  public int getType() {
    return type;
  }

  /**
   * Gets the {@code value} property.
   * @return property value
   */
  public Object getValue() {
    return value;
  }

  /**
   * Gets the {@code in} property.
   * @return property value
   */
  boolean isIn() {
    return in;
  }

  /**
   * Gets the {@code out} property.
   * @return property value
   */
  boolean isOut() {
    return out;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (in) {
      sb.append("IN");
    }
    if (out) {
      sb.append("OUT");
    }
    if (in) {
      sb.append(" value={");
      sb.append(value);
      sb.append("}");
    }
    if (type != Types.NULL) {
      sb.append(" type=");
      sb.append(typeToString(type));
    }
    return sb.toString();
  }

  /**
   * Produces a string representation of this parameter prefixed with a given
   * index indicating its position in a parameter list.
   * @param index the index value to include in the representation
   * @return parameter description
   */
  public String toString(int index) {
    StringBuilder sb = new StringBuilder();
    sb.append("parameter[");
    sb.append(index);
    sb.append("]: ");
    sb.append(toString());
    return sb.toString();
  }

  private static String typeToString(int type) {
    for (Field field : typeFields) {
      try {
        int value = field.getInt(Types.class);
        if (value == type) {
          return field.getName();
        }
      }
      catch (IllegalAccessException ex) {
        assert true;   // oh, well
      }
    }
    return Integer.toString(type);
  }

}
