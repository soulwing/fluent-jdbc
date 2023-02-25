Fluent JDBC
===========

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.soulwing.jdbc/fluent-jdbc/badge.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3Aorg.soulwing.jdbc%20a%3Afluent-jdbc*)

Fluent JDBC is a lightweight façade for performing SQL using JDBC with a
fluent API based on the Builder and Command Object patterns.

This project focuses on the subset of JDBC features needed to perform tasks like 
database migration (e.g. using [Flyway](http://flywaydb.org)) and data loading. 
The assumption here is that you're going to be using JPA for most of your 
interaction with the database, but you need to do a little plain old SQL here 
and there and want that to be as simple to do as possible.

Fluent JDBC was inspired by Spring's `JdbcTemplate`. One of the less attractive 
aspects of Spring's `JdbcTemplate` is that it has *many* overloaded methods with 
different argument types, which makes it hard to understand and use.  Fluent JDBC 
has an operations API that uses the Builder and Command Object design patterns to 
create a fluent language for specifying queries, updates, and stored procedure 
calls. Instead of trying to figure out which of the many overloads of the `query` 
method might be needed in a particular situation you can instead write code like 
this:

```
List<Person> results = jdbc.queryForType(Person.class)
    .using("SELECT * FROM person WHERE name LIKE ?")
    .mappingRowsWith(new PersonMapper())
    .retrieveList(Parameter.with("%Nadine%"));
```
This library is lightweight and depends only on the JDBC features of the 
JDK -- i.e. it adds only a single (small) JAR file dependency to your 
application.

Binary Distribution
===================

Fluent JDBC is available from [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.soulwing.jdbc%22%20a%3A%22fluent-jdbc%22).
You can use Fluent JDBC by simply setting up your build system (Maven, Gradle, 
Ivy, etc) to include it as a dependency.
 
Documentation
=============

There are two main resources for learning and using Fluent JDBC

* [Fluent JDBC Wiki](https://github.com/soulwing/fluent-jdbc/wiki)
* [API Javadocs](http://soulwing.github.io/fluent-jdbc/maven-site/apidocs/)
  

