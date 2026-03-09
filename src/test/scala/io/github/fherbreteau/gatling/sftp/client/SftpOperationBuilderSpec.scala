package io.github.fherbreteau.gatling.sftp.client

import io.gatling.commons.validation.{Failure, Success}
import io.gatling.core.session.Expression
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SftpOperationBuilderSpec extends AnyFunSpec with Matchers {

  private def constExpr(value: String): Expression[String] = _ => Success(value)
  private def failExpr(msg: String): Expression[String] = _ => Failure(msg)

  describe("SftpOperationBuilder") {
    it("should build OperationDef from constant expressions") {
      val builder = SftpOperationBuilder(
        operationName = constExpr("test-op"),
        source = constExpr("source.txt"),
        destination = constExpr("dest.txt"),
        action = SftpActions.Upload
      )

      val result = builder.build.apply(null)
      result shouldBe a[Success[_]]
      val opDef = result.toOption.get
      opDef.operationName shouldBe "test-op"
      opDef.source shouldBe "source.txt"
      opDef.destination shouldBe "dest.txt"
      opDef.action shouldBe SftpActions.Upload
    }

    it("should propagate operation name expression failure") {
      val builder = SftpOperationBuilder(
        operationName = failExpr("name error"),
        source = constExpr("source.txt"),
        destination = constExpr("dest.txt"),
        action = SftpActions.Ls
      )

      val result = builder.build.apply(null)
      result shouldBe a[Failure]
    }

    it("should propagate source expression failure") {
      val builder = SftpOperationBuilder(
        operationName = constExpr("op"),
        source = failExpr("source error"),
        destination = constExpr("dest.txt"),
        action = SftpActions.Ls
      )

      val result = builder.build.apply(null)
      result shouldBe a[Failure]
    }

    it("should propagate destination expression failure") {
      val builder = SftpOperationBuilder(
        operationName = constExpr("op"),
        source = constExpr("source.txt"),
        destination = failExpr("dest error"),
        action = SftpActions.Ls
      )

      val result = builder.build.apply(null)
      result shouldBe a[Failure]
    }

    it("should build for each action type") {
      SftpActions.values.foreach { action =>
        val builder = SftpOperationBuilder(
          operationName = constExpr("op"),
          source = constExpr("src"),
          destination = constExpr("dst"),
          action = action
        )
        val result = builder.build.apply(null)
        result shouldBe a[Success[_]]
        result.toOption.get.action shouldBe action
      }
    }
  }

  describe("OperationBuilder") {
    it("should build OperationDef with correct values") {
      val builder = OperationBuilder("op-name", "src", "dst", SftpActions.Move)
      val opDef = builder.build
      opDef.operationName shouldBe "op-name"
      opDef.source shouldBe "src"
      opDef.destination shouldBe "dst"
      opDef.action shouldBe SftpActions.Move
    }
  }
}
