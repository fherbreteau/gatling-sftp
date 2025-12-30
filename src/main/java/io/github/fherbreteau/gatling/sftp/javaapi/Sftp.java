package io.github.fherbreteau.gatling.sftp.javaapi;

import io.gatling.commons.validation.Validation;
import io.gatling.core.session.Session;
import io.gatling.javaapi.core.internal.Expressions;
import io.github.fherbreteau.gatling.sftp.javaapi.action.SftpActionBuilder;
import scala.Function1;

import jakarta.annotation.Nonnull;
import java.util.function.Function;

/**
 * DSL for bootstrapping SFTP command.
 *
 * <p>Immutable, so all methods return a new occurrence and leave the original unmodified.
 */
public class Sftp {

    private final Function1<Session, Validation<String>> name;

    Sftp(Function1<io.gatling.core.session.Session, Validation<String>> name) {
        this.name = name;
    }

    /**
     * Define a mkdir command.
     * @param directory the directory to create, expressed as a Gatling Expression Language String
     * @return a new instance of SftpActionBuilder
     */
    public SftpActionBuilder mkdir(@Nonnull String directory) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).mkdir(Expressions.toStringExpression(directory)));
    }

    /**
     * Define a mkdir command.
     * @param directory the directory to create, expressed as a function
     * @return a new instance of SftpActionBuilder
     */
    public SftpActionBuilder mkdir(@Nonnull Function<io.gatling.javaapi.core.Session, String> directory) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).mkdir(Expressions.javaFunctionToExpression(directory)));
    }

    /**
     * Define a move command.
     * @param source the source to move, expressed as a Gatling Expression Language String
     * @param destination the destination, expressed as a Gatling Expression Language String
     * @return a new instance of SftpActionBuilder
     */
    public SftpActionBuilder move(@Nonnull String source, @Nonnull String destination) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).move(Expressions.toStringExpression(source), Expressions.toStringExpression(destination)));
    }

    /**
     * Define a move command.
     * @param source the source to move, expressed as a function
     * @param destination the destination, expressed as a function
     * @return a new instance of SftpActionBuilder
     */
    public SftpActionBuilder move(@Nonnull Function<io.gatling.javaapi.core.Session, String> source,
                                  @Nonnull Function<io.gatling.javaapi.core.Session, String> destination) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).move(Expressions.javaFunctionToExpression(source), Expressions.javaFunctionToExpression(destination)));
    }

    /**
     * Define a copy command.
     * @param source the source to move, expressed as a Gatling Expression Language String
     * @param destination the destination, expressed as a Gatling Expression Language String
     * @return a new instance of SftpActionBuilder
     */
    public SftpActionBuilder copy(@Nonnull String source, @Nonnull String destination) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).copy(Expressions.toStringExpression(source), Expressions.toStringExpression(destination)));
    }

    /**
     * Define a copy command.
     * @param source the source to move, expressed as a function
     * @param destination the destination, expressed as a function
     * @return a new instance of SftpActionBuilder
     */
    public SftpActionBuilder copy(@Nonnull Function<io.gatling.javaapi.core.Session, String> source,
                                  @Nonnull Function<io.gatling.javaapi.core.Session, String> destination) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).copy(Expressions.javaFunctionToExpression(source), Expressions.javaFunctionToExpression(destination)));
    }

    /**
     * Define an upload command.
     * @param file the file to upload, expressed as a Gatling Expression Language String
     * @return a new instance of SftpActionBuilder
     */
    public SftpActionBuilder upload(@Nonnull String file) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).upload(Expressions.toStringExpression(file)));
    }

    /**
     * Define an upload command.
     * @param file the file to upload, expressed as a function
     * @return a new instance of SftpActionBuilder
     */
    public SftpActionBuilder upload(@Nonnull Function<io.gatling.javaapi.core.Session, String> file) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).upload(Expressions.javaFunctionToExpression(file)));
    }

    /**
     * Define an upload command.
     * @param source the source to upload, expressed as a Gatling Expression Language String
     * @param destination the destination to upload to, expressed as a Gatling Expression Language String
     * @return a new instance of SftpActionBuilder
     */
    public SftpActionBuilder upload(@Nonnull String source, @Nonnull String destination) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).upload(Expressions.toStringExpression(source), Expressions.toStringExpression(destination)));
    }

    /**
     * Define an upload command.
     * @param source the source to upload, expressed as a function
     * @param destination the destination to upload to, expressed as a function
     * @return a new instance of SftpActionBuilder
     */
    public SftpActionBuilder upload(@Nonnull Function<io.gatling.javaapi.core.Session, String> source,
                                    @Nonnull Function<io.gatling.javaapi.core.Session, String> destination) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).upload(Expressions.javaFunctionToExpression(source), Expressions.javaFunctionToExpression(destination)));
    }

    /**
     * Define a download command.
     * @param file the file to download, expressed as a Gatling Expression Language String
     * @return a new instance of SftpActionBuilder
     */
    public SftpActionBuilder download(@Nonnull String file) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).download(Expressions.toStringExpression(file)));
    }

    /**
     * Define a download command.
     * @param file the file to download, expressed as a function
     * @return a new instance of SftpActionBuilder
     */
    public SftpActionBuilder download(@Nonnull Function<io.gatling.javaapi.core.Session, String> file) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).download(Expressions.javaFunctionToExpression(file)));
    }

    /**
     * Define a download command.
     * @param source the source to download, expressed as a Gatling Expression Language String
     * @param destination the destination to download to, expressed as a Gatling Expression Language String
     * @return a new instance of SftpActionBuilder
     */
    public SftpActionBuilder download(@Nonnull String source, @Nonnull String destination) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).download(Expressions.toStringExpression(source), Expressions.toStringExpression(destination)));
    }

    /**
     * Define a download command.
     * @param source the source to download, expressed as a function
     * @param destination the destination to download to, expressed as a function
     * @return a new instance of SftpActionBuilder
     */
    public SftpActionBuilder download(@Nonnull Function<io.gatling.javaapi.core.Session, String> source,
                                      @Nonnull Function<io.gatling.javaapi.core.Session, String> destination) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).download(Expressions.javaFunctionToExpression(source), Expressions.javaFunctionToExpression(destination)));
    }

    /**
     * Define a delete command.
     * @param file the file to delete, expressed as a Gatling Expression Language String
     * @return a new instance of SftpActionBuilder
     */
    public SftpActionBuilder delete(@Nonnull String file) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).delete(Expressions.toStringExpression(file)));
    }

    /**
     * Define a delete command.
     * @param file the file to delete, expressed as a function
     * @return a new instance of SftpActionBuilder
     */
    public SftpActionBuilder delete(@Nonnull Function<io.gatling.javaapi.core.Session, String> file) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).delete(Expressions.javaFunctionToExpression(file)));
    }

    /**
     * Define a rmdir command.
     * @param directory the directory to delete, expressed as a Gatling Expression Language String
     * @return a new instance of SftpActionBuilder
     */
    public SftpActionBuilder rmdir(@Nonnull String directory) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).rmdir(Expressions.toStringExpression(directory)));
    }

    /**
     * Define a rmdir command.
     * @param directory the directory to delete, expressed as a function
     * @return a new instance of SftpActionBuilder
     */
    public SftpActionBuilder rmdir(@Nonnull Function<io.gatling.javaapi.core.Session, String> directory) {
        return new SftpActionBuilder(new io.github.fherbreteau.gatling.sftp.Sftp(name).rmdir(Expressions.javaFunctionToExpression(directory)));
    }

}
