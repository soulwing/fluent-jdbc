/*
 * File created on Jan 17, 2017
 *
 * Copyright (c) 2017 Carl Harris, Jr
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
package org.soulwing.jdbc;

import java.sql.NClob;
import java.sql.SQLException;

/**
 * An API user callback that prepares a {@link NClob} before a JDBC insert or
 * update operation.
 *
 * @author Carl Harris
 */
public interface NClobHandler {

  /**
   * Prepares the given {@link NClob} before it is included an in insert or
   * update statement.
   * <p>
   * An implementation will typically provide the content for the large object.
   * @param nClob the large object to prepare
   * @throws SQLException as needed
   */
  void prepareNClob(NClob nClob) throws SQLException;

}
