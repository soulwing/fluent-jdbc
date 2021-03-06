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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * An {@link SQLSource} that reads statements from a {@link Reader}.
 *
 * @author Carl Harris
 */
public class ReaderSQLSource implements SQLSource {

  private final Parser parser;

  public ReaderSQLSource(Reader reader) {
    if (!(reader instanceof BufferedReader)) {
      reader = new BufferedReader(reader);
    }
    this.parser = new Parser(reader);
  }

  @Override
  public String next() throws SQLInputException {
    try {
      return parser.next();
    }
    catch (IOException ex) {
      throw new SQLInputException(ex.getMessage(), ex);
    }
  }

  @Override
  public void close() throws IOException {
    parser.close();
  }

}
