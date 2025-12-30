package io.github.fherbreteau.gatling.sftp.examples;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.github.fherbreteau.gatling.sftp.javaapi.SftpDsl.sftp;

import java.nio.file.Paths;

import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.github.fherbreteau.gatling.sftp.javaapi.protocol.SftpProtocolBuilder;

public class SftpSimulationKeyPairJava extends Simulation {

    // Set up Sftp protocol with key pair auth
    SftpProtocolBuilder sftpProtocol = sftp
            .server("localhost")
            .port(2222)
            .keyPair("#{username}", "#{keypair}")
            .localPath(Paths.get("./src/test/resources/data"))
            .remotePath("/tmp");

    // Load credentials from CSV
    FeederBuilder<String> credentialsFeeder = csv("credential.csv").circular();

    String source = "file_to_upload.txt";
    String destination = "file_copied.txt";

    // Define the test scenario
    ScenarioBuilder scn = scenario("SFTP Scenario")
            .feed(credentialsFeeder)
            .exec(
                    exec(sftp("Upload a file").upload(source)),
                    exec(sftp("Copy remote file").copy(source, destination)),
                    exec(sftp("Delete remote file").delete(source)),
                    exec(sftp("Move remote file").move(destination, source)),
                    exec(sftp("Delete remote file").delete(source))
            );

    {
        // Set up the simulation with open workload model
        setUp(scn.injectOpen(atOnceUsers(1)).protocols(sftpProtocol));
    }
}
