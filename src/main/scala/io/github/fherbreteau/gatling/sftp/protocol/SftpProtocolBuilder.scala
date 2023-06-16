package io.github.fherbreteau.gatling.sftp.protocol

import com.softwaremill.quicklens.ModifyPimp
import io.gatling.commons.model.Credentials
import io.gatling.core.config.GatlingConfiguration
import org.apache.sshd.client.SshClient

import java.nio.file.Path
import scala.language.implicitConversions

object SftpProtocolBuilder {

  implicit def toSftpProtocol(builder: SftpProtocolBuilder): SftpProtocol = builder.build

  def apply(configuration: GatlingConfiguration): SftpProtocolBuilder = SftpProtocolBuilder(SftpProtocol(configuration))
}

final case class SftpProtocolBuilder(protocol: SftpProtocol) {
  def client(client: SshClient): SftpProtocolBuilder = this.modify(_.protocol.exchange.client).setTo(client)

  def server(server: String): SftpProtocolBuilder = this.modify(_.protocol.exchange.server).setTo(server)

  def port(port: Int): SftpProtocolBuilder = this.modify(_.protocol.exchange.port).setTo(port)

  def credentials(username: String, password: String): SftpProtocolBuilder = this.modify(_.protocol.exchange.credentials).setTo(Credentials(username, password))

  def localPath(path: Path): SftpProtocolBuilder = this.localSourcePath(path).localDestinationPath(path)
  def localSourcePath(sourcePath: Path): SftpProtocolBuilder = this.modify(_.protocol.localSourcePath).setTo(Some(sourcePath))

  def localDestinationPath(destPath: Path): SftpProtocolBuilder = this.modify(_.protocol.localDestinationPath).setTo(Some(destPath))

  def remotePath(path: String): SftpProtocolBuilder = this.remoteSourcePath(path).remoteDestinationPath(path)

  def remoteSourcePath(sourcePath: String): SftpProtocolBuilder = this.modify(_.protocol.remoteSourcePath).setTo(Some(sourcePath))

  def remoteDestinationPath(destPath: String): SftpProtocolBuilder = this.modify(_.protocol.remoteDestinationPath).setTo(Some(destPath))

  def build: SftpProtocol = protocol
}