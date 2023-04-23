package io.github.fherbreteau.gatling.sftp.client

import com.typesafe.scalalogging.StrictLogging
import io.gatling.core.action.Action
import io.gatling.core.session.Session

case class SftpTransaction(session: Session,
                           sftpOperation: SftpOperation,
                           next: Action) extends StrictLogging {

  def fullRequestName: String = sftpOperation.operationName

  def server: String = sftpOperation.sftpProtocol.exchange.server

  def scenario: String = session.scenario

  def userId: Long = session.userId

  def throttled: Boolean = sftpOperation.throttled

  def action: OperationDef = sftpOperation.definition
}

