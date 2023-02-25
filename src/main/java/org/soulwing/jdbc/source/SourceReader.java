/*
 * File created on Feb 25, 2023
 *
 * Copyright (c) 2023 Carl Harris, Jr
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
import java.io.IOException;

/**
 * A specialized reader used by a {@link Scanner} to read an SQL source.
 *
 * @author Carl Harris
 */
interface SourceReader extends Closeable, AutoCloseable {

  // Use ASCII DEL (0x7f) as the EOF character, so that NUL can be ignored
  // as whitespace.
  char EOF = (char) 0x7f;

  int getStart();

  int getCurrent();

  String getLexeme();

  String getLexeme(int start, int end);

  void markStart();

  boolean eof();

  boolean match(char expected) throws IOException;

  char advance() throws IOException;

  char peek() throws IOException;

  int read() throws IOException;

  void reset() throws IOException;

  void mark(int readAheadLimit) throws IOException;

  @Override
  void close() throws IOException;
}
