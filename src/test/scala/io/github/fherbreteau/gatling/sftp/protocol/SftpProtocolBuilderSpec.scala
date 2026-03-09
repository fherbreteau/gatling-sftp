package io.github.fherbreteau.gatling.sftp.protocol

import io.gatling.commons.validation.{Failure, Success}
import io.gatling.core.session.{Expression, Session}
import io.github.fherbreteau.gatling.sftp.client.Exchange
import io.github.fherbreteau.gatling.sftp.model.{Authentications, Credentials, KeyPairAuth, PasswordAuth}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.nio.file.Paths

class SftpProtocolBuilderSpec extends AnyFunSpec with Matchers {

  private def baseProtocol: SftpProtocol = SftpProtocol(
    exchange = Exchange(null, "localhost", 22, Authentications.Password, null),
    credentials = _ => Failure("not configured"),
    localSourcePath = None,
    localDestinationPath = None,
    remoteSourcePath = None,
    remoteDestinationPath = None
  )

  private def baseBuilder: SftpProtocolBuilder = SftpProtocolBuilder(baseProtocol)

  describe("SftpProtocolBuilder") {
    it("should set server") {
      val builder = baseBuilder.server("sftp.example.com")
      builder.protocol.exchange.server shouldBe "sftp.example.com"
    }

    it("should set port") {
      val builder = baseBuilder.port(2222)
      builder.protocol.exchange.port shouldBe 2222
    }

    it("should configure password authentication") {
      val builder = baseBuilder.password(_ => Success("user"), _ => Success("pass"))
      builder.protocol.exchange.authType shouldBe Authentications.Password
      val credExpr: Expression[Credentials] = builder.protocol.credentials
      val creds = credExpr(null).toOption.get
      creds shouldBe a[PasswordAuth]
      creds.username shouldBe "user"
    }

    it("should configure key pair authentication") {
      val builder = baseBuilder.keyPair(_ => Success("user"), _ => Success("/keys/test.key"))
      builder.protocol.exchange.authType shouldBe Authentications.KeyPair
      val credExpr: Expression[Credentials] = builder.protocol.credentials
      val creds = credExpr(null).toOption.get
      creds shouldBe a[KeyPairAuth]
      creds.username shouldBe "user"
    }

    it("should configure key pair authentication with passphrase") {
      val builder = baseBuilder.keyPair(
        _ => Success("user"),
        _ => Success("/keys/test.key.enc"),
        _ => Success("password")
      )
      builder.protocol.exchange.authType shouldBe Authentications.KeyPair
      val credExpr: Expression[Credentials] = builder.protocol.credentials
      val creds = credExpr(null).toOption.get
      creds shouldBe a[KeyPairAuth]
    }

    it("should set local source path") {
      val path = Paths.get("/local/source")
      val builder = baseBuilder.localSourcePath(path)
      builder.protocol.localSourcePath shouldBe Some(path)
    }

    it("should set local destination path") {
      val path = Paths.get("/local/dest")
      val builder = baseBuilder.localDestinationPath(path)
      builder.protocol.localDestinationPath shouldBe Some(path)
    }

    it("should set both local paths with localPath") {
      val path = Paths.get("/local/both")
      val builder = baseBuilder.localPath(path)
      builder.protocol.localSourcePath shouldBe Some(path)
      builder.protocol.localDestinationPath shouldBe Some(path)
    }

    it("should set remote source path") {
      val builder = baseBuilder.remoteSourcePath("/remote/source")
      builder.protocol.remoteSourcePath shouldBe Some("/remote/source")
    }

    it("should set remote destination path") {
      val builder = baseBuilder.remoteDestinationPath("/remote/dest")
      builder.protocol.remoteDestinationPath shouldBe Some("/remote/dest")
    }

    it("should set both remote paths with remotePath") {
      val builder = baseBuilder.remotePath("/remote/both")
      builder.protocol.remoteSourcePath shouldBe Some("/remote/both")
      builder.protocol.remoteDestinationPath shouldBe Some("/remote/both")
    }

    it("should chain multiple settings") {
      val builder = baseBuilder
        .server("example.com")
        .port(3333)
        .remoteSourcePath("/upload")
        .localSourcePath(Paths.get("/local"))
      builder.protocol.exchange.server shouldBe "example.com"
      builder.protocol.exchange.port shouldBe 3333
      builder.protocol.remoteSourcePath shouldBe Some("/upload")
      builder.protocol.localSourcePath shouldBe Some(Paths.get("/local"))
    }

    it("should produce protocol via build") {
      val protocol = baseBuilder.server("host").port(22).build
      protocol shouldBe a[SftpProtocol]
      protocol.exchange.server shouldBe "host"
    }

    it("should implicitly convert to SftpProtocol") {
      val builder = baseBuilder.server("host")
      val protocol: SftpProtocol = SftpProtocolBuilder.toSftpProtocol(builder)
      protocol.exchange.server shouldBe "host"
    }
  }
}
