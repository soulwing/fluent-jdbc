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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.soulwing.sql.source.StringSQLSource;

/**
 * A simple demo of some of the features of the library.
 *
 * @author Carl Harris
 */
public class SQLTemplateDemo {

  public static void main(String[] args) {
    TestDatabase db = new TestDatabase();
    SQLTemplate sqlTemplate = new SQLTemplate(db.getDataSource());

    sqlTemplate.execute("CREATE TABLE person ( " +
        "id BIGINT PRIMARY KEY, name VARCHAR(50), age INTEGER )");

    sqlTemplate.executeScript(new StringSQLSource(
        "INSERT INTO PERSON(id, name, age) VALUES(1, 'Jennifer Wilson', 29);" +
            "INSERT INTO PERSON(id, name, age) VALUES(2, 'Nadine Bennett', 31);" +
            "INSERT INTO PERSON(id, name, age) VALUES(3, 'Megan Marshall', 27);"
    ));

    List<Map<String, Object>> people = sqlTemplate.query("SELECT * FROM person",
        new Parameter[0],
        new RowMapper<Map<String, Object>>() {
          public Map<String, Object> mapRow(ResultSet rs, int rowNum)
              throws SQLException {
            Map<String, Object> person = new HashMap<>();
            person.put("id", rs.getLong("id"));
            person.put("name", rs.getString("name"));
            person.put("age", rs.getInt("age"));
            return person;
          }
        });

    System.out.format("people: %s\n", people);

    StatementPreparer preparer = StatementPreparer.with(
        "UPDATE person SET age = age + 1 WHERE id = ?");
    sqlTemplate.update(preparer, Parameter.with(2));
    sqlTemplate.update(preparer, Parameter.with(3));

    int averageAge = sqlTemplate.queryForObject(
        "SELECT AVG(age) FROM person", ColumnExtractor.with(int.class));

    System.out.format("average age: %d\n", averageAge);

    int count = sqlTemplate.update("DELETE FROM person");

    System.out.format("deleted %d people\n", count);

    db.close();
  }
}
