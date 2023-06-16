package io.github.fherbreteau.gatling.sftp

import io.gatling.core.session.{EmptyStringExpressionSuccess, Expression}
import io.github.fherbreteau.gatling.sftp.action.SftpActionBuilder
import io.github.fherbreteau.gatling.sftp.client.SftpActions._

case class Sftp(operationName: Expression[String]) {

  def mkdir(directory: Expression[String]): SftpActionBuilder = action(directory, EmptyStringExpressionSuccess, Mkdir)

  def move(source: Expression[String], destination: Expression[String]): SftpActionBuilder = action(source, destination, Move)

  def copy(source: Expression[String], destination: Expression[String]): SftpActionBuilder = action(source, destination, Copy)

  def download(file: Expression[String]): SftpActionBuilder = download(file, file)

  def download(source: Expression[String], destination: Expression[String]): SftpActionBuilder = action(source, destination, Download)

  def upload(file: Expression[String]): SftpActionBuilder = upload(file, file)

  def upload(source: Expression[String], destination: Expression[String]): SftpActionBuilder = action(source, destination, Upload)

  def delete(file: Expression[String]): SftpActionBuilder = action(file, EmptyStringExpressionSuccess, Delete)

  def rmdir(directory: Expression[String]): SftpActionBuilder = action(directory, EmptyStringExpressionSuccess, RmDir)

  private def action(source: Expression[String], destination: Expression[String], action: Action): SftpActionBuilder =
    SftpActionBuilder(operationName, source, destination, action)

}
