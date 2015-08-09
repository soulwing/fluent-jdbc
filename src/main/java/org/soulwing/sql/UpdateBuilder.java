/*
 * File created on Aug 9, 2015
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

import java.sql.SQLException;
import java.util.Arrays;

import javax.sql.DataSource;

import org.soulwing.sql.source.SQLSource;

/**
 * A concrete {@link SQLUpdate} implementation.
 *
 * @author Carl Harris
 */
class UpdateBuilder implements SQLUpdate {

  private final DataSource dataSource;

  private PreparedStatementCreator psc;
  private boolean repeatable;
  private boolean executed;

  /**
   * Constructs a new instance.
   * @param dataSource data source from which a connection will be obtained
   *
   */
  public UpdateBuilder(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public SQLUpdate using(String sql) {
    assertNotExecuted();
    this.psc = StatementPreparer.with(sql);
    return this;
  }

  @Override
  public SQLUpdate using(SQLSource source) {
    return using(SourceUtils.getSingleStatement(source));
  }

  @Override
  public SQLUpdate repeatedly() {
    assertNotExecuted();
    this.repeatable = true;
    return this;
  }

  @Override
  public int execute(Parameter... parameters) {
    assertReady();
    final PreparedUpdateExecutor executor = new PreparedUpdateExecutor(
        psc, Arrays.asList(parameters));

    try {
      return executor.execute(dataSource);
    }
    catch (SQLException ex) {
      throw new SQLRuntimeException(ex);
    }
    finally {
      executed = true;
      if (!repeatable) {
        close();
      }
    }
  }

  private void assertReady() {
    if (executed && !repeatable) {
      throw new IllegalStateException(
          "query has been executed and was not configured as repeatable");
    }
    if (psc == null) {
      throw new IllegalArgumentException(
          "no SQL statement or source has been configured");
    }
  }

  private void assertNotExecuted() {
    if (executed) {
      throw new IllegalStateException(
          "update cannot be reconfigured after is has been executed");
    }
  }

  @Override
  public void close() {
    SQLUtils.closeQuietly(psc);
  }

}
