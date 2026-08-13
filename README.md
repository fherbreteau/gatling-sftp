# Gatling SFTP Plugin

![Build](https://github.com/fherbreteau/gatling-sftp/actions/workflows/ci.yml/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=fherbreteau_gatling-sftp&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=fherbreteau_gatling-sftp)

A Gatling plugin that adds SFTP protocol support for performance testing (compatible with Gatling 3.15.x).

## Getting Started

This plugin is currently available for Scala 2.13, Java 17, Kotlin.

You may add plugin as dependency in project with your tests.

### SBT

Write this to your `build.sbt`:

```scala
libraryDependencies += "io.github.fherbreteau" %% "gatling-sftp" % "<version>" % Test
```
### Maven

Write this to your dependencies block in your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.fherbreteau</groupId>
    <artifactId>gatling-sftp</artifactId>
    <version>${version}</version>
    <scope>test</scope>
</dependency>
```

### Gradle

Write this to your dependencies block in your `build.gradle`:

```kotlin
gatling("io.github.fherbreteau:gatling-sftp:<version>")
```

## Example Scenarios

- **Scala** -- [Sample Simulations](src/test/scala/io/github/fherbreteau/gatling/sftp/examples)
- **Java** -- [Sample Simulations](src/test/java/io/github/fherbreteau/gatling/sftp/examples)
- **Kotlin** -- [Sample Simulations](src/test/kotlin/io/github/fherbreteau/gatling/sftp/examples)

## Advanced Configuration

### Thread Pool Configuration

For high-performance scenarios, you can configure the thread pool size:

```scala
val sftpProtocol: SftpProtocolBuilder = sftp
  .server("localhost")
  .port(2222)
  .password("#{username}", "#{password}")
  .threadPoolSize(8) // Configure thread pool size for concurrent operations
```

### Connection Timeout Handling

Configure connection timeouts and retry logic:

```scala
// Example with error handling
val scn: ScenarioBuilder = scenario("SFTP Scenario with Retries")
  .feed(credentialsFeeder)
  .exec(
    tryMax(3) { // Retry failed operations up to 3 times
      exec(sftp("Upload file with retry").upload(source))
    }
  )
```

### Large File Transfer Optimization

For large file transfers, consider:

```scala
// Configure buffer sizes and transfer settings
val sftpProtocol: SftpProtocolBuilder = sftp
  .server("localhost")
  .port(2222)
  .password("#{username}", "#{password}")
  .localPath(Paths.get("./large_files"))
  .remotePath("/remote/large_files")
```

## Security Best Practices

1. **Credential Management**: Use environment variables or secure vaults for credentials
2. **Key Rotation**: Regularly rotate SSH keys used for authentication
3. **Network Security**: Use SFTP over VPN or private networks when possible
4. **Logging**: Enable debug logging only when needed for troubleshooting

## Performance Tuning

- **Thread Pool Size**: Adjust based on your SFTP server capacity (default: 1)
- **Connection Reuse**: The plugin creates new connections for each operation for isolation
- **Batch Operations**: Group multiple operations in a single scenario for efficiency

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

## Dependency Information

### Required Dependencies

The core plugin requires:
- Apache SSHD Core (`org.apache.sshd:sshd-core`)
- Apache SSHD SFTP (`org.apache.sshd:sshd-sftp`)

### Optional Dependencies

For Ed25519 key support, include Bouncy Castle:
```xml
<dependency>
    <groupId>org.bouncycastle</groupId>
    <artifactId>bcprov-jdk18on</artifactId>
    <version>1.84</version>
    <optional>true</optional>
</dependency>
```

### Version Compatibility

| Gatling SFTP Plugin | Gatling Core | Scala | Java |
|---------------------|--------------|-------|------|
| 0.0.12-SNAPSHOT     | 3.15.0       | 2.13  | 11+  |

## Testing Infrastructure

### Mock SFTP Server for CI

For CI environments without Docker, you can use the embedded Apache SSHD server:

```scala
// Example test with embedded server
val sftpServer = new SftpTestServer()
sftpServer.start()

try {
  // Run your tests against localhost:sftpServer.port
  val result = runSftpTests("localhost", sftpServer.port)
  assert(result.successful)
} finally {
  sftpServer.stop()
}
```

### Test Configuration

Create a `test.conf` file for test-specific settings:

```hocon
sftp {
  test-server {
    port = 2222
    username = "testuser"
    password = "testpass"
    home-directory = "/tmp/sftp-test"
  }
}
```

## Troubleshooting

### Common Issues

1. **Connection Timeouts**: Increase the connection timeout or check network connectivity
2. **Authentication Failures**: Verify credentials and authentication method
3. **Permission Issues**: Ensure proper file system permissions for local/remote paths
4. **Dependency Conflicts**: Check for conflicting SSHD or Bouncy Castle versions

### Debugging

Enable debug logging by adding to `logback.xml`:
```xml
<logger name="io.github.fherbreteau.gatling.sftp" level="DEBUG"/>
```

### Performance Monitoring

Monitor SFTP performance metrics:
- Connection establishment time
- Authentication time
- File transfer throughput
- Operation latency

## Contributing

Contributions are welcome! Please follow these guidelines:

1. **Fork the repository** and create a feature branch
2. **Write tests** for new functionality
3. **Update documentation** for new features
4. **Submit a pull request** with clear description
5. **Follow code style** and conventions

### Development Setup

```bash
# Build and test
mvn clean install

# Run specific tests
mvn test -Dtest=SftpProtocolSpec

# Generate code coverage
mvn jacoco:report
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
