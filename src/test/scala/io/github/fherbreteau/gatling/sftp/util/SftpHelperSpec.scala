package io.github.fherbreteau.gatling.sftp.util

import io.gatling.commons.validation.Success
import io.github.fherbreteau.gatling.sftp.model.{KeyPairAuth, PasswordAuth}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SftpHelperSpec extends AnyFunSpec with Matchers {

  describe("SftpHelper") {
    describe("buildPasswordAuth") {
      it("should create PasswordAuth credentials from expressions") {
        val expr = SftpHelper.buildPasswordAuth(
          _ => Success("testuser"),
          _ => Success("testpass")
        )
        val result = expr.apply(null)
        result shouldBe a[Success[_]]
        val creds = result.toOption.get
        creds shouldBe a[PasswordAuth]
        creds.username shouldBe "testuser"
        creds.asInstanceOf[PasswordAuth].password shouldBe "testpass"
      }

      it("should propagate username expression failure") {
        val expr = SftpHelper.buildPasswordAuth(
          _ => io.gatling.commons.validation.Failure("user error"),
          _ => Success("pass")
        )
        val result = expr.apply(null)
        result shouldBe a[io.gatling.commons.validation.Failure]
      }

      it("should propagate password expression failure") {
        val expr = SftpHelper.buildPasswordAuth(
          _ => Success("user"),
          _ => io.gatling.commons.validation.Failure("pass error")
        )
        val result = expr.apply(null)
        result shouldBe a[io.gatling.commons.validation.Failure]
      }
    }

    describe("buildKeyPairAuth") {
      it("should load unprotected key pair from classpath") {
        val expr = SftpHelper.buildKeyPairAuth(
          _ => Success("testuser"),
          _ => Success("/keys/test.key")
        )
        val result = expr.apply(null)
        result shouldBe a[Success[_]]
        val creds = result.toOption.get
        creds shouldBe a[KeyPairAuth]
        creds.username shouldBe "testuser"
        creds.asInstanceOf[KeyPairAuth].keyPair should not be null
      }

      it("should load password-protected key pair from classpath") {
        val expr = SftpHelper.buildKeyPairAuth(
          _ => Success("testuser"),
          _ => Success("/keys/test.key.enc"),
          _ => Success("password")
        )
        val result = expr.apply(null)
        result shouldBe a[Success[_]]
        val creds = result.toOption.get
        creds shouldBe a[KeyPairAuth]
        creds.username shouldBe "testuser"
      }

      it("should propagate username expression failure") {
        val expr = SftpHelper.buildKeyPairAuth(
          _ => io.gatling.commons.validation.Failure("user error"),
          _ => Success("/keys/test.key")
        )
        val result = expr.apply(null)
        result shouldBe a[io.gatling.commons.validation.Failure]
      }
    }
  }
}
