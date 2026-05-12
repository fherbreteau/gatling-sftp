# Code Review: gatling-sftp

**Date:** 2026-03-09
**Version:** 0.0.12-SNAPSHOT
**Source LOC:** 1,101 | **Test LOC:** 1,352 | **Test suites:** 10 | **Tests:** 82

---

## Summary Scorecard

| Category        | Score    | Notes                                                        |
|-----------------|:--------:|--------------------------------------------------------------|
| Lint Compliance | 8/10     | Unused imports                                               |
| Code Quality    | 6/10     | Mutable `var` in case class, unsafe `.get()` calls           |
| Security        | 5/10     | No host key verification, no path sanitization               |
| Maintainability | 8/10     | Clean architecture, good separation, builder pattern         |
| Documentation   | 8/10     | Javadoc and readme now up to date                            |
| Idempotency     | 7/10     | Executor leak on repeated start/stop, mutable client field   |
| **Overall**     | **6/10** | Solid architecture held back by safety and resource issues   |

---

## Repository Structure

```text
gatling-sftp/
├── .github/
│   ├── workflows/
│   │   ├── ci.yml                  # Build + SonarCloud (JDK 17)
│   │   ├── test.yml                # Unit tests (JDK 17)
│   │   ├── release.yml             # Maven Central release via GPG
│   │   ├── dependency-review.yml   # PR dependency scanning
│   │   └── release-notes.yml       # Auto-generated release notes
│   ├── dependabot.yml              # Weekly Maven dependency updates
│   └── release.yml                 # Release drafter config
├── src/main/
│   ├── scala/...gatling/sftp/
│   │   ├── Predef.scala            # Import entrypoint
│   │   ├── Sftp.scala              # Scala DSL (ls, mkdir, upload, etc.)
│   │   ├── SftpDsl.scala           # Protocol + action factory trait
│   │   ├── action/                 # Gatling ActionBuilder / RequestAction
│   │   ├── client/                 # Exchange, SftpOperation, Transaction
│   │   ├── model/                  # Credentials, Authentications
│   │   ├── protocol/               # SftpProtocol, Builder, Components
│   │   └── util/                   # SftpHelper (key loading)
│   └── java/...gatling/sftp/javaapi/
│       ├── Sftp.java               # Java DSL wrapper
│       ├── SftpDsl.java            # Java entrypoint
│       ├── action/                 # Java ActionBuilder wrapper
│       └── protocol/               # Java ProtocolBuilder wrapper
├── src/test/
│   ├── scala/.../
│   │   ├── model/CredentialsSpec.scala
│   │   ├── client/SftpOperationBuilderSpec.scala
│   │   ├── client/SftpOperationSpec.scala
│   │   ├── client/SftpTransactionSpec.scala
│   │   ├── client/result/SftpResultSpec.scala
│   │   ├── protocol/SftpProtocolSpec.scala
│   │   ├── protocol/SftpProtocolBuilderSpec.scala
│   │   ├── util/SftpHelperSpec.scala
│   │   ├── SftpDslSpec.scala
│   │   ├── integration/SftpIntegrationSpec.scala
│   │   └── examples/               # Gatling simulation examples (3 files)
│   ├── java/.../examples/          # Java simulation examples (3 files)
│   └── resources/                  # Test keys, credentials, configs
├── docker-compose.yml              # OpenSSH test server
├── javadoc/readme.md               # Javadoc overview
├── pom.xml
├── CODE_REVIEW.md
├── LICENSE (MIT)
└── README.md
```

---

## Outstanding Tasks

### Critical

- [ ] **Exchange: mutable `var client` in case class** -- `Exchange.scala:38` declares `var client: SshClient` inside a `final case class`. This breaks immutability guarantees and introduces a race condition when multiple threads call `start()`. Replace with `AtomicReference[SshClient]` or refactor Exchange to a regular class.

- [ ] **Executor thread pool never shut down** -- `Exchange.scala:34` creates `Executors.newSingleThreadExecutor()` but `stop()` on line 59 only calls `client.stop()`. The `ExecutorService` leaks a thread per Exchange instance. Change the field type from `Executor` to `ExecutorService` and call `executor.shutdown()` in `stop()`.

### High

- [ ] **No host key verification** -- `Exchange.scala:30,46` uses `SshClient.setUpDefaultClient()` without configuring a `ServerKeyVerifier`. This accepts any server key, enabling MITM attacks. Add a configurable `ServerKeyVerifier` to the protocol builder (e.g., `AcceptAllServerKeyVerifier.INSTANCE` for tests, `KnownHostsServerKeyVerifier` for production) and document the security implications.

- [ ] **Unsafe `.get` on key loading** -- `SftpHelper.scala:39-41,45-47` calls `getClass.getResourceAsStream(path)` without a null check, then calls `.iterator().next()` without checking `hasNext`, then `.get` on the `Using.Manager` result. A missing or empty key file produces an opaque `NullPointerException` or `NoSuchElementException`. Add null check and meaningful error messages.

- [ ] **Unsafe `.get` on credentials** -- `SftpProtocol.scala:68` calls `.toOption.get` which throws `NoSuchElementException` if the credentials expression fails. Consider returning `Validation[Credentials]` or throwing a domain-specific exception.

### Medium

- [ ] **Path concatenation without sanitization** -- `SftpProtocol.scala:61,65` builds remote paths via `"".concat("/").concat(file)`. No validation against `../` traversal sequences or double slashes. Add path normalization or use a path utility.

- [ ] **Unused imports** -- `Exchange.scala:17` imports `{SshConstants, SshException}`, neither of which is directly referenced. Remove both or keep only if needed transitively.

- [ ] **Unsafe type cast** -- `SftpClients.scala:16` uses `.asInstanceOf[Exchange]` without a type check. Use pattern matching instead: `session.attributes.get(exchange).collect { case e: Exchange => e }`.

- [x] **Javadoc copy-paste errors** -- ~~`SftpProtocolBuilder.java` lines 28, 36, 45, 55, 66, 78, 86, 96, 104, 113 all say `@return a new HttpProtocolBuilder instance` instead of `SftpProtocolBuilder`.~~ Replaced all occurrences with `SftpProtocolBuilder`.

### Low

- [x] **Stale javadoc readme** -- ~~`javadoc/readme.md` references Gatling 3.9.x and Scala 2.12, contains a bare `TODO`. Update or remove.~~ Updated to Gatling 3.15.x, Scala 2.13, Maven dependency example, and linked to examples.

- [ ] **Docker image not pinned** -- `docker-compose.yml:4` uses `openssh-server:latest`. Pin to a specific version for reproducible test environments.

- [ ] **Verbose test logging** -- `logback-test.xml` sets `org.apache.sshd` to TRACE. Use DEBUG or INFO to reduce noise.

- [x] **Incomplete .gitignore** -- ~~Missing entries for `.DS_Store`, `*.log`, `.env`. Consider adding them.~~ Added `.DS_Store`, `Thumbs.db`, `.env`, `.env.*`, and `*.log` entries.

---

## Security Review

| Area                 | Status | Detail                                                                                      |
|----------------------|--------|---------------------------------------------------------------------------------------------|
| Host key verification| FAIL   | `SshClient.setUpDefaultClient()` accepts any server key. No `ServerKeyVerifier` configured.  |
| Credential handling  | PASS   | Passwords are not logged. Key pair loading uses Apache SSHD security utilities.               |
| Path traversal       | WARN   | Remote path built via string concatenation; no `../` sanitization.                           |
| Injection risk       | PASS   | No shell commands, SQL, or user-controlled eval. All operations use typed SFTP client API.   |
| Test credentials     | PASS   | CSV files contain dummy `user/password`. Test keys are purpose-generated ED25519 keys.       |
| CI/CD secrets        | PASS   | GPG, OSSRH, and SonarCloud tokens use GitHub repository secrets. App token used for releases.|
| Dependency scanning  | PASS   | Dependabot (weekly) + `dependency-review-action` on PRs.                                     |
| Docker security      | WARN   | Hardcoded password in `docker-compose.yml` (acceptable for local test container). Unpinned image tag. |
| Resource cleanup     | FAIL   | `ExecutorService` is never shut down. Leaked threads accumulate across Exchange instances.    |
| Thread safety        | FAIL   | Mutable `var` field in case class creates race conditions in concurrent scenarios.            |

---

## Dependency Matrix

| Dependency            | Artifact                      | Version  | Scope            | Status  | Notes                             |
|-----------------------|-------------------------------|----------|------------------|---------|-----------------------------------|
| Gatling Core          | `gatling-core-java`           | 3.15.0   | compile          | Current | Provides Scala 2.13 transitively  |
| Gatling Charts        | `gatling-charts-highcharts`   | 3.15.0   | test             | Current | Simulation runner + reports       |
| Apache SSHD Core      | `sshd-core`                   | 2.17.1   | compile          | Current | SSH client + server classes       |
| Apache SSHD SFTP      | `sshd-sftp`                   | 2.17.1   | compile          | Current | SFTP client/server subsystem      |
| Bouncy Castle         | `bcprov-jdk18on`              | 1.83     | compile (optional)| Current | Ed25519 key support               |
| Jakarta Annotations   | `jakarta.annotation-api`      | 3.0.0    | compile          | Current | `@Nonnull` for Java API           |
| ScalaTest             | `scalatest_2.13`              | 3.2.18   | test             | Current | Unit + integration test framework |
| ScalaTestPlus Mockito | `mockito-5-10_2.13`           | 3.2.18.0 | test             | Current | Mock framework for unit tests     |

### Build Plugins

| Plugin                    | Version | Phase                                    | Notes                                                        |
|---------------------------|---------|------------------------------------------|--------------------------------------------------------------|
| `scala-maven-plugin`      | 4.9.9   | process-sources / process-test-resources | Compiles Scala + Java sources                                |
| `maven-compiler-plugin`   | 3.15.0  | *(disabled)*                             | Default phases disabled; Scala plugin handles compilation    |
| `gatling-maven-plugin`    | 4.21.0  | manual                                   | Runs Gatling simulations via `mvn gatling:test`              |
| `scalatest-maven-plugin`  | 2.2.0   | test                                     | Runs ScalaTest specs via `mvn -B test`                       |

### Transitive Dependencies (notable)

| Dependency       | Version      | Via                     | Notes                                                      |
|------------------|--------------|-------------------------|------------------------------------------------------------|
| Scala Library    | 2.13.18      | `gatling-core-java`     | Runtime language support                                   |
| Scala Reflect    | 2.13.10      | `scalatest_2.13`        | Test-only; version mismatch with Scala Library (2.13.18)   |
| SLF4J API        | 1.7.36       | `sshd-core`             | Logging facade; Logback 1.5.32 binds via Gatling           |
| Logback Classic  | 1.5.32       | `gatling-commons`       | Logging implementation                                     |
| Netty            | 4.2.10.Final | `gatling-core`          | Network I/O (multiple modules)                             |
| Jackson Databind | 2.21.1       | `gatling-core`          | JSON processing                                            |
| Apache SSHD Common| 2.17.1      | `sshd-core`             | Shared SSH utilities                                       |
| Caffeine         | 3.2.3        | `gatling-core`          | In-memory caching                                          |
| Typesafe Config  | 1.4.5        | `gatling-commons`       | Configuration library                                      |
| Saxon-HE         | 12.9         | `gatling-core`          | XPath/XSLT processing                                     |

---

## Test Verification

| Suite                                        | Type               | Framework                            | Tests              | Status       |
|----------------------------------------------|--------------------|--------------------------------------|--------------------|--------------|
| Unit tests (`mvn -B test`)                   | Unit + Integration | ScalaTest + Mockito + Embedded SSHD  | 82                 | PASS         |
| Gatling simulations (`mvn -B gatling:test`)  | E2E (Docker)       | Gatling 3.15.0                       | 6 sims / 54 requests | PASS (0 KO)  |
