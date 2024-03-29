package io.github.fherbreteau.gatling.sftp.action

import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.protocol.ProtocolComponentsRegistry
import io.gatling.core.session.Expression
import io.gatling.core.structure.ScenarioContext
import io.github.fherbreteau.gatling.sftp.client.SftpActions.{Action => SftpClientAction}
import io.github.fherbreteau.gatling.sftp.client.{SftpOperationBuilder, SftpOperationDef}
import io.github.fherbreteau.gatling.sftp.protocol.{SftpComponents, SftpProtocol}

case class SftpActionBuilder(operationName: Expression[String],
                             source: Expression[String],
                             destination: Expression[String],
                             action: SftpClientAction) extends ActionBuilder {
  override def build(ctx: ScenarioContext, next: Action): Action = {
    val sftpComponents = lookUpSftpComponents(ctx.protocolComponentsRegistry)
    val sftpOperationDef = build(sftpComponents.sftpProtocol, ctx.throttled)
    new SftpAction(sftpOperationDef, ctx.coreComponents, next)
  }

  private def build(sshProtocol: SftpProtocol, throttled: Boolean): SftpOperationDef = {
    val resolvedOperationExpression = new SftpOperationBuilder(operationName, source, destination, action).build
    SftpOperationDef(
      operationName,
      resolvedOperationExpression,
      sshProtocol,
      throttled
    )
  }

  private def lookUpSftpComponents(protocolComponentsRegistry: ProtocolComponentsRegistry): SftpComponents =
    protocolComponentsRegistry.components(SftpProtocol.SftpProtocolKey)
}

