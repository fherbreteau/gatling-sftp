package io.github.fherbreteau.gatling.sftp.protocol

import io.gatling.commons.validation.Failure
import io.gatling.core.session.Session
import io.github.fherbreteau.gatling.sftp.client.{Exchange, SftpClients}
import io.github.fherbreteau.gatling.sftp.model.Authentications
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SftpComponentsSpec extends AnyFunSpec with Matchers {

  private val protocol = SftpProtocol(
    exchange = Exchange("localhost", 22, Authentications.Password),
    credentials = _ => Failure("not configured"),
    localSourcePath = None,
    localDestinationPath = None,
    remoteSourcePath = None,
    remoteDestinationPath = None
  )

  describe("SftpComponents") {
    it("should not close another virtual user's SSH client when one user exits") {
      val components = SftpComponents(protocol)
      val firstUser = components.onStart(Session("scenario", 1L, null))
      var firstUserExited = false

      try {
        val secondUser = components.onStart(Session("scenario", 2L, null))
        try {
          val firstExchange = SftpClients.sftpClient(firstUser).get
          val secondExchange = SftpClients.sftpClient(secondUser).get

          firstExchange should not be secondExchange
          firstExchange.client should not be secondExchange.client

          components.onExit(firstUser)
          firstUserExited = true

          firstExchange.client.isClosed shouldBe true
          secondExchange.client.isClosed shouldBe false
        } finally {
          components.onExit(secondUser)
        }
      } finally {
        if (!firstUserExited) components.onExit(firstUser)
      }
    }
  }
}
