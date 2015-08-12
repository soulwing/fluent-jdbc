/*
 * File created on Aug 12, 2015
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

import java.sql.Connection;

import org.soulwing.jdbc.source.StringSQLSource;

/**
 * Simple demo of using Fluent JDBC with a single connection.
 * <p>
 * When utilizing Fluent JDBC in a framework such as <a href="http://flywaydb.org">Flyway</a>
 * which wants to fully manage connection state, you can construct an instance
 * of {@link FluentJdbc} using a {@link Connection} object.  It will use this
 * single connection for all JDBC operations it performs.  The connection
 * <strong>will not</strong> be closed by Fluent JDBC.
 * <p>
 * A {@link FluentJdbc} object that is constructed using a {@link Connection}
 * object shouldn't be shared among multiple concurrent threads, since most
 * transaction management approaches assume a connection per thread.
 *
 * @author Carl Harris
 */
public class FluentJdbcSingleConnectionDemo {

  public static void main(String[] args) throws Exception {
    TestDatabase db = new TestDatabase();
    try (Connection connection = db.getDataSource().getConnection()) {

      FluentJdbc jdbc = new FluentJdbc(connection);

      jdbc.executeScript(new StringSQLSource(
          "CREATE TABLE person ( id IDENTITY, name VARCHAR(255), age INTEGER );" +
          "INSERT INTO PERSON(id, name, age) VALUES(1, 'Jennifer Wilson', 29);" +
          "INSERT INTO PERSON(id, name, age) VALUES(2, 'Nadine Bennett', 31);" +
          "INSERT INTO PERSON(id, name, age) VALUES(3, 'Megan Marshall', 27);"));

      int averageAge = jdbc.queryForType(int.class)
          .using("SELECT AVG(age) FROM person")
          .extractingColumn()
          .retrieveValue();

      System.out.format("average age: %d\n", averageAge);
    }

  }

}
