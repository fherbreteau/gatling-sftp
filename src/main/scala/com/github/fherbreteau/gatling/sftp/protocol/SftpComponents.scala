package com.github.fherbreteau.gatling.sftp.protocol

import com.github.fherbreteau.gatling.sftp.client.SftpClients
import io.gatling.core.protocol.ProtocolComponents
import io.gatling.core.session.Session

case class SftpComponents(sftpProtocol: SftpProtocol) extends ProtocolComponents {

  override lazy val onStart: Session => Session = SftpClients.setSshClient(sftpProtocol)

  override def onExit: Session => Unit = session => {
    SftpClients.sftpClient(session).foreach(_.stop())
  }
}
