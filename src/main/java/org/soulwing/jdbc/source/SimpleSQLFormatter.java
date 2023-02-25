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
package org.soulwing.jdbc.source;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.soulwing.jdbc.SQLRuntimeException;

/**
 * A simple {@link SQLFormatter} that removes comments and cleans up whitespace
 * to make an SQL statement occupy a single line in a log file.
 *
 * @author Carl Harris
 */
public class SimpleSQLFormatter implements SQLFormatter {

  @Override
  public String format(String sql) {
    try {
      final SimpleErrorReporter errorReporter = new SimpleErrorReporter();
      final List<Token> tokens = DefaultScanner.INSTANCE.scanTokens(
          new DelegatingSourceReader(new StringReader(sql.trim())), errorReporter);
      if (errorReporter.hasError()) {
        // we don't recognize the lexical structure, so don't bother formatting
        return sql;
      }
      final StringBuilder statement = new StringBuilder();
      for (final Token token : tokens) {
        if (token.getType() == Token.Type.WHITESPACE) {
          statement.append(" ");
        }
        else if (token.getType() != Token.Type.COMMENT) {
          statement.append(token.getLexeme());
        }
      }
      return statement.toString().trim();
    }
    catch (IOException ex) {
      throw new SQLRuntimeException(ex.getMessage(), ex);
    }
  }

  private static class SimpleErrorReporter implements ErrorReporter {

    private boolean hasError;

    @Override
    public boolean hasError() {
      return hasError;
    }

    @Override
    public void error(int offset, int length, String message) {
      hasError = true;
    }

  }


}
