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

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * A handler that is responsible for injecting a parameter value into a
 * {@link PreparedStatement}.
 *
 * @author Carl Harris
 */
public interface ParameterHandler {

  void handleParameter(PreparedStatement statement, int parameterIndex)
      throws SQLException;

}
