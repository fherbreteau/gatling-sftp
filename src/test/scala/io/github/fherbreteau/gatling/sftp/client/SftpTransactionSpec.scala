package io.github.fherbreteau.gatling.sftp.client

import io.gatling.commons.validation.Success
import io.github.fherbreteau.gatling.sftp.client.SftpActions._
import io.github.fherbreteau.gatling.sftp.model.{Authentications, PasswordAuth}
import io.github.fherbreteau.gatling.sftp.protocol.SftpProtocol
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar

class SftpTransactionSpec extends AnyFunSpec with Matchers with MockitoSugar {

  private val protocol = SftpProtocol(
    exchange = Exchange(null, "sftp.example.com", 2222, Authentications.Password, null),
    credentials = _ => Success(PasswordAuth("user", "pass")),
    localSourcePath = None,
    localDestinationPath = None,
    remoteSourcePath = None,
    remoteDestinationPath = None
  )

  private val operation = SftpOperation(
    OperationDef("list-files", "dir", "", Ls),
    protocol,
    throttled = true
  )

  describe("SftpTransaction") {
    it("should expose fullRequestName from operation") {
      val tx = SftpTransaction(null, operation, null)
      tx.fullRequestName shouldBe "list-files"
    }

    it("should expose server from protocol exchange") {
      val tx = SftpTransaction(null, operation, null)
      tx.server shouldBe "sftp.example.com"
    }

    it("should expose throttled flag from operation") {
      val tx = SftpTransaction(null, operation, null)
      tx.throttled shouldBe true
    }

    it("should expose action definition") {
      val tx = SftpTransaction(null, operation, null)
      tx.action.operationName shouldBe "list-files"
      tx.action.action shouldBe Ls
    }
  }
}
