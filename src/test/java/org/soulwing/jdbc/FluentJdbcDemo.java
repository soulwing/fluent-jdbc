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
package org.soulwing.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.soulwing.jdbc.logger.FormattingJdbcLogger;
import org.soulwing.jdbc.logger.PrintWriterJdbcLogger;
import org.soulwing.jdbc.source.StringSQLSource;

/**
 * A simple demo of some of the features of the library.
 *
 * @author Carl Harris
 */
public class FluentJdbcDemo {

  public static void main(String[] args) {
    TestDatabase db = new TestDatabase();
    FluentJdbc jdbc = new FluentJdbc(db.getDataSource());
    jdbc.setLogger(new FormattingJdbcLogger(new PrintWriterJdbcLogger(System.out)));

    jdbc.execute("CREATE TABLE person ( " +
        "id BIGINT PRIMARY KEY, name VARCHAR(50), age INTEGER )");

    jdbc.executeScript(new StringSQLSource(
        "INSERT INTO PERSON(id, name, age) VALUES(1, 'Jennifer Wilson', 29);" +
            "INSERT INTO PERSON(id, name, age) VALUES(2, 'Nadine Bennett', 31);" +
            "INSERT INTO PERSON(id, name, age) VALUES(3, 'Megan Marshall', 27);"
    ));

    List<Map> people = jdbc.queryForType(Map.class)
        .using("SELECT * FROM person")
        .mappingRowsWith(new RowMapper<Map>() {
          public Map mapRow(ResultSet rs, int rowNum)
              throws SQLException {
            Map<String, Object> person = new HashMap<>();
            person.put("id", rs.getLong("id"));
            person.put("name", rs.getString("name"));
            person.put("age", rs.getInt("age"));
            return person;
          }
        })
        .retrieveList();

    System.out.format("people: %s\n", people);

    try (JdbcUpdate updater = jdbc.update()
        .using("UPDATE person SET age = age + 1 WHERE id = ?")
        .repeatedly()) {
      updater.execute(Parameter.with(2));
      updater.execute(Parameter.with(3));
    }

    int averageAge = jdbc.queryForType(int.class)
        .using("SELECT AVG(age) FROM person")
        .extractingColumn()
        .retrieveValue();

    System.out.format("average age: %d\n", averageAge);

    int count = jdbc.update()
        .using("DELETE FROM person")
        .execute();

    System.out.format("deleted %d people\n", count);

    db.close();
  }
}
