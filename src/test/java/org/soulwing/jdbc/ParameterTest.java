/*
 * File created on Nov 7, 2015
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
import java.sql.PreparedStatement;
import java.sql.Types;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * Unit tests for the {@link Parameter} type.
 *
 * @author Carl Harris
 */
public class ParameterTest {

  private static final int INDEX = 1;

  private static final int TYPE = Types.VARCHAR;

  private static final Object VALUE = new Object();


  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private PreparedStatement preparedStatement;

  @Mock
  private CallableStatement callableStatement;

  @Test
  public void testInjectPreparedStatementValue() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(preparedStatement).setObject(INDEX, VALUE);
      }
    });

    Parameter.with(VALUE).inject(INDEX, preparedStatement);
  }

  @Test
  public void testInjectPreparedStatementValueAndType() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(preparedStatement).setObject(INDEX, VALUE, TYPE);
      }
    });

    Parameter.with(TYPE, VALUE).inject(INDEX, preparedStatement);
  }

  @Test
  public void testInjectPreparedStatementNull() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(preparedStatement).setNull(INDEX, Types.NULL);
      }
    });

    Parameter.with(null).inject(INDEX, preparedStatement);
  }

  @Test
  public void testInjectPreparedStatementNullType() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(preparedStatement).setNull(INDEX, TYPE);
      }
    });

    Parameter.with(TYPE, null).inject(INDEX, preparedStatement);
  }

  @Test
  public void testInjectCallableStatementOutParameter() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(callableStatement).registerOutParameter(INDEX, TYPE);
      }
    });

    Parameter.out(TYPE).inject(INDEX, callableStatement);
  }

  @Test
  public void testInjectCallableStatementInOutParameterValue() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(callableStatement).registerOutParameter(INDEX, TYPE);
        oneOf(callableStatement).setObject(INDEX, VALUE, TYPE);
      }
    });

    Parameter.inout(TYPE, VALUE).inject(INDEX, callableStatement);
  }

  @Test
  public void testInjectCallableStatementInOutParameterNull() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(callableStatement).registerOutParameter(INDEX, TYPE);
        oneOf(callableStatement).setNull(INDEX, TYPE);
      }
    });

    Parameter.inout(TYPE, null).inject(INDEX, callableStatement);
  }

}
