package io.github.fherbreteau.gatling.sftp.javaapi;

import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.core.internal.Expressions;
import io.github.fherbreteau.gatling.sftp.javaapi.protocol.SftpProtocolBuilder;

import jakarta.annotation.Nonnull;
import java.util.function.Function;

public class SftpDsl {

    public static final SftpProtocolBuilder sftp =
            new SftpProtocolBuilder(io.github.fherbreteau.gatling.sftp.protocol.SftpProtocolBuilder.apply(
                    io.gatling.core.Predef.configuration()));

    private SftpDsl() {
    }

    @Nonnull
    public static Sftp sftp(@Nonnull String name) {
        return new Sftp(Expressions.toStringExpression(name));
    }

    @Nonnull
    public static Sftp sftp(@Nonnull Function<Session, String> name) {
        return new Sftp(Expressions.javaFunctionToExpression(name));
    }

}
