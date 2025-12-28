package io.github.fherbreteau.gatling.sftp.protocol

import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.config.GatlingConfiguration.load
import io.github.fherbreteau.gatling.sftp.Predef.sftp
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers.{equal, not}
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class SftpProtocolFunSuite extends AnyFunSuite {

  test("Should create a SFTP Protocol") {
    val conf : GatlingConfiguration = load()
    val sftpProtocol = sftp(conf).build
    sftpProtocol should not be null
    val localSource = sftpProtocol.localSource(".").toString
    localSource should equal ("./.")
    val localDestination = sftpProtocol.localDestination(".").toString
    localDestination should equal ("./.")

    val remoteSource = sftpProtocol.remoteSource(".").toString
    remoteSource should equal ("./.")
    val remoteDestination = sftpProtocol.remoteDestination(".").toString
    remoteDestination should equal ("./.")

  }

}
