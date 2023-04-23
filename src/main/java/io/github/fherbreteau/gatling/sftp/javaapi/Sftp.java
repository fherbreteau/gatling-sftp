package io.github.fherbreteau.gatling.sftp.javaapi;

import io.gatling.commons.validation.Validation;
import io.gatling.core.session.Session;
import io.gatling.javaapi.core.internal.Expressions;
import io.github.fherbreteau.gatling.sftp.javaapi.action.SftpActionBuilder;
import scala.Function1;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class Sftp {

    private final Function1<Session, Validation<String>> name;

    Sftp(Function1<io.gatling.core.session.Session, Validation<String>> name) {
        this.name = name;
    }

    public SftpActionBuilder move(@Nonnull String file) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).move(Expressions.toStringExpression(file)));
    }

    public SftpActionBuilder move(@Nonnull Function<io.gatling.javaapi.core.Session, String> file) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).move(Expressions.javaFunctionToExpression(file)));
    }

    public SftpActionBuilder copy(@Nonnull String file) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).copy(Expressions.toStringExpression(file)));
    }

    public SftpActionBuilder copy(@Nonnull Function<io.gatling.javaapi.core.Session, String> file) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).copy(Expressions.javaFunctionToExpression(file)));
    }

    public SftpActionBuilder delete(@Nonnull String file) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).delete(Expressions.toStringExpression(file)));
    }

    public SftpActionBuilder delete(@Nonnull Function<io.gatling.javaapi.core.Session, String> file) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).delete(Expressions.javaFunctionToExpression(file)));
    }

    public SftpActionBuilder upload(@Nonnull String file) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).upload(Expressions.toStringExpression(file)));
    }

    public SftpActionBuilder upload(@Nonnull Function<io.gatling.javaapi.core.Session, String> file) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).upload(Expressions.javaFunctionToExpression(file)));
    }

    public SftpActionBuilder download(@Nonnull String file) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).download(Expressions.toStringExpression(file)));
    }

    public SftpActionBuilder download(@Nonnull Function<io.gatling.javaapi.core.Session, String> file) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).download(Expressions.javaFunctionToExpression(file)));
    }
}
