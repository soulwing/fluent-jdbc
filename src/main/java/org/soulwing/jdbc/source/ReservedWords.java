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

import java.util.HashMap;
import java.util.Map;

/**
 * A partial set of SQL reserved words.
 * <p>
 * Only those words that are needed for the partial parser in
 * {@link ReaderSQLSource} are defined here.
 *
 * @author Carl Harris
 */
class ReservedWords {

  private static final String BEGIN = "BEGIN";
  private static final String END = "END";
  private static final String IF = "IF";
  private static final String FOR = "FOR";
  private static final String WHILE = "WHILE";
  private static final String LOOP = "LOOP";
  private static final String CASE = "CASE";

  private static final Map<String, Token.Type> tokenMap = new HashMap<>();

  static {
    tokenMap.put(BEGIN, Token.Type.BEGIN);
    tokenMap.put(END, Token.Type.END);
    tokenMap.put(IF, Token.Type.IF);
    tokenMap.put(FOR, Token.Type.FOR);
    tokenMap.put(WHILE, Token.Type.WHILE);
    tokenMap.put(LOOP, Token.Type.LOOP);
    tokenMap.put(CASE, Token.Type.CASE);
  }

  private ReservedWords() {}

  public static Token.Type toToken(String word) {
    return tokenMap.get(word.toUpperCase());
  }

}
