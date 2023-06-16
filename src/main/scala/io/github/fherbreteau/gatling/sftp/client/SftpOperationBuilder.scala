package io.github.fherbreteau.gatling.sftp.client

import com.typesafe.scalalogging.LazyLogging
import io.gatling.commons.validation.{SuccessWrapper, Validation, safely}
import io.gatling.core.session.{Expression, Session}
import io.github.fherbreteau.gatling.sftp.client.SftpActions.Action
import io.github.fherbreteau.gatling.sftp.client.SftpOperationBuilder.BuildOperationErrorMapper

object SftpOperationBuilder {
  val BuildOperationErrorMapper: String => String = "Failed to build operation: " + _
}

case class SftpOperationBuilder(operationName: Expression[String],
                                source: Expression[String],
                                destination: Expression[String],
                                action: Action) extends LazyLogging {

  private type OperationBuilderConfigure = Session => OperationBuilder => Validation[OperationBuilder]

  private val ConfigureIdentity: OperationBuilderConfigure = _ => _.success

  def build: Expression[OperationDef] =
    session =>
      safely(BuildOperationErrorMapper) {
        for {
          requestName <- operationName(session)
          source <- source(session)
          destination <- destination(session)
          operationBuilder = OperationBuilder(requestName, source, destination, action)
          cb <- configOperationBuilder(session, operationBuilder)
        } yield cb.build
      }

  private def configOperationBuilder(session: Session, operationBuilder: OperationBuilder): Validation[OperationBuilder] = {
    ConfigureIdentity(session)(operationBuilder)
  }
}

case class OperationBuilder(operationName: String, source: String, destination: String, action: SftpActions.Action) {

  def build: OperationDef = OperationDef(operationName, source, destination, action)
}