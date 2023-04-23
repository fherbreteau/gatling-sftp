package io.github.fherbreteau.gatling.sftp.examples

import io.gatling.app.Gatling
import io.gatling.core.Predef._
import io.gatling.core.config.GatlingPropertiesBuilder
import io.gatling.core.structure.ScenarioBuilder
import io.github.fherbreteau.gatling.sftp.Predef.sftp
import io.github.fherbreteau.gatling.sftp.protocol.SftpProtocolBuilder

import java.nio.file.Paths

class SftpSimulation extends Simulation {

  val sftpProtocol: SftpProtocolBuilder = sftp
    .server("localhost")
    .port(2222)
    .credentials("user", "password")
    .localSourcePath(Paths.get("./src/test/resources/data"))
    .remoteSourcePath(Paths.get("/tmp"))

  val scn: ScenarioBuilder = scenario("SFTP Scenario")
    .exec(
      sftp("Upload a file")
        .upload("file_to_upload"))
    .exec(
      sftp("Move remote file")
        .copy("file_to_upload"))
    .exec(
      sftp("Delete remote file")
        .delete("file_to_upload")
    )

  setUp(scn.inject(atOnceUsers(1)).protocols(sftpProtocol))
}

object SftpSimulation {
  def main(args: Array[String]): Unit =
    Gatling.fromMap((new GatlingPropertiesBuilder)
      .simulationClass(classOf[io.github.fherbreteau.gatling.sftp.examples.SftpSimulation].getName).build)
}
