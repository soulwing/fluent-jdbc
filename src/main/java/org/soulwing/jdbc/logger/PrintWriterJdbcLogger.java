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

import java.io.PrintStream;
import java.io.PrintWriter;

import org.soulwing.jdbc.Parameter;

/**
 * A {@link JdbcLogger} that delegates to a print stream or print writer.
 *
 * @author Carl Harris
 */
public class PrintWriterJdbcLogger implements JdbcLogger {

  private final PrintWriter delegate;
  private final boolean traceEnabled;

  /**
   * Constructs a new instance.
   * @param delegate delegate print writer
   */
  public PrintWriterJdbcLogger(PrintWriter delegate) {
    this(delegate, false);
  }

  /**
   * Constructs a new instance.
   * @param delegate delegate print stream
   */
  public PrintWriterJdbcLogger(PrintStream delegate) {
    this(delegate, false);
  }

  /**
   * Constructs a new instance.
   * @param delegate delegate print writer
   * @param traceEnabled flag indicating whether trace level logging should
   *    be used
   */
  public PrintWriterJdbcLogger(PrintWriter delegate, boolean traceEnabled) {
    this.delegate = delegate;
    this.traceEnabled = traceEnabled;
  }

  /**
   * Constructs a new instance.
   * @param delegate delegate print stream
   * @param traceEnabled flag indicating whether trace level logging should
   *    be used
   */
  public PrintWriterJdbcLogger(PrintStream delegate, boolean traceEnabled) {
    this(new PrintWriter(delegate), traceEnabled);
  }

  @Override
  public void writeStatement(String sql) {
    delegate.println(sql);
    delegate.flush();
  }

  @Override
  public void writeParameters(Parameter[] parameters) {
    if (!traceEnabled) return;
    for (int index = 0, max = parameters.length; index < max; index++) {
      delegate.println(parameters[index].toString(index));
      delegate.flush();
    }
  }

}
