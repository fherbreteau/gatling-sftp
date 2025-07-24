package io.github.fherbreteau.gatling.sftp.examples;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.github.fherbreteau.gatling.sftp.javaapi.protocol.SftpProtocolBuilder;

import java.nio.file.Paths;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.github.fherbreteau.gatling.sftp.javaapi.SftpDsl.sftp;

public class SftpSimulationPasswordJava extends Simulation {

    SftpProtocolBuilder sftpProtocol = sftp
            .server("localhost")
            .port(2222)
            .password("user", "password")
            .localPath(Paths.get("./src/test/resources/data"))
            .remotePath("/tmp");

    String source = "file_to_upload.txt";
    String destination = "file_copied";
    String absentSource = "non_existent";


    ScenarioBuilder scn = scenario("SFTP Scenario")
            .exec(sftp("Upload a file")
                    .upload(source))
            .exec(sftp("Copy remote file")
                    .copy(source, destination))
            .exec(sftp("Delete remote file")
                    .delete(source))
            .exec(sftp("Move remote file")
                    .move(destination, source))
            .exec(sftp("Delete remote file")
                    .delete(source))
            .exec(sftp("Upload no existent local file")
                    .upload(absentSource));

    {
        setUp(scn.injectOpen(atOnceUsers(1)).protocols(sftpProtocol));
    }
}
