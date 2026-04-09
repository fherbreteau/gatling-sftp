package io.github.fherbreteau.gatling.sftp

import io.gatling.commons.validation.Success
import io.gatling.core.session.Expression
import io.github.fherbreteau.gatling.sftp.client.SftpActions
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SftpDslSpec extends AnyFunSpec with Matchers {

  private val operationName: Expression[String] = _ => Success("test-op")
  private val sftpDsl = Sftp(operationName)
  private def constExpr(value: String): Expression[String] = _ => Success(value)

  describe("Sftp DSL") {
    it("ls should create action builder with Ls action") {
      val builder = sftpDsl.ls(constExpr("dir"))
      builder.action shouldBe SftpActions.Ls
    }

    it("mkdir should create action builder with Mkdir action") {
      val builder = sftpDsl.mkdir(constExpr("dir"))
      builder.action shouldBe SftpActions.Mkdir
    }

    it("move should create action builder with Move action") {
      val builder = sftpDsl.move(constExpr("src"), constExpr("dst"))
      builder.action shouldBe SftpActions.Move
    }

    it("copy should create action builder with Copy action") {
      val builder = sftpDsl.copy(constExpr("src"), constExpr("dst"))
      builder.action shouldBe SftpActions.Copy
    }

    it("upload with single arg should create action builder with Upload action") {
      val builder = sftpDsl.upload(constExpr("file"))
      builder.action shouldBe SftpActions.Upload
    }

    it("upload with two args should create action builder with Upload action") {
      val builder = sftpDsl.upload(constExpr("src"), constExpr("dst"))
      builder.action shouldBe SftpActions.Upload
    }

    it("download with single arg should create action builder with Download action") {
      val builder = sftpDsl.download(constExpr("file"))
      builder.action shouldBe SftpActions.Download
    }

    it("download with two args should create action builder with Download action") {
      val builder = sftpDsl.download(constExpr("src"), constExpr("dst"))
      builder.action shouldBe SftpActions.Download
    }

    it("delete should create action builder with Delete action") {
      val builder = sftpDsl.delete(constExpr("file"))
      builder.action shouldBe SftpActions.Delete
    }

    it("rmdir should create action builder with RmDir action") {
      val builder = sftpDsl.rmdir(constExpr("dir"))
      builder.action shouldBe SftpActions.RmDir
    }
  }

  describe("SftpActionBuilder properties") {
    it("should carry the operation name expression") {
      val builder = sftpDsl.ls(constExpr("dir"))
      builder.operationName.apply(null) shouldBe Success("test-op")
    }

    it("should carry the source expression") {
      val builder = sftpDsl.move(constExpr("src-file"), constExpr("dst-file"))
      builder.source.apply(null) shouldBe Success("src-file")
    }

    it("should carry the destination expression") {
      val builder = sftpDsl.move(constExpr("src-file"), constExpr("dst-file"))
      builder.destination.apply(null) shouldBe Success("dst-file")
    }
  }
}
