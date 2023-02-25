/*
 * File created on Feb 24, 2023
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * A {@link SourceReader} that delegates to a {@link Reader}.
 *
 * @author Carl Harris
 */
class DelegatingSourceReader implements SourceReader {

  private static final int REPLACEMENT_CHARACTER = '\uFFFD';

  private static final int ZERO_WIDTH_NO_BREAK_SPACE = '\uFEFF';

  private final Reader delegate;
  private boolean stripped;
  private boolean eof;
  private int start;
  private int current;
  private StringBuilder lexeme;

  public DelegatingSourceReader(Reader delegate) {
    if (!delegate.markSupported()) {
      delegate = new BufferedReader(delegate);
    }
    this.delegate = delegate;
  }

  @Override
  public int getStart() {
    return start;
  }

  @Override
  public int getCurrent() {
    return current;
  }

  @Override
  public String getLexeme() {
    return lexeme.toString();
  }

  @Override
  public String getLexeme(int start, int end) {
    return lexeme.substring(start, end);
  }

  @Override
  public void markStart() {
    start = current;
    lexeme = new StringBuilder();
  }

  @Override
  public boolean eof() {
    return eof;
  }

  @Override
  public boolean match(char expected) throws IOException {
    final char c = peek();
    if (c != expected) return false;
    advance();
    return true;
  }

  @Override
  public char advance() throws IOException {
    if (eof) return EOF;
    int c = read();
    current++;
    if (c != -1) {
      lexeme.append((char) c);
    }
    else {
      c = EOF;
    }
    return (char) c;
  }

  @Override
  public char peek() throws IOException {
    if (eof) return EOF;
    mark(1);
    try {
      int c = read();
      if (c == -1) {
        c = EOF;
      }
      return (char) c;
    }
    finally {
      reset();
    }
  }

  @Override
  public int read() throws IOException {
    if (!stripped) {
      stripByteOrderMark();
    }
    int c = delegate.read();
    if (c == -1) {
      eof = true;
    }
    return c;
  }

  @Override
  public void reset() throws IOException {
    delegate.reset();
  }

  @Override
  public void mark(int readAheadLimit) throws IOException {
    delegate.mark(readAheadLimit);
  }

  @Override
  public void close() throws IOException {
    delegate.close();
  }

  private void stripByteOrderMark() throws IOException {
    int c = 0;
    do {
      delegate.mark(1);
      c = delegate.read();
    } while (c == REPLACEMENT_CHARACTER || c == ZERO_WIDTH_NO_BREAK_SPACE);
    delegate.reset();
    stripped = true;
  }

}
