package io.github.fherbreteau.gatling.sftp.examples

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.github.fherbreteau.gatling.sftp.Predef.sftp
import io.github.fherbreteau.gatling.sftp.protocol.SftpProtocolBuilder
import org.apache.sshd.common.config.keys.FilePasswordProvider
import org.apache.sshd.common.util.security.SecurityUtils

import java.io.InputStream
import java.nio.file.Paths
import java.security.KeyPair
import scala.util.Using

class SftpSimulationKeyPairScala extends Simulation {

  val sftpProtocol: SftpProtocolBuilder = sftp
    .server("localhost")
    .port(2222)
    .keyPair("user", loadKeyPair("/keys/test.key"))
    .localPath(Paths.get("./src/test/resources/data"))
    .remotePath("/tmp")

  private def loadKeyPair(path: String) : KeyPair =
    Using.Manager { use =>
      val stream: InputStream = use(getClass.getResourceAsStream(path))
      SecurityUtils.loadKeyPairIdentities(null, null, stream, FilePasswordProvider.EMPTY).iterator().next()
    }.get

  val source = "file_to_upload.txt"
  val destination = "file_copied"

  val scn: ScenarioBuilder = scenario("SFTP Scenario")
    .exec(
      sftp("Upload a file")
        .upload(source))
    .exec(
      sftp("Copy remote file")
        .copy(source, destination))
    .exec(
      sftp("Delete remote file")
        .delete(source))
    .exec(
      sftp("Move remote file")
        .move(destination, source))
    .exec(
      sftp("Delete remote file")
        .delete(source))
  setUp(scn.inject(atOnceUsers(1)).protocols(sftpProtocol))
}
