package io.github.fherbreteau.gatling.sftp.integration

import io.gatling.commons.validation.Success
import io.github.fherbreteau.gatling.sftp.client.SftpActions._
import io.github.fherbreteau.gatling.sftp.client._
import io.github.fherbreteau.gatling.sftp.model.{Authentications, PasswordAuth}
import io.github.fherbreteau.gatling.sftp.protocol.SftpProtocol
import org.apache.sshd.client.SshClient
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory
import org.apache.sshd.server.SshServer
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
import org.apache.sshd.sftp.client.{SftpClient, SftpClientFactory}
import org.apache.sshd.sftp.server.SftpSubsystemFactory
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

import java.nio.file.{Files, Path}
import java.time.Duration
import java.util.{Collections, Comparator}
import scala.util.Using

class SftpIntegrationSpec extends AnyFunSpec with Matchers with BeforeAndAfterAll with BeforeAndAfterEach {

  private var sshServer: SshServer = _
  private var tempDir: Path = _
  private var serverPort: Int = _

  override def beforeAll(): Unit = {
    tempDir = Files.createTempDirectory("sftp-integration-test")

    sshServer = SshServer.setUpDefaultServer()
    sshServer.setPort(0)
    sshServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(tempDir.resolve("hostkey.ser")))
    sshServer.setPasswordAuthenticator((username, password, _) =>
      username == "testuser" && password == "testpass"
    )
    sshServer.setSubsystemFactories(Collections.singletonList(new SftpSubsystemFactory()))
    sshServer.setFileSystemFactory(new VirtualFileSystemFactory(tempDir))
    sshServer.start()
    serverPort = sshServer.getPort
  }

  override def afterAll(): Unit = {
    if (sshServer != null) sshServer.stop(true)
    if (tempDir != null) {
      Files.walk(tempDir)
        .sorted(Comparator.reverseOrder[Path]())
        .forEach(p => Files.deleteIfExists(p))
    }
  }

  override def beforeEach(): Unit = {
    // Clean up temp dir contents (except hostkey) before each test
    Files.list(tempDir).forEach { p =>
      if (!p.getFileName.toString.startsWith("hostkey")) {
        if (Files.isDirectory(p)) {
          Files.walk(p).sorted(Comparator.reverseOrder[Path]()).forEach(Files.deleteIfExists(_))
        } else {
          Files.deleteIfExists(p)
        }
      }
    }
  }

  private def createProtocol(
      localSourcePath: Option[Path] = None,
      localDestPath: Option[Path] = None
  ): SftpProtocol = {
    SftpProtocol(
      exchange = Exchange(null, "localhost", serverPort, Authentications.Password, null),
      credentials = _ => Success(PasswordAuth("testuser", "testpass")),
      localSourcePath = localSourcePath,
      localDestinationPath = localDestPath,
      remoteSourcePath = None,
      remoteDestinationPath = None
    )
  }

  private def withSftpClient[T](f: SftpClient => T): T = {
    val client = SshClient.setUpDefaultClient()
    client.start()
    try {
      Using.Manager { use =>
        val session = use(
          client.connect("testuser", "localhost", serverPort)
            .verify(Duration.ofSeconds(5)).getSession
        )
        session.addPasswordIdentity("testpass")
        session.auth().verify(Duration.ofSeconds(5))
        val sftp = use(SftpClientFactory.instance().createSftpClient(session))
        f(sftp)
      }.get
    } finally {
      client.stop()
    }
  }

  describe("SFTP Operations against embedded server") {
    describe("Mkdir and RmDir") {
      it("should create a directory on the server") {
        withSftpClient { client =>
          val op = SftpOperation(
            OperationDef("mkdir-test", "testdir", "", Mkdir),
            createProtocol(), throttled = false
          )
          op.build.apply(client)
          Files.exists(tempDir.resolve("testdir")) shouldBe true
          Files.isDirectory(tempDir.resolve("testdir")) shouldBe true
        }
      }

      it("should remove a directory from the server") {
        Files.createDirectory(tempDir.resolve("dir-to-remove"))

        withSftpClient { client =>
          val op = SftpOperation(
            OperationDef("rmdir-test", "dir-to-remove", "", RmDir),
            createProtocol(), throttled = false
          )
          op.build.apply(client)
          Files.exists(tempDir.resolve("dir-to-remove")) shouldBe false
        }
      }
    }

    describe("Ls") {
      it("should list directory contents without error") {
        Files.createFile(tempDir.resolve("file1.txt"))
        Files.createFile(tempDir.resolve("file2.txt"))

        withSftpClient { client =>
          val op = SftpOperation(
            OperationDef("ls-test", ".", "", Ls),
            createProtocol(), throttled = false
          )
          noException should be thrownBy op.build.apply(client)
        }
      }
    }

    describe("Upload") {
      it("should upload a local file to the remote server") {
        val uploadContent = "Hello, SFTP upload!"
        Files.writeString(tempDir.resolve("local-upload.txt"), uploadContent)

        withSftpClient { client =>
          val protocol = createProtocol(localSourcePath = Some(tempDir))
          val op = SftpOperation(
            OperationDef("upload-test", "local-upload.txt", "remote-uploaded.txt", Upload),
            protocol, throttled = false
          )
          op.build.apply(client)
          Files.exists(tempDir.resolve("remote-uploaded.txt")) shouldBe true
          Files.readString(tempDir.resolve("remote-uploaded.txt")) shouldBe uploadContent
        }
      }

      it("should throw when uploading a non-existent local file") {
        withSftpClient { client =>
          val protocol = createProtocol(localSourcePath = Some(tempDir))
          val op = SftpOperation(
            OperationDef("upload-fail", "does-not-exist.txt", "remote.txt", Upload),
            protocol, throttled = false
          )
          a[java.io.FileNotFoundException] should be thrownBy op.build.apply(client)
        }
      }
    }

    describe("Download") {
      it("should download a remote file to local destination") {
        val content = "Hello, SFTP download!"
        Files.writeString(tempDir.resolve("remote-file.txt"), content)

        val downloadDir = tempDir.resolve("download-dest")
        Files.createDirectories(downloadDir)

        withSftpClient { client =>
          val protocol = createProtocol(localDestPath = Some(downloadDir))
          val op = SftpOperation(
            OperationDef("download-test", "remote-file.txt", "local-downloaded.txt", Download),
            protocol, throttled = false
          )
          op.build.apply(client)
          Files.readString(downloadDir.resolve("local-downloaded.txt")) shouldBe content
        }
      }
    }

    describe("Copy") {
      it("should copy a file on the remote server") {
        val content = "Copy me!"
        Files.writeString(tempDir.resolve("original.txt"), content)

        withSftpClient { client =>
          val op = SftpOperation(
            OperationDef("copy-test", "original.txt", "copied.txt", Copy),
            createProtocol(), throttled = false
          )
          op.build.apply(client)
          Files.exists(tempDir.resolve("original.txt")) shouldBe true
          Files.exists(tempDir.resolve("copied.txt")) shouldBe true
          Files.readString(tempDir.resolve("copied.txt")) shouldBe content
        }
      }
    }

    describe("Move") {
      it("should rename/move a file on the remote server") {
        Files.writeString(tempDir.resolve("before-move.txt"), "move me")

        withSftpClient { client =>
          val op = SftpOperation(
            OperationDef("move-test", "before-move.txt", "after-move.txt", Move),
            createProtocol(), throttled = false
          )
          op.build.apply(client)
          Files.exists(tempDir.resolve("before-move.txt")) shouldBe false
          Files.exists(tempDir.resolve("after-move.txt")) shouldBe true
          Files.readString(tempDir.resolve("after-move.txt")) shouldBe "move me"
        }
      }
    }

    describe("Delete") {
      it("should delete a file on the remote server") {
        Files.writeString(tempDir.resolve("to-delete.txt"), "delete me")

        withSftpClient { client =>
          val op = SftpOperation(
            OperationDef("delete-test", "to-delete.txt", "", Delete),
            createProtocol(), throttled = false
          )
          op.build.apply(client)
          Files.exists(tempDir.resolve("to-delete.txt")) shouldBe false
        }
      }
    }
  }

  describe("Exchange lifecycle") {
    it("should start and stop SSH client with password auth") {
      val exchange = Exchange("localhost", serverPort, Authentications.Password)
      noException should be thrownBy exchange.start()
      noException should be thrownBy exchange.stop()
    }

    it("should start and stop SSH client with key pair auth") {
      val exchange = Exchange("localhost", serverPort, Authentications.KeyPair)
      noException should be thrownBy exchange.start()
      noException should be thrownBy exchange.stop()
    }

    it("should recover from a closed client by creating a new one") {
      val exchange = Exchange("localhost", serverPort, Authentications.Password)
      exchange.start()
      exchange.stop()
      // After stop, client is closed. Start should create a new client.
      noException should be thrownBy exchange.start()
      exchange.stop()
    }
  }
}
