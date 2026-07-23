package io.github.fherbreteau.gatling.sftp.javaapi.protocol;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Path;

import org.apache.sshd.client.SshClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SftpProtocolBuilderTest {

    @Mock
    private io.github.fherbreteau.gatling.sftp.protocol.SftpProtocolBuilder wrapped;

    @InjectMocks
    private SftpProtocolBuilder protocolBuilder;

    @Test
    void shouldDefineTheClient() {
        // Arrange
        SshClient client = SshClient.setUpDefaultClient();
        when(wrapped.client(any())).thenReturn(any());
        // Act
        SftpProtocolBuilder newBuilder = protocolBuilder.client(client);
        // Assert
        assertThat(newBuilder).isNotNull();
        verify(wrapped).client(client);
    }

    @Test
    void shouldDefineTheServer() {
        // Arrange
        when(wrapped.server(any())).thenReturn(wrapped);
        // Act
        SftpProtocolBuilder newBuilder = protocolBuilder.server("server");
        // Assert
        assertThat(newBuilder).isNotNull();
        verify(wrapped).server("server");
    }

    @Test
    void shouldDefineThePort() {
        // Arrange
        when(wrapped.port(anyInt())).thenReturn(wrapped);
        // Act
        SftpProtocolBuilder newBuilder = protocolBuilder.port(1234);
        // Assert
        assertThat(newBuilder).isNotNull();
        verify(wrapped).port(1234);
    }

    @Test
    void shouldDefineThePasswordAuthentication() {
        // Arrange
        when(wrapped.password(any(), any())).thenReturn(wrapped);
        // Act
        SftpProtocolBuilder newBuilder = protocolBuilder.password("username", "password");
        // Assert
        assertThat(newBuilder).isNotNull();
        verify(wrapped).password(any(), any());
    }

    @Test
    void shouldDefineTheKeyPairAuthentication() {
        // Arrange
        when(wrapped.keyPair(any(), any())).thenReturn(wrapped);
        // Act
        SftpProtocolBuilder newBuilder = protocolBuilder.keyPair("username", "keypair");
        // Assert
        assertThat(newBuilder).isNotNull();
        verify(wrapped).keyPair(any(), any());
    }

    @Test
    void shouldDefineTheKeyPairAuthenticationWithPassphrase() {
        // Arrange
        when(wrapped.keyPair(any(), any(), any())).thenReturn(wrapped);
        // Act
        SftpProtocolBuilder newBuilder = protocolBuilder.keyPair("username", "keypair", "passphrase");
        // Assert
        assertThat(newBuilder).isNotNull();
        verify(wrapped).keyPair(any(), any(), any());
    }

    @Test
    void shouldDefineTheLocalPath() {
        // Arrange
        Path path = Path.of("path");
        when(wrapped.localSourcePath(any())).thenReturn(wrapped);
        when(wrapped.localDestinationPath(any())).thenReturn(wrapped);
        // Act
        SftpProtocolBuilder newBuilder = protocolBuilder.localPath(path);
        // Assert
        assertThat(newBuilder).isNotNull();
        verify(wrapped).localSourcePath(path);
        verify(wrapped).localDestinationPath(path);
    }

    @Test
    void shouldDefineTheRemotePath() {
        // Arrange
        when(wrapped.remoteSourcePath(any())).thenReturn(wrapped);
        when(wrapped.remoteDestinationPath(any())).thenReturn(wrapped);
        // Act
        SftpProtocolBuilder newBuilder = protocolBuilder.remotePath("path");
        // Assert
        assertThat(newBuilder).isNotNull();
        verify(wrapped).remoteSourcePath("path");
        verify(wrapped).remoteDestinationPath("path");
    }

    @Test
    void shouldDefineTheThreadPoolSize() {
        // Arrange
        when(wrapped.threadPoolSize(anyInt())).thenReturn(wrapped);
        // Act
        SftpProtocolBuilder newBuilder = protocolBuilder.threadPoolSize(5);
        // Assert
        assertThat(newBuilder).isNotNull();
        verify(wrapped).threadPoolSize(5);
    }

    @Test
    void shouldDefineTheEnableSessionPooling() {
        // Arrange
        when(wrapped.enableSessionPooling(anyBoolean())).thenReturn(wrapped);
        // Act
        SftpProtocolBuilder newBuilder = protocolBuilder.enableSessionPooling(false);
        // Assert
        assertThat(newBuilder).isNotNull();
        verify(wrapped).enableSessionPooling(false);
    }

    @Test
    void shouldDefineTheMaxPooledSession() {
        // Arrange
        when(wrapped.maxPooledSessions(anyInt())).thenReturn(wrapped);
        // Act
        SftpProtocolBuilder newBuilder = protocolBuilder.maxPooledSessions(5);
        // Assert
        assertThat(newBuilder).isNotNull();
        verify(wrapped).maxPooledSessions(5);
    }
}
