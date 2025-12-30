package io.github.fherbreteau.gatling.sftp.javaapi.protocol;

import static io.gatling.javaapi.core.internal.Expressions.toStringExpression;

import io.gatling.core.protocol.Protocol;
import io.gatling.javaapi.core.ProtocolBuilder;
import org.apache.sshd.client.SshClient;

import java.nio.file.Path;

/**
 * DSL for building SFTP protocol configurations
 *
 * <p>Immutable, so all methods return a new occurrence and leave the original unmodified.
 */
public class SftpProtocolBuilder implements ProtocolBuilder {

    private final io.github.fherbreteau.gatling.sftp.protocol.SftpProtocolBuilder wrapped;

    public SftpProtocolBuilder(io.github.fherbreteau.gatling.sftp.protocol.SftpProtocolBuilder wrapped) {
        this.wrapped = wrapped;
    }

    /**
     * Define the client that will be used for all command.
     * @param client the SSH Client to use
     * @return a new HttpProtocolBuilder instance
     */
    public SftpProtocolBuilder client(SshClient client) {
        return new SftpProtocolBuilder(wrapped.client(client));
    }

    /**
     * Define the server that will be used for all command.
     * @param server the address of the sftp server
     * @return a new HttpProtocolBuilder instance
     */
    public SftpProtocolBuilder server(String server) {
        return new SftpProtocolBuilder(wrapped.server(server));
    }

    /**
     * Define the port that will be used for all command.
     * @param port the port of the sftp server
     * @return a new HttpProtocolBuilder instance
     */
    public SftpProtocolBuilder port(int port) {
        return new SftpProtocolBuilder(wrapped.port(port));
    }

    /**
     * Define an authentication using username and password.
     * @param username the name of the user
     * @param password the password of the user
     * @return a new HttpProtocolBuilder instance
     */
    public SftpProtocolBuilder password(String username, String password) {
        return new SftpProtocolBuilder(wrapped.password(toStringExpression(username), toStringExpression(password)));
    }

    /**
     * Define an authentication using username and an SSH key pair.
     * @param username the name of the user
     * @param keyPath the path to the SSH key pair
     * @return a new HttpProtocolBuilder instance
     */
    public SftpProtocolBuilder keyPair(String username, String keyPath) {
        return new SftpProtocolBuilder(wrapped.keyPair(toStringExpression(username), toStringExpression(keyPath)));
    }

    /**
     * Define an authentication using username and an SSH key pair and its passphrase.
     * @param username the name of the user
     * @param keyPath the path to the SSH key pair
     * @param keyPassphrase the SSH key pair passphrase
     * @return a new HttpProtocolBuilder instance
     */
    public SftpProtocolBuilder keyPair(String username, String keyPath, String keyPassphrase) {
        return new SftpProtocolBuilder(wrapped.keyPair(toStringExpression(username), toStringExpression(keyPath),
                toStringExpression(keyPassphrase)));
    }

    /**
     * Define the local path to be used. Used as local source path and local destination path if not defined.
     * @param path the local path
     * @return a new HttpProtocolBuilder instance
     */
    public SftpProtocolBuilder localPath(Path path) {
        return localSourcePath(path).localDestinationPath(path);
    }

    /**
     * Define the local path for the sources to be used.
     * @param sourcePath the local path for the source
     * @return a new HttpProtocolBuilder instance
     */
    public SftpProtocolBuilder localSourcePath(Path sourcePath) {
        return new SftpProtocolBuilder(wrapped.localSourcePath(sourcePath));
    }

    /**
     * Define the local path for the destinations to be used.
     * @param destinationPath the local path for the destination
     * @return a new HttpProtocolBuilder instance
     */
    public SftpProtocolBuilder localDestinationPath(Path destinationPath) {
        return new SftpProtocolBuilder(wrapped.localDestinationPath(destinationPath));
    }

    /**
     * Define the remote path to be used. Used as remote source path and remote destination path if not defined.
     * @param path the local path
     * @return a new HttpProtocolBuilder instance
     */
    public SftpProtocolBuilder remotePath(String path) {
        return remoteSourcePath(path).remoteDestinationPath(path);
    }

    /**
     * Define the remote path for the sources to be used.
     * @param sourcePath the remote path for the source
     * @return a new HttpProtocolBuilder instance
     */
    public SftpProtocolBuilder remoteSourcePath(String sourcePath) {
        return new SftpProtocolBuilder(wrapped.remoteSourcePath(sourcePath));
    }

    /**
     * Define the remote path for the destinations to be used.
     * @param destinationPath the remote path for the destination
     * @return a new HttpProtocolBuilder instance
     */
    public SftpProtocolBuilder remoteDestinationPath(String destinationPath) {
        return new SftpProtocolBuilder(wrapped.remoteDestinationPath(destinationPath));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Protocol protocol() {
        return wrapped.protocol();
    }
}
