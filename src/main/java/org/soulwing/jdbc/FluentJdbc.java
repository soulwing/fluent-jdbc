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
package org.soulwing.jdbc;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.soulwing.jdbc.logger.JdbcLogger;
import org.soulwing.jdbc.logger.NullJdbcLogger;
import org.soulwing.jdbc.logger.PrintWriterJdbcLogger;
import org.soulwing.jdbc.source.SQLSource;

/**
 * A thread-safe {@link JdbcOperations} implementation.
 * <p>
 * This class must be constructed with a {@link DataSource} that will be used
 * as necessary to obtain connections to the database.
 * <pre>
 * import javax.sql.DataSource;
 * import org.soulwing.jdbc.FluentJdbc;
 *
 * DataSource dataSource = ...  // typically injected or retrieved via JNDI
 * FluentJdbc sqlTemplate = new FluentJdbc(dataSource);
 * </pre>
 *
 * A single instance of this class that is constructed with an appropriate
 * {@link DataSource} can be concurrently shared by an arbitrary number of
 * application components.
 *
 * @author Carl Harris
 */
public class FluentJdbc implements JdbcOperations {

  private final DataSource dataSource;
  private JdbcLogger logger = NullJdbcLogger.INSTANCE;
  private boolean autoCommit;
  private boolean ignoreErrors;

  /**
   * Constructs a new instance.
   * @param dataSource data source that will be used to obtain connections to
   *    the database
   */
  public FluentJdbc(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  /**
   * Constructs a new instance for use by a single thread.
   * <p>
   * Use this constructor to create a facade instance that uses the given
   * connection whenever a connection to the database is needed.  This allows
   * the facade to be used in conjunction with a framework such as
   * <a href="http://flywaydb.org">Flyway</a> that wants to explicit manage
   * transaction state by providing a specific connection to each operation
   * that needs one.
   * <p>
   * Because an instance created via this constructor uses a single database
   * connection, it <em>should not</em> be used by multiple concurrent threads.
   *
   * @param connection connection that will be used for all JDBC operations
   *   invoked using this facade instance
   */
  public FluentJdbc(Connection connection) {
    this(new SingleConnectionDataSource(connection));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(String sql) {
    doExecute(sql, dataSource);
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
    Connection connection = null;
    Boolean autoCommit = null;
    try {
      connection = dataSource.getConnection();
      if (this.autoCommit) {
        autoCommit = connection.getAutoCommit();
        connection.setAutoCommit(true);
      }
      final SingleConnectionDataSource dataSource =
          new SingleConnectionDataSource(connection);
      String sql = source.next();
      while (sql != null) {
        try {
          doExecute(sql, dataSource);
        }
        catch (SQLRuntimeException ex) {
          if (!ignoreErrors) {
            throw ex;
          }
        }
        sql = source.next();
      }
    }
    catch (SQLException ex) {
      throw new SQLRuntimeException(ex);
    }
    finally {
      JdbcUtils.closeQuietly(source);
      if (autoCommit != null) {
        try {
          connection.setAutoCommit(autoCommit);
        }
        catch (SQLException ex) {
          throw new SQLRuntimeException(ex);
        }
      }
      if (connection != null) {
        JdbcUtils.closeQuietly(connection);
      }
    }
  }

  private void doExecute(String sql, DataSource dataSource) {
    final PreparedStatementCreator psc = StatementPreparer.with(sql);
    final StatementExecutor executor = new StatementExecutor(psc, logger);
    try {
      executor.execute(dataSource);
    }
    catch (SQLException ex) {
      throw new SQLRuntimeException(ex);
    }
    finally {
      JdbcUtils.closeQuietly(psc);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JdbcQuery<Void> query() {
    return queryForType(Void.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> JdbcQuery<T> queryForType(Class<T> type) {
    return new QueryBuilder<>(type, dataSource, logger);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JdbcUpdate update() {
    return new UpdateBuilder(dataSource, logger);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JdbcCall call(String sql) {
    return new CallBuilder(dataSource, CallPreparer.with(sql), logger);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JdbcCall call(SQLSource source) {
    return new CallBuilder(dataSource, CallPreparer.with(source), logger);
  }

  /**
   * Sets the logger to use for SQL statement logging.
   * @param logger the logger to set (may be {@code null} to disable logging)
   */
  public void setLogger(JdbcLogger logger) {
    if (logger == null) {
      logger = NullJdbcLogger.INSTANCE;
    }
    this.logger = logger;
  }

  /**
   * Sets a print writer to use for SQL statement logging.
   * @param writer the logger to set (may be {@code null} to disable logging)
   */
  public void setLogger(PrintWriter writer) {
    setLogger(writer, false);
  }

  /**
   * Sets a print writer to use for SQL statement logging.
   * @param writer the logger to set (may be {@code null} to disable logging)
   * @param traceEnabled flag indicating whether trace level logging should
   *    be used
   */
  public void setLogger(PrintWriter writer, boolean traceEnabled) {
    setLogger(new PrintWriterJdbcLogger(writer, traceEnabled));
  }

  /**
   * Sets a print stream to use for SQL statement logging.
   * @param stream the logger to set (may be {@code null} to disable logging)
   */
  public void setLogger(PrintStream stream) {
    setLogger(stream, false);
  }

  /**
   * Sets a print stream to use for SQL statement logging.
   * @param stream the logger to set (may be {@code null} to disable logging)
   * @param traceEnabled flag indicating whether trace level logging should
   *    be used
   *
   */
  public void setLogger(PrintStream stream, boolean traceEnabled) {
    setLogger(new PrintWriterJdbcLogger(stream, traceEnabled));
  }

  /**
   * Gets the {@code autoCommit} flag state.
   * <p>
   * @return {@code true} if {@link #executeScript(SQLSource) executeScript}
   *   will use auto-commit mode
   */
  public boolean isAutoCommit() {
    return autoCommit;
  }

  /**
   * Sets the {@code autoCommit} flag.
   * <p>
   * This state of this flag is checked when {@link #executeScript(SQLSource)
   * executeScript} is invoked. If {@code true}, all statements executed by
   * the script will be auto-committed.
   * <p>
   * In a managed transaction environment (e.g. in a Java EE application)
   * setting this flag to {@code true} may cause script execution to fail.
   *
   * @param autoCommit the auto-commit flag state to set
   */
  public void setAutoCommit(boolean autoCommit) {
    this.autoCommit = autoCommit;
  }

  /**
   * Gets the {@code ignoreErrors} flag state.
   * @return {@code true} if {@link #executeScript(SQLSource) executeScript}
   *   will ignore errors at the statement level
   */
  public boolean isIgnoreErrors() {
    return ignoreErrors;
  }

  /**
   * Sets the {@code ignoreErrors} flag.
   * <p>
   * This state of this flag is checked when {@link #executeScript(SQLSource)
   * executeScript} is invoked. If {@code true}, errors thrown by the execution
   * of any given statement will be ignored.
   * <p>
   * In general, ignoring errors is effective only when the
   * {@linkplain #setAutoCommit(boolean) auto-commit} flag is also set to
   * {@code true}.
   *
   * @param ignoreErrors the flag state to set
   */
  public void setIgnoreErrors(boolean ignoreErrors) {
    this.ignoreErrors = ignoreErrors;
  }

}
