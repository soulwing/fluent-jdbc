/*
 * File created on Aug 18, 2015
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
package org.soulwing.jdbc.logger;

import org.slf4j.Logger;
import org.soulwing.jdbc.Parameter;
import org.soulwing.jdbc.logger.JdbcLogger;

/**
 * A {@link JdbcLogger} that delegates to an
 * <a href="http://slf4j.org">slf4j</a> {@code Logger}.
 * <p>
 * In order to use this class, you must include <em>slf4j</em> on the classpath.
 * <p>
 * SQL statements are logged at the {@code DEBUG} level.  Bound parameter values
 * are logged at the {@code TRACE} level.
 *
 * @author Carl Harris
 */
public class Slf4jJdbcLogger implements JdbcLogger {

  private final Logger logger;

  /**
   * Constructs a new instance.
   * @param logger the delegate logger
   */
  public Slf4jJdbcLogger(Logger logger) {
    this.logger = logger;
  }

  @Override
  public void writeStatement(String sql) {
    if (logger.isDebugEnabled()) {
      logger.debug(sql);
    }
  }

  @Override
  public void writeParameters(Parameter[] parameters) {
    if (logger.isTraceEnabled()) {
      for (int index = 0, max = parameters.length; index < max; index++) {
        logger.trace(parameters[index].toString(index));
      }
    }
  }

}
