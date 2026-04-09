package io.github.fherbreteau.gatling.sftp.model

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.security.{KeyPair, KeyPairGenerator}

class CredentialsSpec extends AnyFunSpec with Matchers {

  describe("PasswordAuth") {
    it("should store username and password") {
      val auth = PasswordAuth("user", "pass")
      auth.username shouldBe "user"
      auth.password shouldBe "pass"
    }

    it("should return password as credential") {
      val auth = PasswordAuth("user", "secret")
      auth.credential shouldBe "secret"
    }

    it("should support case class equality") {
      PasswordAuth("a", "b") shouldBe PasswordAuth("a", "b")
      PasswordAuth("a", "b") should not be PasswordAuth("a", "c")
    }
  }

  describe("KeyPairAuth") {
    val keyPair: KeyPair = {
      val gen = KeyPairGenerator.getInstance("RSA")
      gen.initialize(2048)
      gen.generateKeyPair()
    }

    it("should store username and keyPair") {
      val auth = KeyPairAuth("user", keyPair)
      auth.username shouldBe "user"
      auth.keyPair shouldBe keyPair
    }

    it("should return keyPair as credential") {
      val auth = KeyPairAuth("user", keyPair)
      auth.credential shouldBe keyPair
    }
  }
}
