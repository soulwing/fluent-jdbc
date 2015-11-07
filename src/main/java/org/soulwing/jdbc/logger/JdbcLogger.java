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

import org.soulwing.jdbc.Parameter;

/**
 * A simplified log writer interface for logging SQL statements and bound
 * parameter values.
 * <p>
 * An implementation of this class typically delegates to a logging framework.
 *
 * @author Carl Harris
 */
public interface JdbcLogger {

  /**
   * Writes the text of an SQL statement to the log.
   * @param sql the SQL statement text to write
   */
  void writeStatement(String sql);

  /**
   * Writes the parameters that will be bound for an SQL statement.
   * <p>
   * This method is invoked immediately after {@link #writeStatement(String)} to
   * log the actual parameter values that will be used to replace statement
   * placeholders.
   * <p>
   * An implementation should check the configuration of the underlying log
   * system as to whether this level of detail is enabled.
   *
   * @param parameters values that will be bound before statement execution
   */
  void writeParameters(Parameter[] parameters);

}
