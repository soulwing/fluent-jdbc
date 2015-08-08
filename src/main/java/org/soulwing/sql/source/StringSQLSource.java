/*
 * File created on Aug 5, 2015
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

import java.io.StringReader;

/**
 * An {@link SQLSource} that reads statements from a string.
 *
 * @author Carl Harris
 */
public class StringSQLSource extends ReaderSQLSource {

  /**
   * Constructs a new instance.
   * @param sql source SQL
   */
  public StringSQLSource(String sql) {
    super(new StringReader(sql));
  }

}
