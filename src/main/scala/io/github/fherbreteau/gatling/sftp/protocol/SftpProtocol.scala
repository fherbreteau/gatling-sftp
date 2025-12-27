package io.github.fherbreteau.gatling.sftp.protocol

import com.typesafe.scalalogging.StrictLogging
import io.gatling.commons.validation.Failure
import io.gatling.core.CoreComponents
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.protocol.{Protocol, ProtocolKey}
import io.gatling.core.session.{Expression, Session}
import io.github.fherbreteau.gatling.sftp.client.Exchange
import io.github.fherbreteau.gatling.sftp.model.{Authentications, Credentials}

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
        authType = Authentications.Password,
      ),
      credentials = (_: Session) => Failure("unauthenticated") ,
      localSourcePath = None,
      localDestinationPath = None,
      remoteSourcePath = None,
      remoteDestinationPath = None
    )
}

final case class SftpProtocol(exchange: Exchange,
                              credentials: Expression[Credentials],
                              localSourcePath: Option[Path],
                              localDestinationPath: Option[Path],
                              remoteSourcePath: Option[String],
                              remoteDestinationPath: Option[String]) extends Protocol {
  type Components = SftpComponents

  def localSource(file: String): Path = {
    localSourcePath.getOrElse(Paths.get(".")).resolve(file)
  }

  def localDestination(file: String): Path = {
    localDestinationPath.getOrElse(Paths.get(".")).resolve(file)
  }

  def remoteSource(file: String): String = {
    remoteSourcePath.getOrElse("").concat("/").concat(file)
  }

  def remoteDestination(file: String): String = {
    remoteDestinationPath.getOrElse("").concat("/").concat(file)
  }

  def credentials(session: Session): Credentials = credentials.apply(session).toOption.get
}
