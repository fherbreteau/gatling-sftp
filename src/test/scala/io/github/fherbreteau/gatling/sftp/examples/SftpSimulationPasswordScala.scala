package io.github.fherbreteau.gatling.sftp.examples

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.github.fherbreteau.gatling.sftp.Predef.sftp
import io.github.fherbreteau.gatling.sftp.protocol.SftpProtocolBuilder

import java.nio.file.Paths

class SftpSimulationPasswordScala extends Simulation {

  val sftpProtocol: SftpProtocolBuilder = sftp
    .server("localhost")
    .port(2222)
    .password("user", "password")
    .localPath(Paths.get("./src/test/resources/data"))
    .remotePath("/tmp")

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
