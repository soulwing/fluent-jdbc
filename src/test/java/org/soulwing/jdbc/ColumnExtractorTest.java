/*
 * File created on Aug 9, 2015
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
import static org.hamcrest.Matchers.sameInstance;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Time;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

/**
 * Unit tests for {@link ColumnExtractor}.
 *
 * @author Carl Harris
 */
public class ColumnExtractorTest {

  private static final int INDEX = 1;

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

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private ResultSet rs;

  @Test
  public void testExtractByIndex() throws Exception {
    final int index = -1;
    final Object result = new Object();
    context.checking(new Expectations() {
      {
        oneOf(rs).getObject(index, Object.class);
        will(returnValue(result));
      }
    });

    assertThat(ColumnExtractor.with(index, Object.class).extract(rs),
        is(sameInstance(result)));
  }

  @Test
  public void testExtractByLabel() throws Exception {
    final String label = "someLabel";
    final int index = -1;
    final Object result = new Object();
    context.checking(new Expectations() {
      {
        oneOf(rs).getObject(label, Object.class);
        will(returnValue(result));
      }
    });

    assertThat(ColumnExtractor.with(label, Object.class).extract(rs),
        is(sameInstance(result)));
  }


  @Test
  public void testExtractString() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(rs).getString(INDEX);
        will(returnValue(STRING));
      }
    });

    assertThat(ColumnExtractor.with(String.class).extract(rs),
        is(equalTo(STRING)));
  }

  @Test
  public void testExtractInteger() throws Exception {
    context.checking(new Expectations() {
      {
        exactly(2).of(rs).getInt(INDEX);
        will(returnValue(INT));
      }
    });

    assertThat(ColumnExtractor.with(int.class).extract(rs), is(equalTo(INT)));
    assertThat(ColumnExtractor.with(Integer.class).extract(rs), is(equalTo(INT)));
  }

  @Test
  public void testExtractLong() throws Exception {
    context.checking(new Expectations() {
      {
        exactly(2).of(rs).getLong(INDEX);
        will(returnValue(LONG));
      }
    });

    assertThat(ColumnExtractor.with(long.class).extract(rs), is(equalTo(LONG)));
    assertThat(ColumnExtractor.with(Long.class).extract(rs), is(equalTo(LONG)));
  }

  @Test
  public void testExtractShort() throws Exception {
    context.checking(new Expectations() {
      {
        exactly(2).of(rs).getShort(INDEX);
        will(returnValue(SHORT));
      }
    });

    assertThat(ColumnExtractor.with(short.class).extract(rs), is(equalTo(SHORT)));
    assertThat(ColumnExtractor.with(Short.class).extract(rs), is(equalTo(SHORT)));
  }

  @Test
  public void testExtractByte() throws Exception {
    context.checking(new Expectations() {
      {
        exactly(2).of(rs).getByte(INDEX);
        will(returnValue(BYTE));
      }
    });

    assertThat(ColumnExtractor.with(byte.class).extract(rs), is(equalTo(BYTE)));
    assertThat(ColumnExtractor.with(Byte.class).extract(rs), is(equalTo(BYTE)));
  }

  @Test
  public void testExtractDouble() throws Exception {
    context.checking(new Expectations() {
      {
        exactly(2).of(rs).getDouble(INDEX);
        will(returnValue(DOUBLE));
      }
    });

    assertThat(ColumnExtractor.with(double.class).extract(rs), is(equalTo(DOUBLE)));
    assertThat(ColumnExtractor.with(Double.class).extract(rs), is(equalTo(DOUBLE)));
  }

  @Test
  public void testExtractFloat() throws Exception {
    context.checking(new Expectations() {
      {
        exactly(2).of(rs).getFloat(INDEX);
        will(returnValue(FLOAT));
      }
    });

    assertThat(ColumnExtractor.with(float.class).extract(rs), is(equalTo(FLOAT)));
    assertThat(ColumnExtractor.with(Float.class).extract(rs), is(equalTo(FLOAT)));
  }

  @Test
  public void testExtractBoolean() throws Exception {
    context.checking(new Expectations() {
      {
        exactly(2).of(rs).getBoolean(INDEX);
        will(returnValue(BOOLEAN));
      }
    });

    assertThat(ColumnExtractor.with(boolean.class).extract(rs), is(equalTo(BOOLEAN)));
    assertThat(ColumnExtractor.with(Boolean.class).extract(rs), is(equalTo(BOOLEAN)));
  }

  @Test
  public void testExtractDate() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(rs).getDate(INDEX);
        will(returnValue(DATE));
      }
    });

    assertThat(ColumnExtractor.with(Date.class).extract(rs), is(equalTo(DATE)));
  }

  @Test
  public void testExtractTime() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(rs).getTime(INDEX);
        will(returnValue(TIME));
      }
    });

    assertThat(ColumnExtractor.with(Time.class).extract(rs), is(equalTo(TIME)));
  }

}
