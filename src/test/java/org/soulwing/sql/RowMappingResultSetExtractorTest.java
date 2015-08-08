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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import java.sql.ResultSet;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Unit tests for {@link RowMappingResultSetExtractor}.
 *
 * @author Carl Harris
 */
public class RowMappingResultSetExtractorTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private ResultSet rs;

  @Mock
  private RowMapper<Object> rowMapper;

  private Object result1 = new Object();

  private Object result2 = new Object();

  private RowMappingResultSetExtractor<Object> extractor;

  @Before
  public void setUp() throws Exception {
    extractor = new RowMappingResultSetExtractor<>(rowMapper);
  }

  @Test
  public void testExtract() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(rowMapper).mapRow(rs, 1);
        will(returnValue(result1));
        oneOf(rowMapper).mapRow(rs, 2);
        will(returnValue(result2));
      }
    });

    assertThat(extractor.extract(rs), is(sameInstance(result1)));
    assertThat(extractor.extract(rs), is(sameInstance(result2)));
  }

}
