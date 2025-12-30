package io.github.fherbreteau.gatling.sftp.javaapi;

import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.core.internal.Expressions;
import io.github.fherbreteau.gatling.sftp.javaapi.protocol.SftpProtocolBuilder;

import jakarta.annotation.Nonnull;
import java.util.function.Function;

/**
 * The entrypoint of the Gatling SFTP DSL
 */
public class SftpDsl {

    /**
     * Bootstrap a SFTP protocol configuration
     */
    public static final SftpProtocolBuilder sftp =
            new SftpProtocolBuilder(io.github.fherbreteau.gatling.sftp.protocol.SftpProtocolBuilder.apply(
                    io.gatling.core.Predef.configuration()));

    private SftpDsl() {
    }

    /**
     * Bootstrap a SFTP command configuration
     *
     * @param name the SFTP command name, expressed as a Gatling Expression Language String
     * @return the next DSL step
     */
    @Nonnull
    public static Sftp sftp(@Nonnull String name) {
        return new Sftp(Expressions.toStringExpression(name));
    }

    /**
     * Bootstrap a SFTP command configuration
     *
     * @param name the SFTP command name, expressed as a function
     * @return the next DSL step
     */
    @Nonnull
    public static Sftp sftp(@Nonnull Function<Session, String> name) {
        return new Sftp(Expressions.javaFunctionToExpression(name));
    }

}
