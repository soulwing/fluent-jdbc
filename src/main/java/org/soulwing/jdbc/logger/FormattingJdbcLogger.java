/*
 * File created on Aug 19, 2015
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
import org.soulwing.jdbc.source.SQLFormatter;
import org.soulwing.jdbc.source.SimpleSQLFormatter;

/**
 * A {@link JdbcLogger} that formats SQL statement text before delegating to
 * another logger.
 *
 * @author Carl Harris
 */
public class FormattingJdbcLogger implements JdbcLogger {

  private final JdbcLogger delegate;
  private final SQLFormatter formatter;

  /**
   * Constructs a new instance that uses {@link SimpleSQLFormatter}.
   * @param delegate logger delegate
   */
  public FormattingJdbcLogger(JdbcLogger delegate) {
    this(delegate, new SimpleSQLFormatter());
  }

  /**
   * Constructs a new instance.
   * @param delegate logger delegate
   * @param formatter formatter
   */
  public FormattingJdbcLogger(JdbcLogger delegate, SQLFormatter formatter) {
    this.delegate = delegate;
    this.formatter = formatter;
  }

  @Override
  public void writeStatement(String sql) {
    delegate.writeStatement(formatter.format(sql));
  }

  @Override
  public void writeParameters(Parameter[] parameters) {
    delegate.writeParameters(parameters);
  }

}
