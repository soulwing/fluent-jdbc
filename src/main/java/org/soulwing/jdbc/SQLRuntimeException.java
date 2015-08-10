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

/**
 * A runtime exception that wraps an {@link java.sql.SQLException}.
 *
 * @author Carl Harris
 */
public class SQLRuntimeException extends RuntimeException {

  private static final long serialVersionUID = -409813449560593560L;

  /**
   * Constructs a new instance.
   */
  public SQLRuntimeException() {
    super();
  }

  /**
   * Constructs a new instance.
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public SQLRuntimeException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  /**
   * Constructs a new instance.
   * @param message
   * @param cause
   */
  public SQLRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new instance.
   * @param message
   */
  public SQLRuntimeException(String message) {
    super(message);
  }

  /**
   * Constructs a new instance.
   * @param cause
   */
  public SQLRuntimeException(Throwable cause) {
    super(cause);
  }

}
