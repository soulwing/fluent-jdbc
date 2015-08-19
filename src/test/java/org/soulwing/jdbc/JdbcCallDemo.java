/*
 * File created on Aug 10, 2015
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
import java.sql.Types;
import java.util.List;

import org.soulwing.jdbc.logger.FormattingJdbcLogger;
import org.soulwing.jdbc.logger.PrintWriterJdbcLogger;

/**
 * A simple demo of some of the features of the library.
 *
 * @author Carl Harris
 */
public class JdbcCallDemo {

  public static void main(String[] args) {
    TestDatabase db = new TestDatabase();

    FluentJdbc jdbc = new FluentJdbc(db.getDataSource());
    jdbc.setLogger(new FormattingJdbcLogger(new PrintWriterJdbcLogger(System.out)));

    jdbc.execute(
        "CREATE TABLE person ( id IDENTITY, name VARCHAR(50), age INTEGER )");

    jdbc.execute(
        "CREATE PROCEDURE add_person " +
            "(IN p_name VARCHAR(50), IN p_age INTEGER, OUT p_id BIGINT) " +
            "MODIFIES SQL DATA " +
            "BEGIN ATOMIC " +
            "INSERT INTO person(name, age) VALUES(p_name, p_age); " +
            "SET p_id = IDENTITY(); " +
            "END");

    try (JdbcCall call = jdbc.call("{call add_person(?, ?, ?)}")) {
      call.execute(
          Parameter.in("Megan Marshall"),
          Parameter.in(29),
          Parameter.out(Types.BIGINT));

      long id = call.getOutParameter(3, long.class);
      System.out.format("created person (id=%d)\n", id);
    }


    try (JdbcCall call = jdbc.call("{ call add_person(?, ?, ?) }")) {
      createPerson("Jennifer Wilson", 29, call);
      createPerson("Nadine Bennett", 31, call);
      createPerson("Megan Marshall", 27, call);
    }

    jdbc.query()
        .using("SELECT * from PERSON ORDER BY age")
        .handlingResultWith(new ResultSetHandler<Void>() {
            @Override
            public Void handleResult(ResultSet rs) throws SQLException {
              while (rs.next()) {
                System.out.format("%-3d %-30s (age=%d)\n", rs.getLong("id"),
                    rs.getString("name"), rs.getInt("age"));
              }
              return null;
            }
        })
        .retrieveValue();

    jdbc.execute(
        "CREATE PROCEDURE find_persons_by_name(IN p_name VARCHAR(50)) " +
        "  READS SQL DATA " +
        "  DYNAMIC RESULT SETS 1 " +
        "BEGIN ATOMIC " +
        "  DECLARE result CURSOR WITH RETURN FOR " +
        "    SELECT * FROM person WHERE name LIKE p_name FOR READ ONLY; " +
        "  OPEN result; " +
        "END");

    try (JdbcCall call = jdbc.call("{ call find_persons_by_name(?) }")) {
      boolean hasResultSet = call.execute(Parameter.in("%Nadine%"));
      if (hasResultSet || call.getMoreResults()) {
        List<String> names = call.retrieveList("name", String.class);
        System.out.format("matching names: %s\n", names);
      }
      else {
        System.out.format("no names match");
      }
    }

    db.close();
  }

  private static void createPerson(String name, int age, JdbcCall call) {
    call.execute(Parameter.in(name),
        Parameter.in(age),
        Parameter.out(Types.BIGINT));
    System.out.format("created person %d\n", call.getOutParameter(3, Long.class));
  }

}
