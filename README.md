sql-template
============

[![Build Status](https://travis-ci.org/soulwing/sql-template.svg?branch=master)](https://travis-ci.org/soulwing/sql-template)

A template for performing SQL using JDBC, inspired by Spring's `JdbcTemplate`.

Spring's `JdbcTemplate` is great... if you're using the Spring framework. 
However, if you're building apps based on Java EE 7, using `JdbcTemplate`
brings in an awful lot of Spring machinery that you don't really need.

This project focuses on the subset of SQL template features needed to perform
tasks like database migration (e.g. using [Flyway] (http://flywaydb.org)) and 
data loading. The assumption here is that you're going to be using JPA for most 
of your interaction with the database, but you need to do a little plain old SQL 
here and there and want that to be as simple to do as possible.

One of the less attractive aspects of Spring's `JdbcTemplate` is that it has
*many* overloaded methods with different argument types, which makes it harder
to understand and use.  This SQL template library has an operations API that
uses the builder and command object patterns to create a fluent languauge 
for specifying queries, updates, and stored procedure calls. Instead of trying 
to figure out which of the many overloads of the `query` method might be needed 
in a particular situation you can instead write code like this:

```
List<Person> results = sqlTemplate.queryForType(Person.class)
    .using("SELECT * FROM person where name like ?")
    .mappingRowsWith(new PersonMapper())
    .retrieveList(Parameter.with("%Nadine%");
```
This library is lightweight and depends only on the JDBC features of the 
JDK -- i.e. it adds only a single (small) JAR file dependency to your 
application.

Getting Started
===============

The `SQLTemplate` class provides the central API you use to perform SQL 
operations.  See the [Javadocs] (http://soulwing.github.io/sql-template/maven-site/apidocs/)
for full details of the API.  

It is a plain old Java object, with a constructor that takes
a JDBC `DataSource`.  An instance of `SQLTemplate` is thread safe and thus
can be used by any number of application components concurrently.

```
import javax.sql.DataSource;
import org.soulwing.sql.SQLTemplate;

DataSource dataSource = ... // typically injected by the container
SQLTemplate sqlTemplate = new SQLTemplate(dataSource);
```

Using `SQLTemplate` you can easily execute any combination of queries and updates
as well as SQL DDL statements.  The following simple example creates a table, 
inserts some values into it, and then queries and prints some results.  

##### `src/test/java/SQLTemplateDemo.java`: 
```
1   DataSource dataSource = ... // typically injected by the container
2   SQLTemplate sqlTemplate = new SQLTemplate(dataSource);

3   sqlTemplate.execute("CREATE TABLE person ( " + 
       "id BIGINT PRIMARY KEY, name VARCHAR(50), age INTEGER )");

4   sqlTemplate.executeScript(new StringSQLSource(
       "INSERT INTO PERSON(id, name, age) VALUES(1, 'Jennifer Wilson', 29);" +
       "INSERT INTO PERSON(id, name, age) VALUES(2, 'Nadine Bennett', 31);" +
       "INSERT INTO PERSON(id, name, age) VALUES(3, 'Megan Marshall', 27);"
    ));

5   List<Map> people = sqlTemplate.queryForType(Map.class)
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

7   try (SQLUpdate updater = sqlTemplate.update()
            .using("UPDATE person SET age = age + 1 WHERE id = ?")
            .repeatedly()) {
8     updater.execute(Parameter.with(2));
9     updater.execute(Parameter.with(3));
    }

10  int averageAge = sqlTemplate.queryForType(int.class)
        .using("SELECT AVG(age) FROM person")
        .extractingColumn()
        .retrieveValue();

11  System.out.format("average age: %d\n", averageAge);

12  int count = sqlTemplate.update()
        .using("DELETE FROM person")
        .execute();

13  System.out.format("deleted %d people\n", count);
```

The example demonstrates many of the salient features of `SQLTemplate`.

* Line 2 -- `SQLTemplate` is a POJO that you construct with a `DataSource`
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
  
In addition to the features shown in the demo, `SQLTemplate` provides support
for calling stored procedures and handling returned values and result sets --
see [Calling Stored Procedures] for details.


### JDBC Exception Handling

The JDBC API is designed such that almost every method throws the checked 
`SQLException` type.  As observed by others, this isn't very useful, since
in many circumstances it isn't feasible to recover from an `SQLException`. 
All components of `SQLTemplate` are designed to wrap `SQLException` in an 
unchecked `SQLRuntimeException`.  

Interface methods that your code implements are designed to throw `SQLException`
so that you don't have to worry about catching it and rethrowing it.  For 
example, in our demo `RowMapper` we invoked several methods of the 
`java.sql.ResultSet` interface each of which throws `SQLException`.  When 
`SQLTemplate` invokes our row mapper, it will take care of catching and
rethrowing any `SQLException` that occurs.

No effort is made to translate SQL exceptions into more meaningful exception
types, since the intended use of this library is for utility tasks such as 
data migration.  If this is a feature of significant interest, it could be
implemented -- feel free to submit a pull request!


Executing SQL DDL and DML Statements
====================================

The most common plain old SQL tasks are those that involve executing DDL
statements (such as `CREATE TABLE`) or DML statements (such as `INSERT INTO`).
Using `SQLTemplate`, you can not only invoke single SQL statements, but also
SQL scripts.

### Executing a single SQL statement

You can easily execute a statement in a string literal.
```
sqlTemplate.execute("CREATE TABLE person ( name VARCHAR(50) )");
```

You could also put a single statement into a file and execute it.  The `execute`
method accepts any `SQLSource` as input.  For example, using `ResourceSQLSource`,
we could execute a statement in a classpath resource:

```
sqlTemplate.execute(ResourceSQLSource.with("classpath:createTable.sql");
```

Or in any file:

```
sqlTemplate.execute(ResourceSQLSource.with("file:/path/to/some/file.sql");
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
sqlTemplate.executeScript(ResourceSQLSource.with("classpath:db/createSchema.sql"));
```

Or any other file:

```
sqlTemplate.executeScript(ResourceSQLSource.with("file:/path/to/createSchema.sql"));
```

`SQLTemplate` can read SQL scripts for most database dialects.  Simply terminate
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

> **NOTE**:
> At this time, the lexer used by `SQLTemplate` does not support statements
> with block constructs containing multiple valid SQL statements, such as those 
> used in defining stored procedures.  This will be addressed in a future 
> version.  


Performing Queries and Updates
==============================

`SQLTemplate` provides builder-pattern-based API for executing queries and
updates.

### Retrieving Rows

When invoking a query that returns multiple rows, `SQLTemplate` allows you to
easily map column values to create a list of Java objects that correspond to 
each row of data returned by the query.  The `RowMapper` interface allows you
to define how row contents should be used to create instances of almost any
object type:

```
int minAge = 21;
List<Person> person = sqlTemplate.queryForType(Person.class)
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
List<Integer> person = sqlTemplate.queryForType(Integer.class)
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
Person person = sqlTemplate.queryForType(Person.class)
    .using("SELECT * FROM person WHERE id = ?")
    .mappingRowsWith(new RowMapper<Person>() { 
        public void mapRow(ResultSet rs, int rowNum) throws SQLException {
          Person person = new Person();
          person.setId(rs.getLong("id"));
          person.setName(rs.getString("name"));
          person.setAge(rs.getInt("age"));
          return person;
        }})
    .retrieveList(Parameter.with(2)); 
```

Often, you want to query and get a single column value.  The query builder's
`extractColumn` method can be used to accomplish this easily:

```
int average = sqlTemplate.queryForType(int.class)
    .using"SELECT AVG(age) FROM person")
    .extractingColumn()
    .retrieveValue();
```

When the query returns multiple columns, you can identify the column value of
interest by name/label:

```
int min = sqlTemplate.queryForType(int.class)
    .using("SELECT MAX(age) max_age, MIN(age) min_age FROM person")
    .extractingColumn("min_age")
    .retrieveValue();
```

Or by column position (column indexes start at 1):

```
int min = sqlTemplate.queryForType(int.class)
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
sqlTemplate.query().
  .using("SELECT * FROM person")
  .handlingResultWith(new ResultSetExtractor()<Void> {
      public Void extract(ResultSet rs) throws SQLException {
        while (rs.next()) {
          exporter.exportPerson(rs.getLong("id"), rs.getString("name"), 
              rs.getInt("age")); 
        }
      })
  .retrieveValue();
```

By using `ResultSetHandler` you get the benefit of a closure based design
in your code, in addition to not having to handle `SQLException` yourself.


### Performing Updates

Executing statements that insert, update, or delete rows can be done with the
`update` method.  It returns an update builder that can be configured with
the SQL statement that you want to execute.  

For example:

```
sqlTemplate.update()
    .using("UPDATE person SET age = age + 1 WHERE id = ?")
    .execute(Parameter.with(2));
```    

The `update` method returns the number of rows affected by the given statement.


### Executing Queries and Updates Repeatedly

Normally, `SQLTemplate` closes the underlying JDBC statement and database 
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
    SQLUpdate updater = sqlTemplate.update().
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
objects it holds can be released.  As shown here, a convenient way to make it
gets closed is to use the Java 7 *try-with-resources* construct.  Of course, you 
could also accomplish the same thing using a *try-finally* construct. 

 
### Using files for SQL for Queries and Updates

Just as you can with the `execute` method, you can put your SQL query or
update statements in files and use an `SQLSource` to access them.  This 
makes your code cleaner and easier to understand.  Also, with some careful
organization of your resources, you can easily write code that handles
multiple database dialects.

Using `SQLSource` with the query and update methods is easy: 

```
long id = 2;
int age = sqlTemplate.queryForType(int.class)
    .using(ResourceSQLSource.with("classpath:sql/queries/findPersonById.sql"))
    .extractingColumn("age")
    .retrieveValue(Parameter.with(id));

age = age + 1;

sqlTemplate.update()
    .using(ResourceSQLSource.with("classpath:sql/updates/updatePersonAgeById.sql"))
    .execute(Parameter.with(age), Parameter.with(id));
```

Each passed `SQLSource` is used to read a single statement, and is then closed.


Calling Stored Procedures
=========================

Stored procedures can be called using `call` method.  This method creates call
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
try (SQLCall call = sqlTemplate.call("{ call add_person(?, ?, ?) }")) {
  call.execute(
    Parameter.in("Megan Marshall"), 
    Parameter.in(29), 
    Parameter.out(Types.BIGINT));
  
  long id = call.getOutParameter(3, long.class);
  System.out.format("created person (id=%d)\n", id);
}
```

As shown in the example, we can easily retrieve the values of OUT or INOUT
parameters using the `getOutParameter` method.  In this example, we use the
parameter position.  Some JDBC drivers allow the parameters to be retrieve 
by name -- the `getOutParameter` method has an overload that takes a parameter
name for this purpose.

### Procedures that Implicitly Return Result Sets

Some JDBC drivers implicitly return open cursors as `ResultSet` objects.
In ANSI SQL, a procedure that returns a cursor as a result set might be
specified like this:

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
try (SQLCall call = sqlTemplate.call("{ call find_persons_by_name(?) }")) {
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

See the [Javadocs] (http://soulwing.github.io/sql-template/maven-site/apidocs/org/soulwing/sql/SQLCall.html)
for more details on handling return values from stored procedures.


