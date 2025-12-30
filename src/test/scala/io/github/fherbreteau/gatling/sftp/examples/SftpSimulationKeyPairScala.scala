package io.github.fherbreteau.gatling.sftp.examples

import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilder
import io.gatling.core.structure.ScenarioBuilder
import io.github.fherbreteau.gatling.sftp.Predef.sftp
import io.github.fherbreteau.gatling.sftp.protocol.SftpProtocolBuilder

import java.nio.file.Paths

class SftpSimulationKeyPairScala extends Simulation {

  // Set up Sftp protocol with key pair auth
  val sftpProtocol: SftpProtocolBuilder = sftp
    .server("localhost")
    .port(2222)
    .keyPair("#{username}", "#{keypair}")
    .localPath(Paths.get("./src/test/resources/data"))
    .remotePath("/tmp")

  val source = "file_to_upload.txt"
  val destination = "file_copied"

  // Load credentials from CSV
  val credentialsFeeder: FeederBuilder = csv("credential.csv").circular

  // Define the test scenario
  val scn: ScenarioBuilder = scenario("SFTP Scenario")
    .feed(credentialsFeeder)
    .exec(
      exec(sftp("Upload a file").upload(source)),
      exec(sftp("Copy remote file").copy(source, destination)),
      exec(sftp("Delete remote file").delete(source)),
      exec(sftp("Move remote file").move(destination, source)),
      exec(sftp("Delete remote file").delete(source))
    )

  // Set up the simulation with open workload model
  setUp(scn.inject(atOnceUsers(1)).protocols(sftpProtocol))
}
