package io.github.fherbreteau.gatling.sftp.javaapi.action;

import io.gatling.javaapi.core.ActionBuilder;

public class SftpActionBuilder implements ActionBuilder {

    private final io.github.fherbreteau.gatling.sftp.action.SftpActionBuilder wrapped;

    public SftpActionBuilder(io.github.fherbreteau.gatling.sftp.action.SftpActionBuilder wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public io.gatling.core.action.builder.ActionBuilder asScala() {
        return wrapped;
    }
}
