/*
 * File created on Aug 12, 2015
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

import java.sql.Connection;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * Tests for {@link SingleConnectionDataSource}.
 *
 * @author Carl Harris
 */
public class SingleConnectionDataSourceTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private Connection connection;

  @Test
  public void testCloseConnection() throws Exception {
    context.checking(new Expectations() { {} });
    SingleConnectionDataSource dataSource = new SingleConnectionDataSource(
        connection);
    Connection wrapper = dataSource.getConnection();
    wrapper.close();
  }

}
