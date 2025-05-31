package io.github.fherbreteau.gatling.sftp.javaapi.protocol;

import io.gatling.core.protocol.Protocol;
import io.gatling.javaapi.core.ProtocolBuilder;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.common.NamedResource;
import org.apache.sshd.common.config.keys.FilePasswordProvider;

import java.io.InputStream;
import java.nio.file.Path;
import java.security.KeyPair;

public class SftpProtocolBuilder implements ProtocolBuilder {

    private final io.github.fherbreteau.gatling.sftp.protocol.SftpProtocolBuilder wrapped;

    public SftpProtocolBuilder(io.github.fherbreteau.gatling.sftp.protocol.SftpProtocolBuilder wrapped) {
        this.wrapped = wrapped;
    }

    public SftpProtocolBuilder client(SshClient client) {
        return new SftpProtocolBuilder(wrapped.client(client));
    }

    public SftpProtocolBuilder server(String server) {
        return new SftpProtocolBuilder(wrapped.server(server));
    }

    public SftpProtocolBuilder port(int port) {
        return new SftpProtocolBuilder(wrapped.port(port));
    }

    public SftpProtocolBuilder password(String username, String password) {
        return new SftpProtocolBuilder(wrapped.password(username, password));
    }

    public SftpProtocolBuilder keyPair(String username, InputStream stream) {
        return new SftpProtocolBuilder(wrapped.keyPair(username, stream));
    }

    public SftpProtocolBuilder keyPair(String username, NamedResource keyResource, InputStream stream, FilePasswordProvider provider) {
        return new SftpProtocolBuilder(wrapped.keyPair(username, keyResource, stream, provider));
    }

    public SftpProtocolBuilder keyPair(String username, KeyPair keyPair) {
        return new SftpProtocolBuilder(wrapped.keyPair(username, keyPair));
    }

    public SftpProtocolBuilder localPath(Path path) {
        return localSourcePath(path).localDestinationPath(path);
    }

    public SftpProtocolBuilder localSourcePath(Path sourcePath) {
        return new SftpProtocolBuilder(wrapped.localSourcePath(sourcePath));
    }

    public SftpProtocolBuilder localDestinationPath(Path destpath) {
        return new SftpProtocolBuilder(wrapped.localDestinationPath(destpath));
    }

    public SftpProtocolBuilder remotePath(String path) {
        return remoteSourcePath(path).remoteDestinationPath(path);
    }

    public SftpProtocolBuilder remoteSourcePath(String sourcePath) {
        return new SftpProtocolBuilder(wrapped.remoteSourcePath(sourcePath));
    }

    public SftpProtocolBuilder remoteDestinationPath(String destpath) {
        return new SftpProtocolBuilder(wrapped.remoteDestinationPath(destpath));
    }

    @Override
    public Protocol protocol() {
        return wrapped.protocol();
    }
}
