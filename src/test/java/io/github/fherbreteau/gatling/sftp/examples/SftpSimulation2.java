package io.github.fherbreteau.gatling.sftp.examples;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.github.fherbreteau.gatling.sftp.javaapi.protocol.SftpProtocolBuilder;

import java.nio.file.Paths;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.github.fherbreteau.gatling.sftp.javaapi.SftpDsl.sftp;

public class SftpSimulation2 extends Simulation {

    SftpProtocolBuilder sftpProtocol = sftp
            .server("localhost")
            .port(2222)
            .credentials("user", "password")
            .localSourcePath(Paths.get("./src/test/resources/data"))
            .remoteSourcePath(Paths.get("/tmp"));

    ScenarioBuilder scn = scenario("SFTP Scenario")
            .exec(sftp("Upload a file")
                    .upload("file_to_upload"))
            .exec(sftp("Move remote file")
                    .copy("file_to_upload"))
            .exec(sftp("Delete remote file")
                    .delete("file_to_upload"));

    {
        setUp(scn.injectOpen(atOnceUsers(1)).protocols(sftpProtocol));
    }
}
