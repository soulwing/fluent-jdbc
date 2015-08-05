sql-template
============

[![Build Status](https://travis-ci.org/soulwing/sql-template.svg?branch=master)](https://travis-ci.org/soulwing/sql-template)

A template for performing SQL using JDBC, inspired by Spring's `JdbcTemplate`.

Spring's `JdbcTemplate` is great, if you're using the Spring framework. 
However, if you're building apps based on Java EE 7, using `JdbcTemplate`
brings in an awful lot of Spring machinery that you don't really need.

This project focuses on the subset of SQL template features needed to perform
tasks like database migration (e.g. using [Flyway DB]).  The assumption here
is that you're going to be using JPA for all of your typical interaction with
the database, and that you need to do a little plain old SQL here and there
and want that to be as simple to do as possible.
