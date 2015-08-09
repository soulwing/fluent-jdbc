/*
 * File created on May 5, 2014 
 *
 * Copyright (c) 2014 Carl Harris, Jr
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
 *
 */
package org.soulwing.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An closure that maps a row in a {@link ResultSet} to an instance of a given
 * type.
 *
 * @param <T> the type of object produced by this mapper
 * @author Carl Harris
 */
public interface RowMapper<T> {

  /**
   * Maps a single row in a {@link ResultSet} to a new instance of the
   * receiver's declared data type.
   * @param rs result set positioned to the row that is to be mapped
   * @param rowNum current row number (the first row is row 1)
   * @return instance of type {@code T}
   * @throws SQLException
   */
  T mapRow(ResultSet rs, int rowNum) throws SQLException;
  
}
