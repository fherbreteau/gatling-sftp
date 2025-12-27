package io.github.fherbreteau.gatling.sftp.javaapi;

import io.gatling.commons.validation.Validation;
import io.gatling.core.session.Session;
import io.gatling.javaapi.core.internal.Expressions;
import io.github.fherbreteau.gatling.sftp.javaapi.action.SftpActionBuilder;
import scala.Function1;

import jakarta.annotation.Nonnull;
import java.util.function.Function;

public class Sftp {

    private final Function1<Session, Validation<String>> name;

    Sftp(Function1<io.gatling.core.session.Session, Validation<String>> name) {
        this.name = name;
    }

    public SftpActionBuilder mkdir(@Nonnull String directory) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).mkdir(Expressions.toStringExpression(directory)));
    }

    public SftpActionBuilder mkdir(@Nonnull Function<io.gatling.javaapi.core.Session, String> directory) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).mkdir(Expressions.javaFunctionToExpression(directory)));
    }

    public SftpActionBuilder move(@Nonnull String source, @Nonnull String destination) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).move(Expressions.toStringExpression(source), Expressions.toStringExpression(destination)));
    }

    public SftpActionBuilder move(@Nonnull Function<io.gatling.javaapi.core.Session, String> source,
                                  @Nonnull Function<io.gatling.javaapi.core.Session, String> destination) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).move(Expressions.javaFunctionToExpression(source), Expressions.javaFunctionToExpression(destination)));
    }

    public SftpActionBuilder copy(@Nonnull String source, @Nonnull String destination) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).copy(Expressions.toStringExpression(source), Expressions.toStringExpression(destination)));
    }

    public SftpActionBuilder copy(@Nonnull Function<io.gatling.javaapi.core.Session, String> source,
                                  @Nonnull Function<io.gatling.javaapi.core.Session, String> destination) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).copy(Expressions.javaFunctionToExpression(source), Expressions.javaFunctionToExpression(destination)));
    }

    public SftpActionBuilder upload(@Nonnull String file) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).upload(Expressions.toStringExpression(file)));
    }

    public SftpActionBuilder upload(@Nonnull Function<io.gatling.javaapi.core.Session, String> file) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).upload(Expressions.javaFunctionToExpression(file)));
    }

    public SftpActionBuilder upload(@Nonnull String source, @Nonnull String destination) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).upload(Expressions.toStringExpression(source), Expressions.toStringExpression(destination)));
    }

    public SftpActionBuilder upload(@Nonnull Function<io.gatling.javaapi.core.Session, String> source,
                                    @Nonnull Function<io.gatling.javaapi.core.Session, String> destination) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).upload(Expressions.javaFunctionToExpression(source), Expressions.javaFunctionToExpression(destination)));
    }

    public SftpActionBuilder download(@Nonnull String file) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).download(Expressions.toStringExpression(file)));
    }

    public SftpActionBuilder download(@Nonnull Function<io.gatling.javaapi.core.Session, String> file) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).download(Expressions.javaFunctionToExpression(file)));
    }

    public SftpActionBuilder download(@Nonnull String source, @Nonnull String destination) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).download(Expressions.toStringExpression(source), Expressions.toStringExpression(destination)));
    }

    public SftpActionBuilder download(@Nonnull Function<io.gatling.javaapi.core.Session, String> source,
                                      @Nonnull Function<io.gatling.javaapi.core.Session, String> destination) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).download(Expressions.javaFunctionToExpression(source), Expressions.javaFunctionToExpression(destination)));
    }

    public SftpActionBuilder delete(@Nonnull String file) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).delete(Expressions.toStringExpression(file)));
    }

    public SftpActionBuilder delete(@Nonnull Function<io.gatling.javaapi.core.Session, String> file) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).delete(Expressions.javaFunctionToExpression(file)));
    }

    public SftpActionBuilder rmdir(@Nonnull String directory) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).rmdir(Expressions.toStringExpression(directory)));
    }

    public SftpActionBuilder rmdir(@Nonnull Function<io.gatling.javaapi.core.Session, String> directory) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).rmdir(Expressions.javaFunctionToExpression(directory)));
    }

}
