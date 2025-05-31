# Gatling SFTP Plugin
![Build](https://github.com/fherbreteau/gatling-sftp/actions/workflows/maven.yml/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=fherbreteau_gatling-sftp&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=fherbreteau_gatling-sftp)

Plugin for support performance testing with SFTP in Gatling (3.11.x)

# Usage

## Getting Started
Plugin is currently available for Scala 2.12.

You may add plugin as dependency in project with your tests. Write this to your build.sbt:

``` scala
libraryDependencies += "io.github.fherbreteau" %% "gatling-sftp" % <version> % Test
``` 

## Example Scenarios

### Scala
See [Sample Simulations](src/test/scala/io/github/fherbreteau/gatling/sftp/examples)

### Java
See [Sample Simulations](src/test/java/io/github/fherbreteau/gatling/sftp/examples)
