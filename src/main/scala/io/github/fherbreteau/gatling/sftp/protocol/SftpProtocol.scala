package io.github.fherbreteau.gatling.sftp.protocol

import com.typesafe.scalalogging.StrictLogging
import io.gatling.commons.model.Credentials
import io.gatling.core.CoreComponents
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.protocol.{Protocol, ProtocolKey}
import io.github.fherbreteau.gatling.sftp.client.Exchange

import java.nio.file.{Path, Paths}

object SftpProtocol extends StrictLogging {

  val SftpProtocolKey: ProtocolKey[SftpProtocol, SftpComponents] = new ProtocolKey[SftpProtocol, SftpComponents] {

    override def protocolClass: Class[Protocol] = classOf[SftpProtocol].asInstanceOf[Class[Protocol]]

    override def defaultProtocolValue(configuration: GatlingConfiguration): SftpProtocol =
      throw new IllegalArgumentException("Can't provide a default value for ImportProtocol")

    override def newComponents(coreComponents: CoreComponents): SftpProtocol => SftpComponents = {

      sftpProtocol => SftpComponents(sftpProtocol)
    }
  }

  def apply(configuration: GatlingConfiguration): SftpProtocol =
    new SftpProtocol(
      exchange = Exchange(
        server = "localhost",
        port = 22,
        credentials = Credentials("", "")
      ),
      localSourcePath = None,
      localDestinationPath = None,
      remoteSourcePath = None,
      remoteDestinationPath = None
    )
}

final case class SftpProtocol(exchange: Exchange,
                              localSourcePath: Option[Path],
                              localDestinationPath: Option[Path],
                              remoteSourcePath: Option[Path],
                              remoteDestinationPath: Option[Path]) extends Protocol {
  type Components = SftpComponents

  def source(file: String, isLocal: Boolean): Path = {
    basePath(localSourcePath, remoteSourcePath, isLocal).resolve(file)
  }

  def destination(file: String, isLocal: Boolean): Path = {
    basePath(localDestinationPath, remoteDestinationPath, isLocal).resolve(file)
  }

  private def basePath(localPath: Option[Path], remotePath: Option[Path], isLocal: Boolean): Path =
    if (isLocal) {
      localPath.getOrElse(Paths.get("."))
    } else {
      remotePath.getOrElse(Paths.get(s"/home/${exchange.credentials.username}"))
    }
}
