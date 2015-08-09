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
import java.util.Arrays;
import java.util.List;

import org.junit.After;
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

    assertThat(template.queryForObject("SELECT text1, text2 FROM foo",
        ColumnExtractor.with(2, String.class)), is(equalTo("baz")));
  }

  @Test
  public void testQueryForObjectByColumnLabel() throws Exception {
    template.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( text1 VARCHAR(255), text2 VARCHAR(255) );" +
        "INSERT INTO foo(text1, text2) VALUES('bar', 'baz')"));

    assertThat(template.queryForObject("SELECT text1, text2 FROM foo",
        ColumnExtractor.with("text2", String.class)), is(equalTo("baz")));
  }

  @Test
  public void testQueryForString() throws Exception {
    template.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( text VARCHAR(255) );" +
        "INSERT INTO foo(text) VALUES('bar')"));

    assertThat(template.queryForObject("SELECT * FROM foo",
        ColumnExtractor.with(String.class)), is(equalTo("bar")));
  }

  @Test
  public void testQueryForInt() throws Exception {
    template.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( i INTEGER );" +
        "INSERT INTO foo(i) VALUES(1)"));

    assertThat(template.queryForObject("SELECT * FROM foo",
        ColumnExtractor.with(int.class)), is(equalTo(1)));
  }

  @Test
  public void testQueryForLong() throws Exception {
    template.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( i BIGINT );" +
        "INSERT INTO foo(i) VALUES(" + Long.MAX_VALUE + ")"));

    assertThat(template.queryForObject("SELECT * FROM foo",
        ColumnExtractor.with(long.class)), is(equalTo(Long.MAX_VALUE)));
  }

  @Test
  public void testQueryForDouble() throws Exception {
    template.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( i DECIMAL(5,2) );" +
        "INSERT INTO foo(i) VALUES(3.14)"));

    assertThat(template.queryForObject("SELECT * FROM foo",
        ColumnExtractor.with(double.class)), is(equalTo(3.14)));
  }

  @Test
  public void testQueryForBigDecimal() throws Exception {
    template.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( i DECIMAL(5,2) );" +
        "INSERT INTO foo(i) VALUES(3.14)"));

    assertThat(template.queryForObject("SELECT * FROM foo",
            ColumnExtractor.with(BigDecimal.class)),
        is(equalTo(BigDecimal.valueOf(3.14))));
  }

  @Test
  public void testQueryForJavaUtilDate() throws Exception {
    template.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( created TIMESTAMP );" +
        "INSERT INTO foo(created) VALUES(current_timestamp)"));

    assertThat(template.queryForObject("SELECT * FROM foo",
            ColumnExtractor.with(java.util.Date.class)),
        is(instanceOf(java.util.Date.class)));
  }

  @Test
  public void testQueryForTimestamp() throws Exception {
    template.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( created TIMESTAMP );" +
        "INSERT INTO foo(created) VALUES(current_timestamp)"));

    assertThat(template.queryForObject("SELECT * FROM foo",
            ColumnExtractor.with(Timestamp.class)),
        is(instanceOf(Timestamp.class)));
  }

  @Test
  public void testQueryForDate() throws Exception {
    template.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( created TIMESTAMP );" +
        "INSERT INTO foo(created) VALUES(current_timestamp)"));

    assertThat(template.queryForObject("SELECT * FROM foo",
        ColumnExtractor.with(Date.class)), is(instanceOf(Date.class)));
  }

  @Test
  public void testQueryForTime() throws Exception {
    template.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( created TIMESTAMP );" +
        "INSERT INTO foo(created) VALUES(current_timestamp)"));

    assertThat(template.queryForObject("SELECT * FROM foo",
        ColumnExtractor.with(Time.class)), is(instanceOf(Time.class)));
  }

  @Test
  public void testQueryForObjectUsingRowMapper() throws Exception {
    template.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( text VARCHAR(255) );" +
        "INSERT INTO foo(text) VALUES('foo')"));

    final RowMapper<String> rowMapper = new RowMapper<String>() {
      @Override
      public String mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getString(1);
      }
    };

    assertThat(template.queryForObject("SELECT * FROM foo", new Parameter[]{},
        rowMapper), is(equalTo("foo")));
  }

  @Test
  public void testQueryForObjectWithParameters() throws Exception {
    template.executeScript(new StringSQLSource(
        "CREATE TABLE foo ( text1 VARCHAR(255), text2 VARCHAR(255) );" +
        "INSERT INTO foo(text1, text2) VALUES('bar', 'baz')"));

    assertThat(template.queryForObject(
        "SELECT text1, text2 FROM foo " +
        "WHERE text1 = ? AND text2 = ?",
            ColumnExtractor.with(2, String.class),
            Parameter.with("bar"), Parameter.with("baz")
        ),
        is(equalTo("baz")));
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
    try (StatementPreparer preparer = StatementPreparer.with(
        "INSERT INTO foo(text) VALUES(?)")) {
      for (String word : words) {
        template.update(preparer, Parameter.with(word));
      }
    }

    final List<String> results = template.query(
        "SELECT * FROM foo ORDER BY text", ColumnExtractor.with(String.class));

    assertThat(results, is(equalTo(Arrays.asList(words))));

    try (StatementPreparer preparer = StatementPreparer.with(
        "SELECT text FROM foo WHERE text = ?")) {
      for (String word : words) {
        String result = template.queryForObject(preparer,
            ColumnExtractor.with(String.class), Parameter.with(word));
        assertThat(result, is(equalTo(word)));
      }
    }
  }

}
