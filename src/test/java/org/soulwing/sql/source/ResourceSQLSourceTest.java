/*
 * File created on Aug 6, 2015
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
package org.soulwing.sql.source;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.net.URI;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link ResourceSQLSource}.
 *
 * @author Carl Harris
 */
public class ResourceSQLSourceTest {

  private static final String TEST_PATH = "testSource/";

  private static final String NAME_UTF8 = "testSource.txt";

  private static final String NAME_UTF16 = "testSource-UTF16.txt";

  private static final String TEXT_UTF8 = "Resource in UTF-8 encoding";

  private static final String TEXT_UTF16 = "Resource in UTF-16 encoding";

  private static final String UTF_16 = "UTF-16BE";

  @Test
  public void testResourceLocationUsingClass() throws Exception {
    final ResourceSQLSource source = ResourceSQLSource.with(
        NAME_UTF8, getClass());
    final String next = source.next();
    assertThat(next, is(equalTo(TEXT_UTF8)));
    source.close();
  }

  @Test
  public void testResourceLocationUsingClassAndEncoding() throws Exception {
    final ResourceSQLSource source =ResourceSQLSource.with(
        NAME_UTF16, getClass(), UTF_16);
    final String next = source.next();
    assertThat(next, is(equalTo(TEXT_UTF16)));
    source.close();
  }

  @Test
  public void testResourceLocationUsingClassLoader() throws Exception {
    final ResourceSQLSource source = ResourceSQLSource.with(
        TEST_PATH + NAME_UTF8, getClass().getClassLoader());
    final String next = source.next();
    assertThat(next, is(equalTo(TEXT_UTF8)));
    source.close();
  }

  @Test
  public void testResourceLocationUsingClassLoaderAndEncoding() throws Exception {
    final ResourceSQLSource source = ResourceSQLSource.with(
        TEST_PATH + NAME_UTF16, getClass().getClassLoader(), UTF_16);
    final String next = source.next();
    assertThat(next, is(equalTo(TEXT_UTF16)));
    source.close();
  }

  @Test
  public void testResourceLocationUsingClasspathUri() throws Exception {
    final ResourceSQLSource source = ResourceSQLSource.with(
       "classpath:" + TEST_PATH + NAME_UTF8);
    final String next = source.next();
    assertThat(next, is(equalTo(TEXT_UTF8)));
    source.close();
  }

  @Test
  public void testResourceLocationUsingClasspathUriAndEncoding() throws Exception {
    final ResourceSQLSource source = ResourceSQLSource.with(
        "classpath:" + TEST_PATH + NAME_UTF16, UTF_16);
    final String next = source.next();
    assertThat(next, is(equalTo(TEXT_UTF16)));
    source.close();
  }

}
