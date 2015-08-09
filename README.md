sql-template
============

[![Build Status](https://travis-ci.org/soulwing/sql-template.svg?branch=master)](https://travis-ci.org/soulwing/sql-template)

A template for performing SQL using JDBC, inspired by Spring's `JdbcTemplate`.

Spring's `JdbcTemplate` is great... if you're using the Spring framework. 
However, if you're building apps based on Java EE 7, using `JdbcTemplate`
brings in an awful lot of Spring machinery that you don't really need.

This project focuses on the subset of SQL template features needed to perform
tasks like database migration (e.g. using [Flyway DB]) and data loading. The 
assumption here is that you're going to be using JPA for all of your typical 
interaction with the database, and that you need to do a little plain old SQL 
here and there and want that to be as simple to do as possible.

This library is small and depends only on the JDBC features of the JDK -- i.e.
it adds a single (and small) JAR file dependency to your Java EE application.


Getting Started
===============

[Describe how to create an SQLTemplate and show a basic example of using it
to create a table and insert some data]

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

> At this time, the lexer used by `SQLTemplate` does not support statements
> with block constructs containing multiple valid SQL statements, such as those 
> used in defining stored procedures.  This will be addressed in a future 
> version.  


Performing Queries and Updates
==============================

`SQLTemplate` provides a number of query and update methods to make it easy
to retrieve and change data in your database.

### Retrieving Rows

When invoking a query that returns multiple rows, `SQLTemplate` allows you to
easily map column values to create a list of Java objects that correspond to 
each row of data returned by the query.  The `RowMapper` interface allows you
to define how row contents should be used to create instances of almost any
object type:

```
int minAge = 21;
List<Person> person = sqlTemplate.queryForObject(
    "SELECT * FROM person WHERE age >= ? ORDER BY age", 
    new Parameter[] { Parameter.with(minAge) },
    new RowMapper<Person>() { 
      public void mapRow(ResultSet rs, int rowNum) throws SQLException {
        Person person = new Person();
        person.setId(rs.getLong("id"));
        person.setName(rs.getString("name"));
        person.setAge(rs.getInt("age"));
        return person;
      }
    });
```

The `RowMapper` is invoked once for each row, and is provided with a JDBC
`ResultSet` positioned on the current row and the index of the row (starting
at 1).

> Note that query parameters are specified using the `Parameter` class. This
> class has `with` factory methods to create instances that specify a value
> and (optionally) an SQL type.

You can also extract all values of a given column, using a `ColumnExtractor` to
describe the column.

```
int minAge = 21;
List<Integer> person = sqlTemplate.queryForObject(
    "SELECT * FROM person WHERE age >= ?", 
    ColumnExtractor.with("age", Integer.class),
    Parameter.with(minAge));
```

If you want to do your own manipulation of the `ResultSet` you can
use the `ResultSetExtractor` interface.

```
sqlTemplate.query("SELECT * FROM person", new ResultSetExtractor()<Void> {
  public Void extract(ResultSet rs) throws SQLException {
    while (rs.next()) {
      exportPerson(rs.getLong("id"), rs.getString("name"), rs.getInt("age")); 
    }
  }
});
```

By using `ResultSetExtractor` you get the benefit of a closure based design
in your code, in addition to not having to handle `SQLException` yourself.


### Single Row Queries

For queries that return a single row, `SQLTemplate` provides the 
`queryForObject` method for getting a Java object from a single row.

You can use a `RowMapper` to turn the resulting row of a single row query
into an object:

```
Person person = sqlTemplate.queryForObject(
    "SELECT * FROM person WHERE id = ?", 
    new Parameters[] { Parameter.with(2) },
    new RowMapper<Person>() { 
      public void mapRow(ResultSet rs, int rowNum) throws SQLException {
        Person person = new Person();
        person.setId(rs.getLong("id"));
        person.setName(rs.getString("name"));
        person.setAge(rs.getInt("age"));
        return person;
      }
    });
```

Often, you want to query and get a single column value.  The `ColumnExtractor`
class provides the mechanism for you to describe the column.

```
int average = sqlTemplate.queryForObject("SELECT AVG(age) FROM person",
    ColumnExtractor.with(int.class));
```

When the query returns multiple columns, you can identify the column value of
interest by name/label:

```
int max = sqlTemplate.queryForObject(
    "SELECT AVG(age) avg_age, MAX(age) max_age, MIN(age) min_age FROM person",
    ColumnExtractor.with("max_age", int.class));
```

Or by column position (column indexes start at 1):

```
int max = sqlTemplate.queryForObject(
    "SELECT AVG(age) avg_age, MAX(age) max_age, MIN(age) min_age FROM person",
    ColumnExtractor.with(2, int.class));
```

> You should use `queryForObject` only when you expect exactly one row. If the
> query returns no rows, the `SQLNoResultException` will be thrown. If the
> query returns more than one row, the `SQLNonUniqueResultException` will be
> thrown.


### Performing Updates

Executing statements that insert, update, or delete rows can be done with the
`update` method.  For example:

```
sqlTemplate.update("UPDATE person SET age = age + 1 WHERE id = ?",
    Parameter.with(2));
```    

The `update` method returns the number of rows affected by the given statement.


### Reusing Prepared Statements for Queries and Updates

Often statements that query or update the database need to be executed 
repeatedly, with different parameters. For these situations, each of the
query and update methods on `SQLTemplate` allows you to pass a 
*statement preparer* instead of passing an SQL statement.

The SQL associated with a statement preparer is parsed once, and
the resulting JDBC statement object and associated connection are cached until
the preparer is closed. This allows you to repeatedly pass the same statement 
preparer to a query or update method, while passing different parameters to be 
bound before each execution of the the prepared statement.

The provided `StatementPreparer` class provides factory methods for creating
thread-safe lazy statement preparing objects. It has methods for preparing a 
statement from SQL in a string, or from an `SQLSource`. You could also make
your own statement preparer by implementing the `PreparedStatementCreator`
interface.

Suppose you are importing information from a CSV file. For each
line in the CSV file, you want to execute the same `INSERT` statement, with
different values. This could be accomplished as follows:

```
final File csvFile = new File("people.csv");
final String sql = "INSERT INTO person(id, name, age) VALUES(?, ?, ?)";

try (StatementPreparer preparer = StatementPreparer.with(sql);
  CSVReader reader = new CSVReader(csvFile)) {
  while (reader.hasNext()) {
    CSV csv = reader.next();
    sqlTemplate.update(preparer, Parameter.with(Long.valueOf(csv.get(0))),
         Parameter.with(csv.get(2)), Parameter.with(Integer.valueOf(csv.get(2))));
  }  
}
```

> It is important to close a statement preparer when you no longer need it, so 
> that the database connection and other JDBC objects it holds can be released. 
> As shown here, a convenient way to make sure the preparer is closed is to
> use the Java 7 *try-with-resources* construct.  Of course, you could also
> accomplish the same thing using a *try-finally* construct. 

 
### Using files for SQL for Queries and Updates

Just as you can with the `execute` method, you can put your SQL query or
update statements in files and use an `SQLSource` to access them.  This 
makes your code cleaner and easier to understand.  Also, with some careful
organization of your resources, you can easily write code that handles
multiple database dialects.

Using `SQLSource` with the query and update methods is easy: 

```
long id = 2;
int age = sqlTemplate.queryForObject(
    ResourceSQLSource.with("classpath:sql/queries/findPersonById.sql"),
    ColumnExtractor.with("age", int.class), Parameter.with(id));

age = age + 1;

sqlTemplate.update(
    ResourceSQLSource.with("classpath:sql/updates/updatePersonAgeById.sql"),
    Parameter.with(age), Parameter.with(id));
```

Each passed `SQLSource` is used to read a single statement, and then the
source is closed.