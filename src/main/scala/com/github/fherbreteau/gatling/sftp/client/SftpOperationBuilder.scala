package com.github.fherbreteau.gatling.sftp.client

import SftpActions.Action
import SftpOperationBuilder.BuildOperationErrorMapper
import com.typesafe.scalalogging.LazyLogging
import io.gatling.commons.validation.{SuccessWrapper, Validation, safely}
import io.gatling.core.session.{Expression, Session}

object SftpOperationBuilder  {
  val BuildOperationErrorMapper: String => String = "Failed to build operation: " + _
}

case class SftpOperationBuilder(operationName: Expression[String],
                                file: Expression[String],
                                action: Action) extends LazyLogging {

  type OperationBuilderConfigure = Session => OperationBuilder => Validation[OperationBuilder]

  val ConfigureIdentity: OperationBuilderConfigure = _ => _.success

  def build: Expression[OperationDef] =
    session =>
      safely(BuildOperationErrorMapper) {
        for {
          requestName <- operationName(session)
          file <- file(session)
          operationBuilder = OperationBuilder(requestName, file, action)
          cb <- configOperationBuilder(session, operationBuilder)
        } yield cb.build
      }

  def configOperationBuilder(session: Session, operationBuilder: OperationBuilder): Validation[OperationBuilder] = {
    ConfigureIdentity(session)(operationBuilder)
  }
}

case class OperationBuilder(operationName: String, file: String, action: SftpActions.Action) {

  def build: OperationDef = OperationDef(operationName, file, action)
}