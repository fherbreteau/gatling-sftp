package io.github.fherbreteau.gatling.sftp.javaapi;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Function;

import io.gatling.commons.validation.Success;
import io.gatling.commons.validation.Validation;
import io.gatling.javaapi.core.Session;
import io.github.fherbreteau.gatling.sftp.javaapi.action.SftpActionBuilder;
import org.junit.jupiter.api.Test;
import scala.Function1;

class SftpDslTest {

    private final Function1<io.gatling.core.session.Session, Validation<String>> operationName = s -> new Success<>("test-op");
    private final Sftp sftpDsl = new Sftp(operationName);
    private final Function<Session, String> fct = s -> "dir";

    @Test
    void shouldCreateActionBuilderForLsWithString() {
        SftpActionBuilder builder = sftpDsl.ls("dir");
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForLsWithFunction() {
        SftpActionBuilder builder = sftpDsl.ls(fct);
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForMkDirWithString() {
        SftpActionBuilder builder = sftpDsl.mkdir("dir");
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForMkDirWithFunction() {
        SftpActionBuilder builder = sftpDsl.mkdir(fct);
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForMoveWithString() {
        SftpActionBuilder builder = sftpDsl.move("dir", "dir");
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForMoveWithFunction() {
        SftpActionBuilder builder = sftpDsl.move(fct, fct);
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForCopyWithString() {
        SftpActionBuilder builder = sftpDsl.copy("dir", "dir");
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForCopyWithFunction() {
        SftpActionBuilder builder = sftpDsl.copy(fct, fct);
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForUploadWithString() {
        SftpActionBuilder builder = sftpDsl.upload("dir");
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForUploadWithFunction() {
        SftpActionBuilder builder = sftpDsl.upload(fct);
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForUploadSrcDestWithString() {
        SftpActionBuilder builder = sftpDsl.upload("dir", "dir");
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForUploadSrcDestWithFunction() {
        SftpActionBuilder builder = sftpDsl.upload(fct, fct);
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForDownloadWithString() {
        SftpActionBuilder builder = sftpDsl.download("dir");
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForDownloadWithFunction() {
        SftpActionBuilder builder = sftpDsl.download(fct);
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForDownloadSrcDestWithString() {
        SftpActionBuilder builder = sftpDsl.download("dir", "dir");
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForDownloadSrcDestWithFunction() {
        SftpActionBuilder builder = sftpDsl.download(fct, fct);
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForDeleteWithString() {
        SftpActionBuilder builder = sftpDsl.delete("dir");
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForDeleteWithFunction() {
        SftpActionBuilder builder = sftpDsl.delete(fct);
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForRmDirWithString() {
        SftpActionBuilder builder = sftpDsl.rmdir("dir");
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForRmDirWithFunction() {
        SftpActionBuilder builder = sftpDsl.rmdir(fct);
        assertThat(builder.asScala()).isNotNull();
    }
}
