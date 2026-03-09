# Plugin for support performance testing with SFTP in Gatling (3.15.x)

## Getting Started

Plugin is currently available for Scala 2.13.

You may add plugin as dependency in project with your tests. Add this to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.fherbreteau</groupId>
    <artifactId>gatling-sftp</artifactId>
    <version>0.0.12-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

## Example Scenarios

See the [examples](../src/test/scala/io/github/fherbreteau/gatling/sftp/examples/) directory for sample simulations using password and key-pair authentication.
