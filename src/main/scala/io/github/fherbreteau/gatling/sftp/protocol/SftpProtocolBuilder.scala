package io.github.fherbreteau.gatling.sftp.protocol

import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.session.Expression
import io.gatling.internal.quicklens._
import io.github.fherbreteau.gatling.sftp.model.Authentications
import io.github.fherbreteau.gatling.sftp.util.SftpHelper
import org.apache.sshd.client.ClientBuilder

import java.nio.file.Path

object SftpProtocolBuilder {

  implicit def toSftpProtocol(builder: SftpProtocolBuilder): SftpProtocol = builder.build

  def apply(configuration: GatlingConfiguration): SftpProtocolBuilder = SftpProtocolBuilder(SftpProtocol(configuration))
}

final case class SftpProtocolBuilder(protocol: SftpProtocol) {
  def client(clientBuilder: ClientBuilder): SftpProtocolBuilder = this.modify(_.protocol.exchange.clientBuilder).setTo(clientBuilder)

  def server(server: String): SftpProtocolBuilder = this.modify(_.protocol.exchange.server).setTo(server)

  def port(port: Int): SftpProtocolBuilder = this.modify(_.protocol.exchange.port).setTo(port)

  def password(username: Expression[String], password: Expression[String]): SftpProtocolBuilder =
    this.modify(_.protocol.exchange.authType).setTo(Authentications.Password)
      .modify(_.protocol.credentials).setTo(SftpHelper.buildPasswordAuth(username, password))

  def keyPair(username: Expression[String], keyPath: Expression[String]): SftpProtocolBuilder =
    this.modify(_.protocol.exchange.authType).setTo(Authentications.KeyPair)
      .modify(_.protocol.credentials).setTo(SftpHelper.buildKeyPairAuth(username, keyPath))

  def keyPair(username: Expression[String], keyPath: Expression[String], keyPassphrase: Expression[String]): SftpProtocolBuilder =
    this.modify(_.protocol.exchange.authType).setTo(Authentications.KeyPair)
      .modify(_.protocol.credentials).setTo(SftpHelper.buildKeyPairAuth(username, keyPath, keyPassphrase))

  def localPath(path: Path): SftpProtocolBuilder = this.localSourcePath(path).localDestinationPath(path)

  def localSourcePath(sourcePath: Path): SftpProtocolBuilder = this.modify(_.protocol.localSourcePath).setTo(Some(sourcePath))

  def localDestinationPath(destPath: Path): SftpProtocolBuilder = this.modify(_.protocol.localDestinationPath).setTo(Some(destPath))

  def remotePath(path: String): SftpProtocolBuilder = this.remoteSourcePath(path).remoteDestinationPath(path)

  def remoteSourcePath(sourcePath: String): SftpProtocolBuilder = this.modify(_.protocol.remoteSourcePath).setTo(Some(sourcePath))

  def remoteDestinationPath(destPath: String): SftpProtocolBuilder = this.modify(_.protocol.remoteDestinationPath).setTo(Some(destPath))

  def build: SftpProtocol = protocol
}