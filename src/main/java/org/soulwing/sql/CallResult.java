/*
 * File created on Aug 8, 2015
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
package org.soulwing.sql;

import java.sql.ResultSet;
import java.util.List;

/**
 * A result from a stored procedure call.
 *
 * @author Carl Harris
 */
public interface CallResult extends AutoCloseable {

  /**
   * Gets the number of rows inserted/updated.
   * @return update count
   */
  int getUpdateCount();

  /**
   * Moves to the next result set.
   * @return {@code true} if another result set is available.
   */
  boolean getMoreResults();

  /**
   * Gets the current result set.
   * @return result set or {@code null} if all results have been visited
   */
  ResultSet getResultSet();

  /**
   * Extracts the current result set using the given extractor.
   * @param extractor the subject extractor
   * @return object produced by {@code extractor}
   */
  <T> T extractResultSet(ResultSetExtractor<T> extractor);

  /**
   * Maps the current result set using the given row mapper.
   * @param rowMapper the subject row mapper
   * @return list of objects produced by {@link RowMapper}
   */
  <T> List<T> mapResultSet(RowMapper<T> rowMapper);

  /**
   * Gets a column value from a single row result set.
   * @param columnExtractor extractor that will be used to extract the
   *    column value
   * @param <T>
   * @return column value
   */
  <T> T get(ColumnExtractor<T> columnExtractor);

  /**
   * Maps an object from a single row result set.
   * @param rowMapper row mapper that will be used to map the object
   *    from the row
   * @return mapped row object
   */
  <T> T get(RowMapper<T> rowMapper);

  List<Parameter> getOutParameters();

  void close();

}
