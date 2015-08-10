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
package org.soulwing.jdbc.source;

import java.io.Closeable;

/**
 * A source of SQL statements.
 * <p>
 * An instance of this type abstracts the details of reading the text of an
 * SQL source file and parsing it into a sequence of statements.
 *
 * @author Carl Harris
 */
public interface SQLSource extends Closeable, AutoCloseable {

  /**
   * Gets the next statement from this source.
   * @return statement or {@code null} if the end of the source has been reached
   * @throws SQLInputException
   */
  String next() throws SQLInputException;

}
