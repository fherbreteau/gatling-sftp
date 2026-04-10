package io.github.fherbreteau.gatling.sftp.examples

import io.gatling.javaapi.core.CoreDsl.atOnceUsers
import io.gatling.javaapi.core.CoreDsl.csv
import io.gatling.javaapi.core.CoreDsl.exec
import io.gatling.javaapi.core.CoreDsl.scenario
import io.gatling.javaapi.core.FeederBuilder
import io.gatling.javaapi.core.ScenarioBuilder
import io.gatling.javaapi.core.Simulation
import io.github.fherbreteau.gatling.sftp.javaapi.SftpDsl.sftp
import io.github.fherbreteau.gatling.sftp.javaapi.protocol.SftpProtocolBuilder
import java.nio.file.Paths

class SftpSimulationPasswordKotlin : Simulation() {

    // Set up Sftp protocol with key pair auth
    val sftpProtocol: SftpProtocolBuilder = sftp
        .server("localhost")
        .port(2222)
        .password("#{username}", "#{password}")
        .localPath(Paths.get("./src/test/resources/data"))
        .remotePath("/tmp")

    val remotePath = "/tmp"
    val source = "file_to_upload.txt"
    val destination = "file_copied"
    val folder = "folder"

    // Load credentials from CSV
    val credentialsFeeder: FeederBuilder<String> = csv("credential.csv").circular()

    // Define the test scenario
    val scn: ScenarioBuilder = scenario("SFTP Scenario")
        .feed(credentialsFeeder)
        .exec(
            exec(sftp("List remote directory").ls(remotePath)),
            exec(sftp("Upload a file").upload(source)),
            exec(sftp("Copy remote file").copy(source, destination)),
            exec(sftp("Delete remote file").delete(source)),
            exec(sftp("Move remote file").move(destination, source)),
            exec(sftp("Download remote file").download(source)),
            exec(sftp("Delete remote file").delete(source)),
            exec(sftp("Create a remote dir").mkdir(folder)),
            exec(sftp("Delete a remote dir").rmdir(folder))
        )

    // Set up the simulation with open workload model
    init {
        setUp(scn.injectOpen(atOnceUsers(1)).protocols(sftpProtocol))
    }
}