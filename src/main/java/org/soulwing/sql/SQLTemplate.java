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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import javax.sql.DataSource;

import org.soulwing.sql.source.SQLSource;

/**
 * A thread-safe {@link SQLOperations} implementation.
 * <p>
 * This class must be constructed with a {@link DataSource} that will be used
 * as necessary to obtain connections to the database.
 * <pre>
 * import javax.sql.DataSource;
 * import org.soulwing.sql.SQLTemplate;
 *
 * DataSource dataSource = ...  // typically injected or retrieved via JNDI
 * SQLTemplate sqlTemplate = new SQLTemplate(dataSource);
 * </pre>
 *
 * A single instance of this class that is constructed with an appropriate
 * {@link DataSource} can be concurrently shared by an arbitrary number of
 * application components.
 *
 * @author Carl Harris
 */
public class SQLTemplate implements SQLOperations {

  private final DataSource dataSource;

  /**
   * Constructs a new template.
   * @param dataSource data source that will be used to obtain connections to
   *    the database
   */
  public SQLTemplate(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  /**
   * Constructs a new template for use by a single thread.
   * <p>
   * Use this constructor to create a template instance that uses the given
   * connection whenever a connection to the database is needed.  This allows
   * the template to be used in conjunction with a framework such as
   * <a href="http://flywaydb.org">Flyway</a> that wants to explicit manage
   * transaction state by providing a specific connection to each operation
   * that needs one.
   * <p>
   * Because this template uses a single database connection, it should
   * <em>not</em> be used by multiple concurrent threads.
   *
   * @param connection connection that will be used for all JDBC operations
   *   invoked using the template instance
   */
  public SQLTemplate(Connection connection) {
    this(new SingleConnectionDataSource(connection));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(String sql) {
    final PreparedStatementCreator psc = StatementPreparer.with(sql);
    final StatementExecutor executor = new StatementExecutor(psc);
    try {
      executor.execute(dataSource);
    }
    catch (SQLException ex) {
      throw new SQLRuntimeException(ex);
    }
    finally {
      SQLUtils.closeQuietly(psc);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(SQLSource source) {
    execute(SourceUtils.getSingleStatement(source));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void executeScript(SQLSource source) {
    try {
      String sql = source.next();
      while (sql != null) {
        execute(sql);
        sql = source.next();
      }
    }
    finally {
      SQLUtils.closeQuietly(source);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SQLQuery<Void> query() {
    return queryForType(Void.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> SQLQuery<T> queryForType(Class<T> type) {
    return new QueryBuilder<>(type, dataSource);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SQLUpdate update() {
    return new UpdateBuilder(dataSource);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SQLCall call(String sql) {
    return new CallBuilder(dataSource, CallPreparer.with(sql));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SQLCall call(SQLSource source) {
    return new CallBuilder(dataSource, CallPreparer.with(source));
  }

}
