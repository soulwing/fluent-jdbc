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
package org.soulwing.jdbc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.soulwing.jdbc.source.StringSQLSource;

/**
 * Tests for {@link FluentJdbc}.
 *
 * @author Carl Harris
 */
public class FluentJdbcTest {

  private final TestDatabase db = new TestDatabase();

  private FluentJdbc jdbc;

  private DataSourceWrapper dataSource;

  @Before
  public void setUp() throws Exception {
    dataSource = new DataSourceWrapper(db.getDataSource());
    jdbc = new FluentJdbc(dataSource);
  }

  @After
  public void tearDown() throws Exception {
    assertThat(dataSource.hasOpenConnections(), is(false));
    db.close();
  }

  @Test
  public void testExecuteScript() throws Exception {
    jdbc.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( text VARCHAR(255) );" +
            "DROP TABLE foo"));
  }

  @Test
  public void testQueryForObjectByColumnIndex() throws Exception {
    jdbc.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( text1 VARCHAR(255), text2 VARCHAR(255) );" +
            "INSERT INTO foo(text1, text2) VALUES('bar', 'baz')"));

    String result = jdbc.queryForType(String.class)
        .using("SELECT text1, text2 FROM foo")
        .extractingColumn(2)
        .retrieveValue();

    assertThat(result, is(equalTo("baz")));
  }

  @Test
  public void testQueryForObjectByColumnLabel() throws Exception {
    jdbc.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( text1 VARCHAR(255), text2 VARCHAR(255) );" +
            "INSERT INTO foo(text1, text2) VALUES('bar', 'baz')"));

    String result = jdbc.queryForType(String.class)
        .using("SELECT text1, text2 FROM foo")
        .extractingColumn("text2")
        .retrieveValue();

    assertThat(result, is(equalTo("baz")));
  }

  @Test
  public void testQueryForString() throws Exception {
    jdbc.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( text VARCHAR(255) );" +
            "INSERT INTO foo(text) VALUES('bar')"));

    String result = jdbc.queryForType(String.class)
        .using("SELECT * FROM foo")
        .extractingColumn()
        .retrieveValue();

    assertThat(result, is(equalTo("bar")));
  }

  @Test
  public void testQueryForInt() throws Exception {
    jdbc.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( i INTEGER );" +
            "INSERT INTO foo(i) VALUES(1)"));

    int result = jdbc.queryForType(int.class)
        .using("SELECT * FROM foo")
        .extractingColumn()
        .retrieveValue();

    assertThat(result, is(equalTo(1)));
  }

  @Test
  public void testQueryForLong() throws Exception {
    jdbc.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( i BIGINT );" +
            "INSERT INTO foo(i) VALUES(" + Long.MAX_VALUE + ")"));

    long result = jdbc.queryForType(long.class)
        .using("SELECT * FROM foo")
        .extractingColumn()
        .retrieveValue();

    assertThat(result, is(equalTo(Long.MAX_VALUE)));
  }

  @Test
  public void testQueryForDouble() throws Exception {
    jdbc.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( i DECIMAL(5,2) );" +
        "INSERT INTO foo(i) VALUES(3.14)"));

    double result = jdbc.queryForType(double.class)
        .using("SELECT * FROM foo")
        .extractingColumn()
        .retrieveValue();

    assertThat(result, is(equalTo(3.14)));
  }

  @Test
  public void testQueryForBigDecimal() throws Exception {
    jdbc.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( i DECIMAL(5,2) );" +
        "INSERT INTO foo(i) VALUES(3.14)"));

    BigDecimal result = jdbc.queryForType(BigDecimal.class)
        .using("SELECT * FROM foo")
        .extractingColumn()
        .retrieveValue();

    assertThat(result, is(equalTo(BigDecimal.valueOf(3.14))));
  }

  @Test
  @Ignore
  // This test fails because HSQLDB doesn't consider java.util.Date to be
  // a supported type to use with ResultSet.getObject.
  public void testQueryForJavaUtilDate() throws Exception {
    jdbc.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( created TIMESTAMP );" +
            "INSERT INTO foo(created) VALUES(current_timestamp)"));

    java.util.Date result = jdbc.queryForType(java.util.Date.class)
        .using("SELECT * FROM foo")
        .extractingColumn()
        .retrieveValue();

    assertThat(result, is(instanceOf(java.util.Date.class)));
  }

  @Test
  public void testQueryForTimestamp() throws Exception {
    jdbc.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( created TIMESTAMP );" +
        "INSERT INTO foo(created) VALUES(current_timestamp)"));

    Timestamp result = jdbc.queryForType(Timestamp.class)
        .using("SELECT * FROM foo")
        .extractingColumn()
        .retrieveValue();

    assertThat(result, is(instanceOf(Timestamp.class)));
  }

  @Test
  public void testQueryForDate() throws Exception {
    jdbc.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( created TIMESTAMP );" +
            "INSERT INTO foo(created) VALUES(current_timestamp)"));

    Date result = jdbc.queryForType(Date.class)
        .using("SELECT * FROM foo")
        .extractingColumn()
        .retrieveValue();

    assertThat(result, is(instanceOf(Date.class)));
  }

  @Test
  public void testQueryForTime() throws Exception {
    jdbc.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( created TIMESTAMP );" +
        "INSERT INTO foo(created) VALUES(current_timestamp)"));

    Time result = jdbc.queryForType(Time.class)
        .using("SELECT * FROM foo")
        .extractingColumn()
        .retrieveValue();

    assertThat(result, is(instanceOf(Time.class)));
  }

  @Test
  public void testQueryForObjectUsingRowMapper() throws Exception {
    jdbc.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( text VARCHAR(255) );" +
        "INSERT INTO foo(text) VALUES('foo')"));

    String result = jdbc.queryForType(String.class)
        .using("SELECT * FROM foo")
        .mappingRowsWith(new RowMapper<String>() {
          @Override
          public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString(1);
          }
        })
        .retrieveValue();

    assertThat(result, is(equalTo("foo")));
  }

  @Test
  public void testQueryForObjectWithParameters() throws Exception {
    jdbc.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( text1 VARCHAR(255), text2 VARCHAR(255) );" +
            "INSERT INTO foo(text1, text2) VALUES('bar', 'baz')"));

    String result = jdbc.queryForType(String.class)
        .using("SELECT text1, text2 FROM foo WHERE text1 = ? AND text2 = ?")
        .extractingColumn(2)
        .retrieveValue(Parameter.with("bar"), Parameter.with("baz"));

    assertThat(result, is(equalTo("baz")));
  }

  @Test
  public void testCallProcedureReturningParameters() throws Exception {
    jdbc.execute(
        "CREATE PROCEDURE foo(IN text VARCHAR(255), OUT t VARCHAR(255)) " +
          "BEGIN ATOMIC " +
          "SET t = text; " +
          "END");

    JdbcCall call = jdbc.call("{call foo(?, ?)}");
    try {
      call.execute(Parameter.in("bar"), Parameter.out(Types.VARCHAR));
      assertThat(call.getOutParameter(2, String.class), is(equalTo("bar")));

      call.execute(Parameter.in("baz"), Parameter.out(Types.VARCHAR));
      assertThat(call.getOutParameter(2, String.class), is(equalTo("baz")));
    }
    finally {
      call.close();
    }
  }

  @Test
  @Ignore   // this test fails because of an implementation issue in HSQLDB
            // see http://sourceforge.net/p/hsqldb/feature-requests/280
  public void testCallFunction() throws Exception {
    jdbc.execute(
        "CREATE FUNCTION foo(IN name VARCHAR(255)) " +
            "RETURNS VARCHAR(255) " +
            "BEGIN ATOMIC " +
            "RETURN 'Hello, ' || name; " +
            "END");

    JdbcCall call = jdbc.call("{? = call foo(?)}");
    try {
      call.execute(Parameter.out(Types.VARCHAR), Parameter.in("bar"));
      assertThat(call.getOutParameter(1, String.class), is(equalTo("Hello, bar")));

      call.execute(Parameter.out(Types.VARCHAR), Parameter.in("baz"));
      assertThat(call.getOutParameter(1, String.class), is(equalTo("Hello, baz")));
    }
    finally {
      call.close();
    }
  }

  @Test
  public void testCallProcedureAndRetrieveList() throws Exception {
    jdbc.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( text VARCHAR(255) ); " +
            "INSERT INTO foo(text) VALUES('bar'); "));

    jdbc.execute(
        "CREATE PROCEDURE foo_proc() " +
            "READS SQL DATA " +
            "DYNAMIC RESULT SETS 1 " +
            "BEGIN ATOMIC " +
            "DECLARE result CURSOR WITH RETURN FOR " +
              "SELECT * FROM foo FOR READ ONLY; " +
            "OPEN result; " +
            "END");

    final RowMapper<String> rowMapper = new RowMapper<String>() {
      @Override
      public String mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getString(1);
      }
    };

    JdbcCall call = jdbc.call("call foo_proc()");
    try {
      call.execute();
      assertThat(call.getMoreResults(), is(true));
      List<String> results = call.retrieveList(rowMapper);
      assertThat(results.size(), is(equalTo(1)));
      assertThat(results.get(0), is(equalTo("bar")));
      assertThat(call.getMoreResults(), is(false));
      assertThat(call.getUpdateCount(), is(equalTo(-1)));
    }
    finally {
      call.close();
    }

  }

  @Test
  public void testCallProcedureAndRetrieveValue() throws Exception {
    jdbc.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( text VARCHAR(255) ); " +
            "INSERT INTO foo(text) VALUES('bar'); "));

    jdbc.execute(
        "CREATE PROCEDURE foo_proc() " +
            "READS SQL DATA " +
            "DYNAMIC RESULT SETS 1 " +
            "BEGIN ATOMIC " +
            "DECLARE result CURSOR WITH RETURN FOR " +
            "SELECT * FROM foo FOR READ ONLY; " +
            "OPEN result; " +
            "END");

    JdbcCall call = jdbc.call("call foo_proc()");
    try {
      call.execute();
      assertThat(call.getMoreResults(), is(true));
      String value = call.retrieveValue(1, String.class);
      assertThat(value, is(equalTo("bar")));
      assertThat(call.getMoreResults(), is(false));
      assertThat(call.getUpdateCount(), is(equalTo(-1)));
    }
    finally {
      call.close();
    }

  }

  @Test
  public void testUpdateAndQueryUsingPreparedStatementCreator()
      throws Exception {
    final String[] words = { "bar", "baz", "bif"};

    jdbc.execute("CREATE TABLE foo ( text VARCHAR(255) )");

    try (JdbcUpdate updater = jdbc.update()
            .using("INSERT INTO foo(text) VALUES(?)")
            .repeatedly()) {
      for (String word : words) {
        updater.execute(Parameter.with(word));
      }
    }

    final List<String> results = jdbc.queryForType(String.class)
        .using("SELECT * FROM foo ORDER BY text")
        .extractingColumn()
        .retrieveList();

    assertThat(results, is(equalTo(Arrays.asList(words))));

    try (JdbcQuery<String> query = jdbc.queryForType(String.class)
            .using("SELECT text FROM foo WHERE text = ?")
            .extractingColumn()
            .repeatedly()) {
      for (String word : words) {
        String result = query.retrieveValue(Parameter.with(word));
        assertThat(result, is(equalTo(word)));
      }
    }

  }

  @Test
  public void testQueryUsingResultSetHandler() throws Exception {
    final String[] words = { "bar", "baz", "bif"};

    jdbc.execute("CREATE TABLE foo ( text VARCHAR(255) )");

    try (JdbcUpdate updater = jdbc.update()
        .using("INSERT INTO foo(text) VALUES(?)")
        .repeatedly()) {
      for (String word : words) {
        updater.execute(Parameter.with(word));
      }
    }

    final List<String> resultWords = new ArrayList<>();

    final ResultSetHandler<Void> handler = new ResultSetHandler<Void>() {
      @Override
      public Void handleResult(ResultSet rs) throws SQLException {
        while (rs.next()) {
          resultWords.add(rs.getString("text"));
        }
        return null;
      }
    };

    jdbc.query()
        .using("SELECT * FROM foo ORDER BY text")
        .handlingResultWith(handler)
        .retrieveValue();

    assertThat(resultWords, is(equalTo(Arrays.asList(words))));
  }

}
