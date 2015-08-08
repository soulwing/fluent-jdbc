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

import org.soulwing.sql.SQLRuntimeException;

/**
 * An exception thrown by an {@link SQLSource} when a resource cannot be found.
 *
 * @author Carl Harris
 */
public class SQLInputException extends SQLRuntimeException {

  public SQLInputException(String message, Throwable cause) {
    super(message, cause);
  }

}
