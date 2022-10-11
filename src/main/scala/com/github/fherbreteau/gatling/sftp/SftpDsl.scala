package com.github.fherbreteau.gatling.sftp

import com.github.fherbreteau.gatling.sftp.protocol.{SftpProtocol, SftpProtocolBuilder}
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.session.Expression

import scala.language.implicitConversions

trait SftpDsl {

  def sftp(implicit configuration: GatlingConfiguration): SftpProtocolBuilder = SftpProtocolBuilder(configuration)

  def sftp(requestName: Expression[String]): Sftp = Sftp(requestName)

  implicit def sftpProtocolBuilder2SftpProtocol(builder: SftpProtocolBuilder): SftpProtocol = builder.build
}
