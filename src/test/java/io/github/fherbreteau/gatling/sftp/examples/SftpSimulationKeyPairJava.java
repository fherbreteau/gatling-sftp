package io.github.fherbreteau.gatling.sftp.examples;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.github.fherbreteau.gatling.sftp.javaapi.SftpDsl.sftp;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyPair;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.github.fherbreteau.gatling.sftp.javaapi.protocol.SftpProtocolBuilder;
import org.apache.sshd.common.config.keys.FilePasswordProvider;
import org.apache.sshd.common.util.security.SecurityUtils;

public class SftpSimulationKeyPairJava extends Simulation {

    SftpProtocolBuilder sftpProtocol = sftp
            .server("localhost")
            .port(2222)
            .keyPair("user", getKeyPair("/keys/test.key"))
            .localPath(Paths.get("./src/test/resources/data"))
            .remotePath("/tmp");

    String source = "file_to_upload.txt";
    String destination = "file_copied.txt";


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
                    .delete(source));

    private static KeyPair getKeyPair(String path) {
        try (InputStream stream = SftpSimulationKeyPairJava.class.getResourceAsStream(path)) {
            Iterable<KeyPair> keyPairs = SecurityUtils.loadKeyPairIdentities(null, null, stream, FilePasswordProvider.EMPTY);
            return keyPairs.iterator().next();
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    {
        setUp(scn.injectOpen(atOnceUsers(1)).protocols(sftpProtocol));
    }
}
