package io.github.fherbreteau.gatling.sftp

import io.gatling.core.session.Expression
import io.github.fherbreteau.gatling.sftp.action.SftpActionBuilder
import io.github.fherbreteau.gatling.sftp.client.SftpActions._

case class Sftp(operationName: Expression[String]) {

  def move(file: Expression[String]): SftpActionBuilder = action(file, Move)

  def copy(file: Expression[String]): SftpActionBuilder = action(file, Copy)

  def delete(file: Expression[String]): SftpActionBuilder = action(file, Delete)

  def download(file: Expression[String]): SftpActionBuilder = action(file, Download)

  def upload(file: Expression[String]): SftpActionBuilder = action(file, Upload)

  private def action(file: Expression[String], action: Action): SftpActionBuilder =
    SftpActionBuilder(operationName, file, action)

}
