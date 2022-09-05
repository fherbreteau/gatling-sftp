package com.github.fherbreteau.gatling.sftp.client.result

import com.github.fherbreteau.gatling.sftp.client.OperationDef
import io.gatling.commons.stats.Status

trait SftpResult {
  def operation: OperationDef

  def startTimestamp: Long

  def endTimestamp: Long

  def status: Status

  def message: Option[String]
}

final case class SftpFailure(
                              operation: OperationDef,
                              startTimestamp: Long,
                              endTimestamp: Long,
                              errorMessage: String,
                              status: Status
                            ) extends SftpResult {
  override def message: Option[String] = Some(errorMessage)
}

final case class SftpResponse(
                               operation: OperationDef,
                               startTimestamp: Long,
                               endTimestamp: Long,
                               status: Status
                             ) extends SftpResult {
  override def message: Option[String] = None
}
