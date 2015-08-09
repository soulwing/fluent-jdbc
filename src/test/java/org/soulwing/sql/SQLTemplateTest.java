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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.soulwing.sql.source.StringSQLSource;

/**
 * Tests for {@link SQLTemplate}.
 *
 * @author Carl Harris
 */
public class SQLTemplateTest {

  private final TestDatabase db = new TestDatabase();

  private SQLTemplate template;

  private DataSourceWrapper dataSource;

  @Before
  public void setUp() throws Exception {
    dataSource = new DataSourceWrapper(db.getDataSource());
    template = new SQLTemplate(dataSource);
  }

  @After
  public void tearDown() throws Exception {
    assertThat(dataSource.hasOpenConnections(), is(false));
    db.close();
  }

  @Test
  public void testExecuteScript() throws Exception {
    template.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( text VARCHAR(255) );" +
            "DROP TABLE foo"));
  }

  @Test
  public void testQueryForObjectByColumnIndex() throws Exception {
    template.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( text1 VARCHAR(255), text2 VARCHAR(255) );" +
            "INSERT INTO foo(text1, text2) VALUES('bar', 'baz')"));

    String result = template.queryForType(String.class)
        .using("SELECT text1, text2 FROM foo")
        .extractingColumn(2)
        .retrieveValue();

    assertThat(result, is(equalTo("baz")));
  }

  @Test
  public void testQueryForObjectByColumnLabel() throws Exception {
    template.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( text1 VARCHAR(255), text2 VARCHAR(255) );" +
            "INSERT INTO foo(text1, text2) VALUES('bar', 'baz')"));

    String result = template.queryForType(String.class)
        .using("SELECT text1, text2 FROM foo")
        .extractingColumn("text2")
        .retrieveValue();

    assertThat(result, is(equalTo("baz")));
  }

  @Test
  public void testQueryForString() throws Exception {
    template.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( text VARCHAR(255) );" +
            "INSERT INTO foo(text) VALUES('bar')"));

    String result = template.queryForType(String.class)
        .using("SELECT * FROM foo")
        .extractingColumn()
        .retrieveValue();

    assertThat(result, is(equalTo("bar")));
  }

  @Test
  public void testQueryForInt() throws Exception {
    template.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( i INTEGER );" +
            "INSERT INTO foo(i) VALUES(1)"));

    int result = template.queryForType(int.class)
        .using("SELECT * FROM foo")
        .extractingColumn()
        .retrieveValue();

    assertThat(result, is(equalTo(1)));
  }

  @Test
  public void testQueryForLong() throws Exception {
    template.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( i BIGINT );" +
            "INSERT INTO foo(i) VALUES(" + Long.MAX_VALUE + ")"));

    long result = template.queryForType(long.class)
        .using("SELECT * FROM foo")
        .extractingColumn()
        .retrieveValue();

    assertThat(result, is(equalTo(Long.MAX_VALUE)));
  }

  @Test
  public void testQueryForDouble() throws Exception {
    template.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( i DECIMAL(5,2) );" +
        "INSERT INTO foo(i) VALUES(3.14)"));

    double result = template.queryForType(double.class)
        .using("SELECT * FROM foo")
        .extractingColumn()
        .retrieveValue();

    assertThat(result, is(equalTo(3.14)));
  }

  @Test
  public void testQueryForBigDecimal() throws Exception {
    template.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( i DECIMAL(5,2) );" +
        "INSERT INTO foo(i) VALUES(3.14)"));

    BigDecimal result = template.queryForType(BigDecimal.class)
        .using("SELECT * FROM foo")
        .extractingColumn()
        .retrieveValue();

    assertThat(result, is(equalTo(BigDecimal.valueOf(3.14))));
  }

  @Test
  public void testQueryForJavaUtilDate() throws Exception {
    template.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( created TIMESTAMP );" +
            "INSERT INTO foo(created) VALUES(current_timestamp)"));

    java.util.Date result = template.queryForType(java.util.Date.class)
        .using("SELECT * FROM foo")
        .extractingColumn()
        .retrieveValue();

    assertThat(result, is(instanceOf(java.util.Date.class)));
  }

  @Test
  public void testQueryForTimestamp() throws Exception {
    template.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( created TIMESTAMP );" +
        "INSERT INTO foo(created) VALUES(current_timestamp)"));

    Timestamp result = template.queryForType(Timestamp.class)
        .using("SELECT * FROM foo")
        .extractingColumn()
        .retrieveValue();

    assertThat(result, is(instanceOf(Timestamp.class)));
  }

  @Test
  public void testQueryForDate() throws Exception {
    template.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( created TIMESTAMP );" +
            "INSERT INTO foo(created) VALUES(current_timestamp)"));

    Date result = template.queryForType(Date.class)
        .using("SELECT * FROM foo")
        .extractingColumn()
        .retrieveValue();

    assertThat(result, is(instanceOf(Date.class)));
  }

  @Test
  public void testQueryForTime() throws Exception {
    template.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( created TIMESTAMP );" +
        "INSERT INTO foo(created) VALUES(current_timestamp)"));

    Time result = template.queryForType(Time.class)
        .using("SELECT * FROM foo")
        .extractingColumn()
        .retrieveValue();

    assertThat(result, is(instanceOf(Time.class)));
  }

  @Test
  public void testQueryForObjectUsingRowMapper() throws Exception {
    template.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( text VARCHAR(255) );" +
        "INSERT INTO foo(text) VALUES('foo')"));

    String result = template.queryForType(String.class)
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
    template.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( text1 VARCHAR(255), text2 VARCHAR(255) );" +
            "INSERT INTO foo(text1, text2) VALUES('bar', 'baz')"));

    String result = template.queryForType(String.class)
        .using("SELECT text1, text2 FROM foo WHERE text1 = ? AND text2 = ?")
        .extractingColumn(2)
        .retrieveValue(Parameter.with("bar"), Parameter.with("baz"));

    assertThat(result, is(equalTo("baz")));
  }

  @Test
  public void testCallProcedureReturningParameters() throws Exception {
    template.execute(
        "CREATE PROCEDURE foo(IN text VARCHAR(255), OUT t VARCHAR(255)) " +
          "BEGIN ATOMIC " +
          "SET t = text; " +
          "END");

    CallResult result = template.call("{call foo(?, ?)}",
       Parameter.in("bar"),  Parameter.out(Types.VARCHAR));
    try {
      List<Parameter> results = result.getOutParameters();
      assertThat(results.size(), is(equalTo(1)));
      assertThat((String) results.get(0).getValue(), is(equalTo("bar")));
    }
    finally {
      result.close();
    }
  }

  @Test
  public void testCallProcedureAndMapResultSet() throws Exception {
    template.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( text VARCHAR(255) ); " +
            "INSERT INTO foo(text) VALUES('bar'); "));

    template.execute(
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

    CallResult result = template.call("call foo_proc()");
    try {
      assertThat(result.getMoreResults(), is(true));
      List<String> results = result.mapResultSet(rowMapper);
      assertThat(results.size(), is(equalTo(1)));
      assertThat(results.get(0), is(equalTo("bar")));
      assertThat(result.getMoreResults(), is(false));
      assertThat(result.getUpdateCount(), is(equalTo(-1)));
    }
    finally {
      result.close();
    }

  }

  @Test
  public void testCallProcedureAndGetObject() throws Exception {
    template.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( text VARCHAR(255) ); " +
            "INSERT INTO foo(text) VALUES('bar'); "));

    template.execute(
        "CREATE PROCEDURE foo_proc() " +
            "READS SQL DATA " +
            "DYNAMIC RESULT SETS 1 " +
            "BEGIN ATOMIC " +
            "DECLARE result CURSOR WITH RETURN FOR " +
            "SELECT * FROM foo FOR READ ONLY; " +
            "OPEN result; " +
            "END");

    CallResult result = template.call("call foo_proc()");
    try {
      assertThat(result.getMoreResults(), is(true));
      String value = result.get(ColumnExtractor.with(String.class));
      assertThat(value, is(equalTo("bar")));
      assertThat(result.getMoreResults(), is(false));
      assertThat(result.getUpdateCount(), is(equalTo(-1)));
    }
    finally {
      result.close();
    }

  }

  @Test
  public void testUpdateAndQueryUsingPreparedStatementCreator()
      throws Exception {
    final String[] words = { "bar", "baz", "bif"};

    template.execute("CREATE TABLE foo ( text VARCHAR(255) )");

    try (SQLUpdate updater = template.update()
            .using("INSERT INTO foo(text) VALUES(?)")
            .repeatedly()) {
      for (String word : words) {
        updater.execute(Parameter.with(word));
      }
    }

    final List<String> results = template.queryForType(String.class)
        .using("SELECT * FROM foo ORDER BY text")
        .extractingColumn()
        .retrieveList();

    assertThat(results, is(equalTo(Arrays.asList(words))));

    try (SQLQuery<String> query = template.queryForType(String.class)
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

    template.execute("CREATE TABLE foo ( text VARCHAR(255) )");

    try (SQLUpdate updater = template.update()
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

    template.query()
        .using("SELECT * FROM foo ORDER BY text")
        .handlingResultWith(handler)
        .retrieveValue();

    assertThat(resultWords, is(equalTo(Arrays.asList(words))));
  }

}
