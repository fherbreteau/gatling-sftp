package com.github.fherbreteau.gatling.sftp.javaapi.protocol;

import io.gatling.core.protocol.Protocol;
import io.gatling.javaapi.core.ProtocolBuilder;
import org.apache.sshd.client.SshClient;

import java.nio.file.Path;

public class SftpProtocolBuilder implements ProtocolBuilder {

    private final com.github.fherbreteau.gatling.sftp.protocol.SftpProtocolBuilder wrapped;

    public SftpProtocolBuilder(com.github.fherbreteau.gatling.sftp.protocol.SftpProtocolBuilder wrapped) {
        this.wrapped = wrapped;
    }

    public SftpProtocolBuilder client( SshClient client) {
        return new SftpProtocolBuilder(wrapped.client(client));
    }

    public SftpProtocolBuilder server( String server) {
        return new SftpProtocolBuilder(wrapped.server(server));
    }

    public SftpProtocolBuilder port(int port) {
        return new SftpProtocolBuilder(wrapped.port(port));
    }

    public SftpProtocolBuilder credentials(String username, String password) {
        return new SftpProtocolBuilder(wrapped.credentials(username, password));
    }

    public SftpProtocolBuilder localSourcePath(Path sourcePath) {
        return new SftpProtocolBuilder(wrapped.localSourcePath(sourcePath));
    }

    public SftpProtocolBuilder localDestinationPath(Path destpath) {
        return new SftpProtocolBuilder(wrapped.localDestinationPath(destpath));
    }

    public SftpProtocolBuilder remoteSourcePath(Path sourcePath) {
        return new SftpProtocolBuilder(wrapped.remoteSourcePath(sourcePath));
    }

    public SftpProtocolBuilder remoteDestinationPath(Path destpath) {
        return new SftpProtocolBuilder(wrapped.remoteDestinationPath(destpath));
    }

    @Override
    public Protocol protocol() {
        return wrapped.protocol();
    }
}
