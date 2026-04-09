# Gatling SFTP Plugin

![Build](https://github.com/fherbreteau/gatling-sftp/actions/workflows/maven.yml/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=fherbreteau_gatling-sftp&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=fherbreteau_gatling-sftp)

A Gatling plugin for SFTP performance testing. Currently available for Scala 2.13 and compatible with Gatling 3.15.x.

## Getting Started

This plugin is currently available for Scala 2.13.

### SBT

Add the plugin as a dependency in your test project. In your `build.sbt`:

```scala
libraryDependencies += "io.github.fherbreteau" %% "gatling-sftp" % "<version>" % Test
```

## Example Scenarios

- **Scala** -- [Sample Simulations](src/test/scala/io/github/fherbreteau/gatling/sftp/examples)
- **Java** -- [Sample Simulations](src/test/java/io/github/fherbreteau/gatling/sftp/examples)

## Testing

The test suite includes:

- **Unit tests** -- models, builders, DSL, operation dispatch (with mocked SFTP client), credential loading, and path resolution
- **Integration tests** -- full SFTP operations (mkdir, rmdir, ls, upload, download, copy, move, delete) against an embedded Apache SSHD server (no Docker required)

Tests use [ScalaTest](https://www.scalatest.org/) with [Mockito](https://site.mockito.org/) and run via the `scalatest-maven-plugin`:

```bash
mvn -B test
```

To run the example Gatling simulations against a real SFTP server, start the Docker container first:

```bash
docker-compose up -d
mvn -B gatling:test
docker compose down
```
