Fluent JDBC
===========

[![Build Status](https://travis-ci.org/soulwing/fluent-jdbc.svg?branch=master)](https://travis-ci.org/soulwing/fluent-jdbc)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.soulwing.jdbc/fluent-jdbc/badge.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3Aorg.soulwing.jdbc%20a%3Afluent-jdbc*)

Fluent JDBC is a lightweight façade for performing SQL using JDBC, inspired by
Spring's `JdbcTemplate`.

Spring's `JdbcTemplate` is great... if you're using the Spring framework. 
However, if you're building apps based on Java EE 7, using `JdbcTemplate`
brings in an awful lot of Spring machinery that you don't really need.

This project focuses on the subset of JDBC features needed to perform tasks like 
database migration (e.g. using [Flyway] (http://flywaydb.org)) and data loading. 
The assumption here is that you're going to be using JPA for most of your 
interaction with the database, but you need to do a little plain old SQL here 
and there and want that to be as simple to do as possible.

One of the less attractive aspects of Spring's `JdbcTemplate` is that it has
*many* overloaded methods with different argument types, which makes it hard
to understand and use.  Fluent JDBC has an operations API that uses the Builder 
and Command design patterns to create a fluent language for specifying queries, 
updates, and stored procedure calls. Instead of trying to figure out which of 
the many overloads of the `query` method might be needed in a particular 
situation you can instead write code like this:

```
List<Person> results = jdbc.queryForType(Person.class)
    .using("SELECT * FROM person WHERE name LIKE ?")
    .mappingRowsWith(new PersonMapper())
    .retrieveList(Parameter.with("%Nadine%"));
```
This library is lightweight and depends only on the JDBC features of the 
JDK -- i.e. it adds only a single (small) JAR file dependency to your 
application.

Getting Started
===============

Fluent JDBC is available from Maven Central, so you can make use of it in your
project by simply including it as a dependency:

```
<dependency>
  <groupId>org.soulwing.jdbc</groupId>
  <artifactId>fluent-jdbc</artifactId>
  <version>1.1.4</version>
</dependency>
```

The `FluentJdbc` class provides the central API you use to perform SQL 
operations.  See the [Javadocs] (http://soulwing.github.io/fluent-jdbc/maven-site/apidocs/)
for full details of the API.  

It is a plain old Java object, with a constructor that takes
a JDBC `DataSource`.  An instance of `FluentJdbc` is thread safe and thus
can be used by any number of application components concurrently.

```
import javax.sql.DataSource;
import org.soulwing.jdbc.FluentJdbc;

DataSource dataSource = ... // typically injected by your container
FluentJdbc jdbc = new FluentJdbc(dataSource);
```

Using Fluent JDBC you can easily execute any combination of queries and updates
as well as SQL DDL statements.  The following simple example creates a table, 
inserts some values into it, and then queries and prints some results.  

##### `src/test/java/FluentJdbcDemo.java`: 
```
1   DataSource dataSource = ... // typically injected by the container
2   FluentJdbc jdbc = new FluentJdbc(dataSource);

3   jdbc.execute("CREATE TABLE person ( " + 
       "id BIGINT PRIMARY KEY, name VARCHAR(50), age INTEGER )");

4   jdbc.executeScript(new StringSQLSource(
       "INSERT INTO PERSON(id, name, age) VALUES(1, 'Jennifer Wilson', 29);" +
       "INSERT INTO PERSON(id, name, age) VALUES(2, 'Nadine Bennett', 31);" +
       "INSERT INTO PERSON(id, name, age) VALUES(3, 'Megan Marshall', 27);"
    ));

5   List<Map> people = jdbc.queryForType(Map.class)
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

6   System.out.format("people: %s\n", people);

7   try (JdbcUpdate updater = jdbc.update()
            .using("UPDATE person SET age = age + 1 WHERE id = ?")
            .repeatedly()) {
8     updater.execute(Parameter.with(2));
9     updater.execute(Parameter.with(3));
    }

10  int averageAge = jdbc.queryForType(int.class)
        .using("SELECT AVG(age) FROM person")
        .extractingColumn()
        .retrieveValue();

11  System.out.format("average age: %d\n", averageAge);

12  int count = jdbc.update()
        .using("DELETE FROM person")
        .execute();

13  System.out.format("deleted %d people\n", count);
```

The example demonstrates many of the salient features of Fluent JDBC.

* Line 2 -- `FluentJdbc` is a POJO that you construct with a `DataSource`
* Line 3 -- Execute arbitrary SQL DDL or DML statements using `execute`
* Line 4 -- Execute DDL or DML scripts using an `SQLSource` with 
  `executeScript`; here we use `StringSQLSource`, but there are useful 
  implementations that allow the use of files or classpath resources
* Line 5 -- Execute a query  to retrieve a list of objects of arbitrary type
  using the `queryByType` method; the query builder's `using` method specifies
  the SQL statement, and the builder  can be configured with a row mapper or 
  column extractor.
* Line 7 -- Prepare an update statement for efficient repeated use using 
  `repeatedly` on the query builder; note the use of the *try-with-resources*
  construct -- queries and updates that are configured for repeated use must be
  closed when no longer needed.
* Line 8-9 -- Execute an update using the prepared statement and arbitrary 
  parameter values specified using `Parameter.with`
* Line 10 -- Execute a single row query, extract a column value and return it
  using the query builder's `retrieveValue` method.  Since we didn't specify
  a column for `extractingColumn`, the first column value
  will be extracted, we but we can also specify a column by label or index
* Line 12 -- Executing an update returns the number of affected rows
  
In addition to the features shown in the demo, Fluent JDBC provides support
for calling stored procedures and handling returned values and result sets --
see [Calling Stored Procedures] (#calling-stored-procedures) for details.


### JDBC Exception Handling

The JDBC API is designed such that almost every method throws the checked 
`SQLException` type.  As observed by others, this isn't very useful, since
in many circumstances it isn't feasible to recover from an `SQLException`. 
All components of Fluent JDBC are designed to wrap `SQLException` in an 
unchecked `SQLRuntimeException`.  

Interface methods that your code implements are designed to throw `SQLException`
so that you don't have to worry about catching it and rethrowing it.  For 
example, in our demo `RowMapper` we invoked several methods of the 
`java.sql.ResultSet` interface each of which throws `SQLException`.  When 
Fluent JDBC invokes our row mapper, it will take care of catching and
rethrowing any `SQLException` that occurs.

No effort is made to translate SQL exceptions into more meaningful exception
types, since the intended use of this library is for utility tasks such as 
data migration.  If this is a feature of significant interest, it could be
implemented -- feel free to submit a pull request!


Executing SQL DDL and DML Statements
====================================

The most common plain old SQL tasks are those that involve executing DDL
statements (such as `CREATE TABLE`) or DML statements (such as `INSERT INTO`).
Using Fluent JDBC, you can not only invoke single SQL statements, but also
SQL scripts.

### Executing a single SQL statement

You can easily execute a statement in a string literal.
```
jdbc.execute("CREATE TABLE person ( name VARCHAR(50) )");
```

You could also put a single statement into a file and execute it.  The `execute`
method accepts any `SQLSource` as input.  For example, using `ResourceSQLSource`,
we could execute a statement in a classpath resource:

```
jdbc.execute(ResourceSQLSource.with("classpath:createTable.sql");
```

Or in any file:

```
jdbc.execute(ResourceSQLSource.with("file:/path/to/some/file.sql");
```

When using the `execute` method with a `SQLSource`, only the first statement 
that appears in the file is executed. If you want to execute a sequence of 
statements (i.e. a script), read on.

### Executing an SQL Script

Of course, when doing tasks like database initialization or database migration,
you often want to execute all of the statements in a file. You can do that
using the `executeScript` method. This method takes a single `SQLSource` 
argument, that provides the SQL script to execute. Using `ResourceSQLSource`
we could execute a classpath resource as a script:

```
jdbc.executeScript(ResourceSQLSource.with("classpath:db/createSchema.sql"));
```

Or any other file:

```
jdbc.executeScript(ResourceSQLSource.with("file:/path/to/createSchema.sql"));
```

Fluent JDBC can read SQL scripts for most database dialects.  Simply terminate
each statement with a semicolon (;).  A script can contain single-line or block
comments, too.  For example:

```
-- create the PERSON table
CREATE TABLE person (id BIGINT, name VARCHAR(50), age INTEGER);

INSERT INTO PERSON(id, name, age)     -- first person
VALUES(1, 'Jennifer Wilson', 29);

INSERT INTO PERSON(id, name, age)     -- another person
VALUES(2, 'Nadine Bennett', 31);

/* this one is commented out for now
INSERT INTO PERSON(id, name, age)     -- excluded person
VALUES(3, 'Megan Marshall', 27);
*/
```

When executing a script, Fluent JDBC uses a single connection to execute
every statement in the file.  Moreover, it configures the connection to
utilize auto-commit mode, so that each statement in the script is committed 
on successful execution.  This enables Fluent JDBC to support an execution 
mode in which all errors encountered while executing the script are ignored, 
which is useful in schema drop-create scenarios.

> **NOTE**:
> At this time, the script parser used by `executeScript` does not support 
> statements with block constructs containing multiple valid SQL statements, 
> such as those used in defining stored procedures.  This will be addressed 
> in a future version. Meanwhile, you should use the `execute` method for
> DDL statements that define stored procedures.


Performing Queries and Updates
==============================

Fluent JDBC provides an API that is based on the *builder* and *command* 
patterns for executing queries and updates.

### Retrieving Rows

When invoking a query that returns multiple rows, Fluent JDBC allows you to
easily map column values to create a list of Java objects that correspond to 
each row of data returned by the query.  The `RowMapper` interface allows you
to define how row contents should be used to create instances of almost any
object type:

```
int minAge = 21;
List<Person> person = jdbc.queryForType(Person.class)
    .using("SELECT * FROM person WHERE age >= ? ORDER BY age")
    .mappingRowsWith(new RowMapper<Person>() { 
                         public void mapRow(ResultSet rs, int rowNum) throws SQLException {
                           Person person = new Person();
                           person.setId(rs.getLong("id"));
                           person.setName(rs.getString("name"));
                           person.setAge(rs.getInt("age"));
                           return person;
                         }
                     })
    .retrieveList(Parameter.with(minAge);
```

The `RowMapper` is invoked once for each row, and is provided with a JDBC
`ResultSet` positioned on the current row and the index of the row (starting
at 1).

Note that query parameters are specified using the `Parameter` class. This
class has `with` factory methods to create instances that specify a value
and (optionally) an SQL type.

You can also extract all values of a given column:

```
int minAge = 21;
List<Integer> person = jdbc.queryForType(Integer.class)
    .using("SELECT * FROM person WHERE age >= ?")
    .extractingColumn("age")
    .retrieveList(Parameter.with(minAge)); 
```

In addition to specifying a column label (name), you can also specify its
index (starting at 1).  For the common case of single column queries, you
can omit the label or index to retrieve the first column.   


### Single Row Queries

For queries that return a single row, the query builder provides the
`retrieveValue` method for getting a Java object from a single row.

You can use a `RowMapper` to turn the resulting row of a single row query
into an object:

```
Person person = jdbc.queryForType(Person.class)
    .using("SELECT * FROM person WHERE id = ?")
    .mappingRowsWith(new RowMapper<Person>() { 
        public void mapRow(ResultSet rs, int rowNum) throws SQLException {
          Person person = new Person();
          person.setId(rs.getLong("id"));
          person.setName(rs.getString("name"));
          person.setAge(rs.getInt("age"));
          return person;
        }})
    .retrieveValue(Parameter.with(2)); 
```

Often, you want to query and get a single column value.  The query builder's
`extractingColumn` method can be used to accomplish this easily:

```
int average = jdbc.queryForType(int.class)
    .using"SELECT AVG(age) FROM person")
    .extractingColumn()
    .retrieveValue();
```

When the query returns multiple columns, you can identify the column value of
interest by name/label:

```
int min = jdbc.queryForType(int.class)
    .using("SELECT MAX(age) max_age, MIN(age) min_age FROM person")
    .extractingColumn("min_age")
    .retrieveValue();
```

Or by column position (column indexes start at 1):

```
int min = jdbc.queryForType(int.class)
    .using("SELECT MAX(age) max_age, MIN(age) min_age FROM person")
    .extractingColumn(2)
    .retrieveValue();
```

> **NOTE**:
> You should use `retrieveValue` only when you expect exactly one row. If the
> query returns no rows, the `SQLNoResultException` will be thrown. If the
> query returns more than one row, the `SQLNonUniqueResultException` will be
> thrown.

### Using `ResultSetHandler`

If you want to do your own manipulation of the `ResultSet` returned by a query
you can use the `ResultSetHandler` interface.  Suppose we wanted to export
some data:

```
jdbc.query().
  .using("SELECT * FROM person")
  .handlingResultWith(new ResultSetExtractor()<Void> {
      public Void extract(ResultSet rs) throws SQLException {
        while (rs.next()) {
          exporter.exportPerson(rs.getLong("id"), rs.getString("name"), 
              rs.getInt("age")); 
        }
        return null;
      })
  .execute();
```

By using `ResultSetHandler` you get the benefit of a closure based design
in your code, in addition to not having to handle `SQLException` yourself.


### Performing Updates

Executing statements that insert, update, or delete rows can be done with the
`update` method.  It returns an update builder that can be configured with
the SQL statement that you want to execute.  

For example:

```
jdbc.update()
    .using("UPDATE person SET age = age + 1 WHERE id = ?")
    .execute(Parameter.with(2));
```    

The `update` method returns the number of rows affected by the given statement.


### Executing Queries and Updates Repeatedly

Normally, Fluent JDBC closes the underlying JDBC statement and database 
connection after the statement has been executed (using `execute`, 
`retrieveList`, or `retrieveValue`) and the result has been retrieved. 

However, sometimes you want to query or update the database repeatedly, using
the same statement, with different parameters.  For these situations, the query 
and update builders provide a `repeatedly` method which is used to configure
the query or update so that it can be executed as many times as needed.

When configured for repeated execution, the SQL is parsed once, and
the resulting JDBC statement object and associated connection are cached until
the query or update is closed. This allows you to repeatedly execute the
SQL statement, while passing different parameters to be bound before each 
execution of the the prepared statement.

Suppose you are importing information from a CSV file. For each
line in the CSV file, you want to execute the same `INSERT` statement, with
different values. This could be accomplished as follows:

```
final File csvFile = new File("people.csv");
final String sql = "";

try (CSVReader reader = new CSVReader(csvFile);
    JdbcUpdate updater = jdbc.update().
        .using("INSERT INTO person(id, name, age) VALUES(?, ?, ?)")
        .repeatedly()) {  
  while (reader.hasNext()) {
    CSV csv = reader.next();
    updater.execute(
        Parameter.with(Long.valueOf(csv.get(0))),
        Parameter.with(csv.get(2)), 
        Parameter.with(Integer.valueOf(csv.get(2)));
  }  
}
```

When a query or updater is configured for repeated execution, you must close
it when it is no longer needed, so that the database connection and other JDBC 
objects it holds can be released.  As shown here, a convenient way to make 
certain it gets closed is to use the Java 7 *try-with-resources* construct.  Of 
course, you could also accomplish the same thing using a *try-finally* construct,
in which you explicitly call the `close` method.

 
### Using files for SQL for Queries and Updates

Just as you can with the `execute` method, you can put your SQL query or
update statements in files and use an `SQLSource` to access them.  This 
makes your code cleaner and easier to understand.  Also, with some careful
organization of your resources, you can easily write code that handles
multiple database dialects.

Using `SQLSource` with the query and update methods is easy: 

```
long id = 2;
int age = jdbc.queryForType(int.class)
    .using(ResourceSQLSource.with("classpath:sql/queries/findPersonById.sql"))
    .extractingColumn("age")
    .retrieveValue(Parameter.with(id));

age = age + 1;

jdbc.update()
    .using(ResourceSQLSource.with("classpath:sql/updates/updatePersonAgeById.sql"))
    .execute(Parameter.with(age), Parameter.with(id));
```

Each passed `SQLSource` is used to read a single statement, and is then closed.


Calling Stored Procedures
=========================

Stored procedures can be called using `call` method.  This method creates a call
object that can be executed using the `execute` method.  Zero or more parameters
for the call can be specified as arguments to the execute method.  Each parameter
can be specified as IN, OUT, or INOUT.

Unlike the query and update builder objects, *every* call object is configured
to support repeated execution with different parameter lists.  This is 
necessitated by the way the JDBC API exposes the results of each invocation of
a stored procedure on the `CallableStatement` object.

Because every call object supports repeated execution, you **must** close a call
object when it is no longer needed, by either invoking the `close` method or by
nesting the call object in a *try-with-resources* construct.

The JDBC specification allows you the use of a vendor-independent syntax to
call stored procedures, in addition to whatever call syntax is supported by your
JDBC driver vendor.  See the Javadocs for [CallableStatement] (http://docs.oracle.com/javase/7/docs/api/java/sql/CallableStatement.html) 
for details.  For the sake of simplicity, the examples shown below use the 
vendor independent syntax, but you could also use whatever syntax is supported 
by the JDBC vendor in your own code.

### Passing IN and OUT parameters

The following example calls a stored procedure named `add_person` that has
three parameters.  The declaration in ANSI SQL might look something like this:

```
PROCEDURE add_person(IN name VARCHAR, IN age INTEGER, OUT id BIGINT) ...
```

We can call this procedure as follows:
```
try (SQLCall call = jdbc.call("{ call add_person(?, ?, ?) }")) {
  call.execute(
    Parameter.in("Megan Marshall"), 
    Parameter.in(29), 
    Parameter.out(Types.BIGINT));
  
  long id = call.getOutParameter(3, long.class);
  System.out.format("created person (id=%d)\n", id);
}
```

As shown in the example, we can easily retrieve the values of OUT or INOUT
parameters using the `getOutParameter` method.  In this example, we specify
the parameter by position. Some JDBC drivers allow the parameters to be 
retrieved by name -- the `getOutParameter` method has an overload that takes
a parameter name as a string for this purpose.

### Procedures that Implicitly Return Result Sets

Some JDBC drivers return open cursors as `ResultSet` objects. In ANSI SQL, a 
procedure that returns a cursor as a result set might be written like this:

```
CREATE PROCEDURE find_persons_by_name(IN p_name VARCHAR(50))
  READS SQL DATA
  DYNAMIC RESULT SETS 1
BEGIN ATOMIC
  DECLARE result CURSOR WITH RETURN FOR 
    SELECT * FROM person WHERE name LIKE p_name FOR READ ONLY;
  OPEN result;
END
```

Invoking the `find_persons_by_name` procedure can be done using the `call` 
method as follows:

```
try (SQLCall call = jdbc.call("{ call find_persons_by_name(?) }")) {
  boolean isResultSet = call.execute(Parameter.in("%Nadine%"));
  if (isResultSet || call.getMoreResults()) {
    List<String> names = call.retrieveList("name", String.class);
    System.out.format("matching names: %s\n", names);
  }
  else {
    System.out.format("no names match");
  }
}
```

The *if* statement inside of the *try* block warrants some explanation. Some 
JDBC drivers will automatically return the first result set and indicate that 
they have done so by returning `true` from the `execute` method.  Others will 
return `false` from `execute`, but a subsequent call to `getMoreResults`
will return `true` indicating that a result set is available.  The example code
should work correctly in either case.

Once we know that a result set is available, we have the same choices for
handling it that we have when invoking a query:

* map the result set to objects using a `RowMapper`
* extract a column value from each row (as shown here)
* handle the result set yourself using a `ResultSetHandler`

See the [Javadocs] (http://soulwing.github.io/fluent-jdbc/maven-site/apidocs/org/soulwing/jdbc/JdbcCall.html)
for more details on handling return values from stored procedures.


Using a Single Connection
=========================

Some JDBC frameworks/libraries want to fully manage connection state, by 
providing `Connection` objects to your code, instead of allowing you to 
obtain connections from a `DataSource` as needed. [Flyway] (http://flywaydb.org)
is an example -- when you write a `JdbcMigration`, the `migrate` method gets
a connection object that it must use to do its work.

In this case, you can construct a `FluentJdbc` instance using the connection 
object, instead of a data source. Fluent JDBC will use the single connection
that was provided to it for all JDBC operations you perform using that instance.
Fluent JDBC will not close this connection -- the creator of the connection is
responsible for closing it.

**NOTE**:  A `FluentJdbc` instance that is constructed using a `Connection`
should not be shared with multiple concurrent threads, since most transaction 
management mechanisms assume a connection-per-thread model.

The following example shows how this works. The first couple of lines of code
represent "framework" code that has somehow obtained a `DataSource` and gets
a connection from it.  The code inside of the *try* block represents your code
that is being invoked by the framework.  

Inside of the *try* block, a `FluentJdbc` instance is created using the 
connection.  All of the code in the *try* block that uses that instance of 
`FluentJdbc` is using the same connection to perform JDBC operations.

```
DataSource dataSource = ...
try (Connection connection = dataSource.getConnection()) {

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
```

Logging SQL Statements
======================

Fluent JDBC supports logging of both SQL statements and bound placeholder 
values.  Enabling statement logging on `System.out` is as simple as this:

```
FluentJdbc jdbc = new FluentJdbc(...);
jdbc.setLogger(System.out);
```

If you want to log both statements and bound parameter values, specify
the additional `traceEnabled` flag value as `true`:

```
jdbc.setLogger(System.out, true);
```

In addition to logging to `System.out` you can also configure Fluent JDBC
to use any implementation of its `JdbcLogger` interface.  This is a 
very simple adapter interface that can easily be implemented to work with almost 
any logging framework.  What's more, Fluent JDBC includes support for many 
popular frameworks, including *Slf4j*, *Log4j*, *Commons Logging*, and 
*java.util.Logging (JULI)*.

Especially when using a logging framework, you may wish to format SQL statements
prior to logging them.  Fluent JDBC includes a `FormattingJdbcLogger` that you
can use to stack formatting on top of any `JdbcLogger`.  Formatters implement
the `SQLFormatter` interface.  A very simple, single-line formatter that cleans
up whitespace and removes SQL comments is also included (`SimpleSQLFormatter`).

For example, suppose we wanted to log single-line formatted SQL to a 
`java.util.logging.Logger`.  The following configuration is all we need.

```
import java.util.logging.Logger;

import org.soulwing.jdbc.FluentJdbc;
import org.soulwing.jdbc.logger.FormattingJdbcLogger;
import org.soulwing.jdbc.logger.JuliJdbcLogger;
import org.soulwing.jdbc.source.SimpleSQLFormatter;

Logger logger = ...

FluentJdbc jdbc = new FluentJdbc(...);
jdbc.setLogger(new FormattingJdbcLogger(new JuliJbdcLogger(logger)));
```

See the [Javadocs]
(http://soulwing.github.io/fluent-jdbc/maven-site/apidocs/org/soulwing/jdbc/logger/JdbcLogger.html)
for complete details on logging and formatting.
