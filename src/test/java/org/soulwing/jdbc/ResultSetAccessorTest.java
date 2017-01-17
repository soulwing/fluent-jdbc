/*
 * File created on Aug 11, 2015
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Unit tests for {@link CallableStatementAccessor}.
 *
 * @author Carl Harris
 */
public class ResultSetAccessorTest {

  private static final String LABEL = "someLabel";
  private static final int INDEX = -1;

  private static final String STRING = new String();

  private static final int INT = -1;
  private static final long LONG = -2L;
  private static final short SHORT = -3;
  private static final byte BYTE = -4;
  private static final double DOUBLE = -5.0;
  private static final float FLOAT = -6.0f;

  private static final boolean BOOLEAN = true;

  private static final Date DATE = new Date(0);
  private static final Time TIME = new Time(0);
  private static final Timestamp TIMESTAMP = new Timestamp(0);

  private static final Object OBJECT = new Object();


  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private ResultSet statement;

  @Mock
  private Blob blob;

  @Mock
  private Clob clob;

  @Mock
  private NClob nClob;

  private ResultSetAccessor accessor;

  @Before
  public void setUp() throws Exception {
    accessor = ResultSetAccessor.with(statement);
  }

  @Test
  public void testGetStringLabel() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(statement).getString(LABEL);
        will(returnValue(STRING));
      }
    });

    assertThat(accessor.getString(LABEL), is(equalTo(STRING)));
  }

  @Test
  public void testGetIntLabel() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(statement).getInt(LABEL);
        will(returnValue(INT));
      }
    });

    assertThat(accessor.getInt(LABEL), is(equalTo(INT)));
  }

  @Test
  public void testGetLongLabel() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(statement).getLong(LABEL);
        will(returnValue(LONG));
      }
    });

    assertThat(accessor.getLong(LABEL), is(equalTo(LONG)));
  }

  @Test
  public void testGetShortLabel() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(statement).getShort(LABEL);
        will(returnValue(SHORT));
      }
    });

    assertThat(accessor.getShort(LABEL), is(equalTo(SHORT)));
  }

  @Test
  public void testGetByteLabel() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(statement).getByte(LABEL);
        will(returnValue(BYTE));
      }
    });

    assertThat(accessor.getByte(LABEL), is(equalTo(BYTE)));
  }

  @Test
  public void testGetDoubleLabel() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(statement).getDouble(LABEL);
        will(returnValue(DOUBLE));
      }
    });

    assertThat(accessor.getDouble(LABEL), is(equalTo(DOUBLE)));
  }

  @Test
  public void testGetFloatLabel() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(statement).getFloat(LABEL);
        will(returnValue(FLOAT));
      }
    });

    assertThat(accessor.getFloat(LABEL), is(equalTo(FLOAT)));
  }

  @Test
  public void testGetDateLabel() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(statement).getDate(LABEL);
        will(returnValue(DATE));
      }
    });

    assertThat(accessor.getDate(LABEL), is(equalTo(DATE)));
  }

  @Test
  public void testGetTimeLabel() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(statement).getTime(LABEL);
        will(returnValue(TIME));
      }
    });

    assertThat(accessor.getTime(LABEL), is(equalTo(TIME)));
  }

  @Test
  public void testGetTimestampLabel() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(statement).getTimestamp(LABEL);
        will(returnValue(TIMESTAMP));
      }
    });

    assertThat(accessor.getTimestamp(LABEL), is(equalTo(TIMESTAMP)));
  }

  @Test
  public void testGetBlobLabel() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(statement).getBlob(LABEL);
        will(returnValue(blob));
      }
    });

    assertThat(accessor.getBlob(LABEL), is(equalTo(blob)));
  }

  @Test
  public void testGetClobLabel() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(statement).getClob(LABEL);
        will(returnValue(clob));
      }
    });

    assertThat(accessor.getClob(LABEL), is(equalTo(clob)));
  }

  @Test
  public void testGetNClobLabel() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(statement).getNClob(LABEL);
        will(returnValue(nClob));
      }
    });

    assertThat(accessor.getNClob(LABEL), is(equalTo(nClob)));
  }

  @Test
  public void testGetObjectLabel() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(statement).getObject(LABEL, Object.class);
        will(returnValue(OBJECT));
      }
    });

    assertThat(accessor.getObject(LABEL, Object.class), is(equalTo(OBJECT)));
  }
  
  @Test
  public void testGetStringIndex() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(statement).getString(INDEX);
        will(returnValue(STRING));
      }
    });

    assertThat(accessor.getString(INDEX), is(equalTo(STRING)));
  }

  @Test
  public void testGetIntIndex() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(statement).getInt(INDEX);
        will(returnValue(INT));
      }
    });

    assertThat(accessor.getInt(INDEX), is(equalTo(INT)));
  }

  @Test
  public void testGetLongIndex() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(statement).getLong(INDEX);
        will(returnValue(LONG));
      }
    });

    assertThat(accessor.getLong(INDEX), is(equalTo(LONG)));
  }

  @Test
  public void testGetShortIndex() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(statement).getShort(INDEX);
        will(returnValue(SHORT));
      }
    });

    assertThat(accessor.getShort(INDEX), is(equalTo(SHORT)));
  }

  @Test
  public void testGetByteIndex() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(statement).getByte(INDEX);
        will(returnValue(BYTE));
      }
    });

    assertThat(accessor.getByte(INDEX), is(equalTo(BYTE)));
  }

  @Test
  public void testGetDoubleIndex() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(statement).getDouble(INDEX);
        will(returnValue(DOUBLE));
      }
    });

    assertThat(accessor.getDouble(INDEX), is(equalTo(DOUBLE)));
  }

  @Test
  public void testGetFloatIndex() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(statement).getFloat(INDEX);
        will(returnValue(FLOAT));
      }
    });

    assertThat(accessor.getFloat(INDEX), is(equalTo(FLOAT)));
  }

  @Test
  public void testGetDateIndex() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(statement).getDate(INDEX);
        will(returnValue(DATE));
      }
    });

    assertThat(accessor.getDate(INDEX), is(equalTo(DATE)));
  }

  @Test
  public void testGetTimeIndex() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(statement).getTime(INDEX);
        will(returnValue(TIME));
      }
    });

    assertThat(accessor.getTime(INDEX), is(equalTo(TIME)));
  }

  @Test
  public void testGetTimestampIndex() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(statement).getTimestamp(INDEX);
        will(returnValue(TIMESTAMP));
      }
    });

    assertThat(accessor.getTimestamp(INDEX), is(equalTo(TIMESTAMP)));
  }

  @Test
  public void testGetBlobIndex() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(statement).getBlob(INDEX);
        will(returnValue(blob));
      }
    });

    assertThat(accessor.getBlob(INDEX), is(equalTo(blob)));
  }

  @Test
  public void testGetClobIndex() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(statement).getClob(INDEX);
        will(returnValue(clob));
      }
    });

    assertThat(accessor.getClob(INDEX), is(equalTo(clob)));
  }

  @Test
  public void testGetNClobIndex() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(statement).getNClob(INDEX);
        will(returnValue(nClob));
      }
    });

    assertThat(accessor.getNClob(INDEX), is(equalTo(nClob)));
  }

  @Test
  public void testGetObjectIndex() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(statement).getObject(INDEX, Object.class);
        will(returnValue(OBJECT));
      }
    });

    assertThat(accessor.getObject(INDEX, Object.class), is(equalTo(OBJECT)));
  }
  
}
