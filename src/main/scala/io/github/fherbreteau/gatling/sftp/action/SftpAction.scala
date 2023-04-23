package io.github.fherbreteau.gatling.sftp.action

import io.gatling.commons.util.Clock
import io.gatling.commons.validation.Validation
import io.gatling.core.CoreComponents
import io.gatling.core.action.{Action, RequestAction}
import io.gatling.core.session.{Expression, Session}
import io.gatling.core.stats.StatsEngine
import io.gatling.core.util.NameGen
import io.github.fherbreteau.gatling.sftp.client.{SftpOperationDef, SftpTransaction}

class SftpAction(sftpOperationDef: SftpOperationDef,
                 coreComponents: CoreComponents,
                 val next: Action) extends RequestAction with NameGen {

  override val name: String = genName("sftpOperation")

  override def clock: Clock = coreComponents.clock

  override def requestName: Expression[String] = sftpOperationDef.operationName

  override def statsEngine: StatsEngine = coreComponents.statsEngine

  override def sendRequest(session: Session): Validation[Unit] =
    sftpOperationDef.build(session).map { sftpOperation =>
      val transaction = SftpTransaction(
        session,
        sftpOperation,
        next)
      sftpOperation.sftpProtocol.exchange.execute(transaction, coreComponents)
    }

}