/*
 * File created on Aug 6, 2015
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

import java.util.ArrayList;
import java.util.List;

import org.soulwing.jdbc.SQLRuntimeException;

/**
 * An exception thrown by an {@link SQLSource} when an error occurs in reading
 * an {@link SQLSource}.
 *
 * @author Carl Harris
 */
public class SQLInputException extends SQLRuntimeException {

  public static class Error {
    public final int offset;
    public final int length;
    public final String message;

    public Error(int offset, int length, String message) {
      this.offset = offset;
      this.length = length;
      this.message = message;
    }
  }

  private final List<Error> errors = new ArrayList<>();

  public SQLInputException() {
    super("SQL input error(s)");
  }

  public SQLInputException(int offset, int length, String message) {
    super("SQL input error");
    addError(offset, length, message);
  }

  public SQLInputException(String message, Throwable cause) {
    super(message, cause);
  }

  void addError(int offset, int length, String message) {
    errors.add(new Error(offset, length, message));
  }

  public List<Error> getErrors() {
    return new ArrayList<>(errors);
  }

  @Override
  public String getMessage() {
    if (errors.isEmpty()) return super.getMessage();
    return errorList(super.getMessage());
  }

  @Override
  public String getLocalizedMessage() {
    if (errors.isEmpty()) return super.getLocalizedMessage();
    return errorList(super.getLocalizedMessage());
  }

  private String errorList(String summary) {
    final StringBuilder sb = new StringBuilder();
    sb.append(summary);
    sb.append(": ");
    for (int i = 0; i < errors.size(); i++) {
      final Error error = errors.get(i);
      sb.append(String.format("(offset=%d, length=%d) %s",
          error.offset, error.length, error.message));
      if (i + 1 < errors.size()) {
        sb.append(", ");
      }
    }
    return sb.toString();
  }

}
