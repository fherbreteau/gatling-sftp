package io.github.fherbreteau.gatling.sftp.javaapi.action;

import io.gatling.javaapi.core.ActionBuilder;

/**
 * DSL for building SFTP commands configurations
 *
 * <p>Immutable, so all methods return a new occurrence and leave the original unmodified.
 */
public class SftpActionBuilder implements ActionBuilder {

    private final io.github.fherbreteau.gatling.sftp.action.SftpActionBuilder wrapped;

    public SftpActionBuilder(io.github.fherbreteau.gatling.sftp.action.SftpActionBuilder wrapped) {
        this.wrapped = wrapped;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public io.gatling.core.action.builder.ActionBuilder asScala() {
        return wrapped;
    }
}
