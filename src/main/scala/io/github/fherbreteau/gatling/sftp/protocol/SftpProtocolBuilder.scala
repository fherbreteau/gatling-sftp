package io.github.fherbreteau.gatling.sftp.protocol

import io.gatling.core.config.GatlingConfiguration
import io.gatling.internal.quicklens._
import io.github.fherbreteau.gatling.sftp.model.{KeyPairAuth, PasswordAuth}
import org.apache.sshd.client.SshClient
import org.apache.sshd.common.NamedResource
import org.apache.sshd.common.config.keys.FilePasswordProvider
import org.apache.sshd.common.util.security.SecurityUtils

import java.io.InputStream
import java.nio.file.Path
import java.security.KeyPair
import scala.language.implicitConversions

object SftpProtocolBuilder {

  implicit def toSftpProtocol(builder: SftpProtocolBuilder): SftpProtocol = builder.build

  def apply(configuration: GatlingConfiguration): SftpProtocolBuilder = SftpProtocolBuilder(SftpProtocol(configuration))
}

final case class SftpProtocolBuilder(protocol: SftpProtocol) {
  def client(client: SshClient): SftpProtocolBuilder = this.modify(_.protocol.exchange.client).setTo(client)

  def server(server: String): SftpProtocolBuilder = this.modify(_.protocol.exchange.server).setTo(server)

  def port(port: Int): SftpProtocolBuilder = this.modify(_.protocol.exchange.port).setTo(port)

  def password(username: String, password: String): SftpProtocolBuilder = this.modify(_.protocol.exchange.credentials).setTo(PasswordAuth(username, password))

  def keyPair(username: String, stream: InputStream): SftpProtocolBuilder = this.keyPair(username, null, stream, FilePasswordProvider.EMPTY)

  def keyPair(username: String, resourceKey: NamedResource, stream: InputStream, provider: FilePasswordProvider): SftpProtocolBuilder =
    this.keyPair(username, SecurityUtils.loadKeyPairIdentities(null, resourceKey, stream, provider).iterator().next())

  def keyPair(username: String, keyPair: KeyPair): SftpProtocolBuilder = this.modify(_.protocol.exchange.credentials).setTo(KeyPairAuth(username, keyPair))

  def localPath(path: Path): SftpProtocolBuilder = this.localSourcePath(path).localDestinationPath(path)

  def localSourcePath(sourcePath: Path): SftpProtocolBuilder = this.modify(_.protocol.localSourcePath).setTo(Some(sourcePath))

  def localDestinationPath(destPath: Path): SftpProtocolBuilder = this.modify(_.protocol.localDestinationPath).setTo(Some(destPath))

  def remotePath(path: String): SftpProtocolBuilder = this.remoteSourcePath(path).remoteDestinationPath(path)

  def remoteSourcePath(sourcePath: String): SftpProtocolBuilder = this.modify(_.protocol.remoteSourcePath).setTo(Some(sourcePath))

  def remoteDestinationPath(destPath: String): SftpProtocolBuilder = this.modify(_.protocol.remoteDestinationPath).setTo(Some(destPath))

  def build: SftpProtocol = protocol
}