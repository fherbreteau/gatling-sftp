package io.github.fherbreteau.gatling.sftp.protocol

import io.gatling.commons.validation.{Failure, Success}
import io.gatling.core.session.{Expression, Session}
import io.github.fherbreteau.gatling.sftp.client.Exchange
import io.github.fherbreteau.gatling.sftp.model.{Authentications, Credentials, PasswordAuth}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.nio.file.{Path, Paths}

class SftpProtocolSpec extends AnyFunSpec with Matchers {

  private def createProtocol(
      localSourcePath: Option[Path] = None,
      localDestinationPath: Option[Path] = None,
      remoteSourcePath: Option[String] = None,
      remoteDestinationPath: Option[String] = None
  ): SftpProtocol = {
    SftpProtocol(
      exchange = Exchange(
        client = null,
        server = "localhost",
        port = 22,
        authType = Authentications.Password,
        executor = null
      ),
      credentials = _ => Success(PasswordAuth("user", "pass")),
      localSourcePath = localSourcePath,
      localDestinationPath = localDestinationPath,
      remoteSourcePath = remoteSourcePath,
      remoteDestinationPath = remoteDestinationPath
    )
  }

  describe("SftpProtocol") {
    describe("localSource") {
      it("should resolve file against base source path") {
        val protocol = createProtocol(localSourcePath = Some(Paths.get("/base/source")))
        protocol.localSource("file.txt") shouldBe Paths.get("/base/source/file.txt")
      }

      it("should resolve file against current dir when no base path") {
        val protocol = createProtocol()
        protocol.localSource("file.txt") shouldBe Paths.get("./file.txt")
      }
    }

    describe("localDestination") {
      it("should resolve file against base destination path") {
        val protocol = createProtocol(localDestinationPath = Some(Paths.get("/base/dest")))
        protocol.localDestination("file.txt") shouldBe Paths.get("/base/dest/file.txt")
      }

      it("should resolve file against current dir when no base path") {
        val protocol = createProtocol()
        protocol.localDestination("file.txt") shouldBe Paths.get("./file.txt")
      }
    }

    describe("remoteSource") {
      it("should resolve file against remote source path") {
        val protocol = createProtocol(remoteSourcePath = Some("/remote/src"))
        protocol.remoteSource("file.txt") shouldBe "/remote/src/file.txt"
      }

      it("should resolve file with leading slash when no base path") {
        val protocol = createProtocol()
        protocol.remoteSource("file.txt") shouldBe "/file.txt"
      }
    }

    describe("remoteDestination") {
      it("should resolve file against remote destination path") {
        val protocol = createProtocol(remoteDestinationPath = Some("/remote/dst"))
        protocol.remoteDestination("file.txt") shouldBe "/remote/dst/file.txt"
      }

      it("should resolve file with leading slash when no base path") {
        val protocol = createProtocol()
        protocol.remoteDestination("file.txt") shouldBe "/file.txt"
      }
    }

    describe("credentials") {
      it("should evaluate credentials expression") {
        val protocol = createProtocol()
        val credExpr: Expression[Credentials] = protocol.credentials
        val creds = credExpr(null).toOption.get
        creds shouldBe a[PasswordAuth]
        creds.username shouldBe "user"
      }

      it("should throw when credentials expression fails") {
        val protocol = SftpProtocol(
          exchange = Exchange(null, "localhost", 22, Authentications.Password, null),
          credentials = _ => Failure("unauthenticated"),
          localSourcePath = None,
          localDestinationPath = None,
          remoteSourcePath = None,
          remoteDestinationPath = None
        )
        val credExpr: Expression[Credentials] = protocol.credentials
        credExpr(null) shouldBe a[Failure]
      }
    }
  }
}
