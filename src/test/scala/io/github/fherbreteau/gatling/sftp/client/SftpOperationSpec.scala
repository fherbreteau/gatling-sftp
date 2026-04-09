package io.github.fherbreteau.gatling.sftp.client

import io.gatling.commons.validation.Success
import io.github.fherbreteau.gatling.sftp.client.SftpActions._
import io.github.fherbreteau.gatling.sftp.model.{Authentications, PasswordAuth}
import io.github.fherbreteau.gatling.sftp.protocol.SftpProtocol
import org.apache.sshd.sftp.client.SftpClient
import org.apache.sshd.sftp.client.SftpClient.OpenMode
import org.mockito.ArgumentMatchers.{any, anyString}
import org.mockito.Mockito.{verify, when}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.BeforeAndAfterAll

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.nio.file.{Files, Path}

class SftpOperationSpec extends AnyFunSpec with Matchers with MockitoSugar with BeforeAndAfterAll {

  var tempDir: Path = _

  override def beforeAll(): Unit = {
    tempDir = Files.createTempDirectory("sftp-operation-test")
  }

  override def afterAll(): Unit = {
    import java.util.Comparator
    Files.walk(tempDir).sorted(Comparator.reverseOrder[Path]()).forEach(Files.deleteIfExists(_))
  }

  private def createProtocol(
      localSourcePath: Option[Path] = None,
      localDestPath: Option[Path] = None,
      remoteSourcePath: Option[String] = None,
      remoteDestPath: Option[String] = None
  ): SftpProtocol = {
    SftpProtocol(
      exchange = Exchange(null, "localhost", 22, Authentications.Password, null),
      credentials = _ => Success(PasswordAuth("user", "pass")),
      localSourcePath = localSourcePath,
      localDestinationPath = localDestPath,
      remoteSourcePath = remoteSourcePath,
      remoteDestinationPath = remoteDestPath
    )
  }

  private def createOperation(
      action: SftpActions.Action,
      source: String = "source",
      destination: String = "dest",
      protocol: SftpProtocol = createProtocol()
  ): SftpOperation = {
    SftpOperation(OperationDef("test-op", source, destination, action), protocol, throttled = false)
  }

  describe("SftpOperation.build") {
    describe("Ls") {
      it("should call readDir on the SFTP client") {
        val client = mock[SftpClient]
        val op = createOperation(Ls, source = "mydir")
        op.build.apply(client)
        verify(client).readDir("/mydir")
      }
    }

    describe("Move") {
      it("should call rename on the SFTP client") {
        val client = mock[SftpClient]
        val op = createOperation(Move, source = "old.txt", destination = "new.txt")
        op.build.apply(client)
        verify(client).rename("/old.txt", "/new.txt")
      }
    }

    describe("Copy") {
      it("should read from source and write to destination") {
        val client = mock[SftpClient]
        val inputStream = new ByteArrayInputStream("test content".getBytes)
        val outputStream = new ByteArrayOutputStream()
        when(client.read("/source.txt")).thenReturn(inputStream)
        when(client.write("/dest.txt", OpenMode.Create, OpenMode.Write)).thenReturn(outputStream)

        val op = createOperation(Copy, source = "source.txt", destination = "dest.txt")
        op.build.apply(client)

        verify(client).read("/source.txt")
        verify(client).write("/dest.txt", OpenMode.Create, OpenMode.Write)
        outputStream.toString shouldBe "test content"
      }
    }

    describe("Delete") {
      it("should call remove on the SFTP client") {
        val client = mock[SftpClient]
        val op = createOperation(Delete, source = "file.txt")
        op.build.apply(client)
        verify(client).remove("/file.txt")
      }
    }

    describe("Upload") {
      it("should upload local file to remote destination") {
        val localFile = tempDir.resolve("upload-test.txt")
        Files.writeString(localFile, "upload content")

        val client = mock[SftpClient]
        val outputStream = new ByteArrayOutputStream()
        when(client.write("/upload-test.txt", OpenMode.Create, OpenMode.Write)).thenReturn(outputStream)

        val protocol = createProtocol(localSourcePath = Some(tempDir))
        val op = createOperation(Upload, source = "upload-test.txt", destination = "upload-test.txt", protocol = protocol)
        op.build.apply(client)

        outputStream.toString shouldBe "upload content"
      }

      it("should throw FileNotFoundException when local file does not exist") {
        val client = mock[SftpClient]
        val protocol = createProtocol(localSourcePath = Some(tempDir))
        val op = createOperation(Upload, source = "nonexistent.txt", destination = "nonexistent.txt", protocol = protocol)

        a[java.io.FileNotFoundException] should be thrownBy op.build.apply(client)
      }
    }

    describe("Download") {
      it("should download remote file to local destination") {
        val client = mock[SftpClient]
        val inputStream = new ByteArrayInputStream("download content".getBytes)
        when(client.read("/remote-file.txt")).thenReturn(inputStream)

        val downloadDir = tempDir.resolve("downloads")
        Files.createDirectories(downloadDir)
        val protocol = createProtocol(localDestPath = Some(downloadDir))
        val op = createOperation(Download, source = "remote-file.txt", destination = "downloaded.txt", protocol = protocol)
        op.build.apply(client)

        Files.readString(downloadDir.resolve("downloaded.txt")) shouldBe "download content"
      }
    }

    describe("Mkdir") {
      it("should call mkdir on the SFTP client") {
        val client = mock[SftpClient]
        val op = createOperation(Mkdir, source = "newdir")
        op.build.apply(client)
        verify(client).mkdir("/newdir")
      }
    }

    describe("RmDir") {
      it("should call rmdir on the SFTP client") {
        val client = mock[SftpClient]
        val op = createOperation(RmDir, source = "olddir")
        op.build.apply(client)
        verify(client).rmdir("/olddir")
      }
    }
  }

  describe("SftpOperationDef") {
    it("should build SftpOperation from session") {
      val opDef = SftpOperationDef(
        operationName = _ => Success("test"),
        operationDef = _ => Success(OperationDef("test", "src", "dst", Ls)),
        sftpProtocol = createProtocol(),
        throttled = false
      )

      val result = opDef.build(null)
      result shouldBe a[Success[_]]
      val sftpOp = result.toOption.get
      sftpOp.operationName shouldBe "test"
      sftpOp.throttled shouldBe false
    }
  }
}
