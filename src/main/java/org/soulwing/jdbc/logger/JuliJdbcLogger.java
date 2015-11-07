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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.soulwing.jdbc.Parameter;

/**
 * A {@link JdbcLogger} that delegates to {@link java.util.logging.Logger}.
 * <p>
 * SQL statements are logged at the {@code FINE} level.  Bound parameter values
 * are logged at the {@code FINEST} level.
 *
 * @author Carl Harris
 */
public class JuliJdbcLogger implements JdbcLogger {

  private final Logger logger;

  /**
   * Constructs a new instance.
   * @param logger the delegate logger
   */
  public JuliJdbcLogger(Logger logger) {
    this.logger = logger;
  }

  @Override
  public void writeStatement(String sql) {
    if (logger.isLoggable(Level.FINE)) {
      logger.fine(sql);
    }
  }

  @Override
  public void writeParameters(Parameter[] parameters) {
    if (logger.isLoggable(Level.FINEST)) {
      for (int index = 0, max = parameters.length; index < max; index++) {
        logger.finest(parameters[index].toString(index));
      }
    }
  }
  
}
