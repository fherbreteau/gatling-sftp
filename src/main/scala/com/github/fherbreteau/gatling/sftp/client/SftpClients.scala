package com.github.fherbreteau.gatling.sftp.client

import com.github.fherbreteau.gatling.sftp.protocol.SftpProtocol
import io.gatling.core.Predef.Session
import io.gatling.core.session.SessionPrivateAttributes

object SftpClients {
  private val exchange: String = SessionPrivateAttributes.PrivateAttributePrefix + "exchange"

  def setSshClient(sftpProtocol: SftpProtocol): Session => Session =
    session => {
      sftpProtocol.exchange.start()
      session.set(exchange, sftpProtocol.exchange)
    }

  def sftpClient(session: Session): Option[Exchange] =
    session.attributes.get(exchange).map(_.asInstanceOf[Exchange])

}
