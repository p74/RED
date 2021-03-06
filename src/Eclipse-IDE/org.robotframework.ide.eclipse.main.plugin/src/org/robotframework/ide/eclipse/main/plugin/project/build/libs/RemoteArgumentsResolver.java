/*
 * Copyright 2018 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.project.build.libs;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.rf.ide.core.libraries.ArgumentsDescriptor;
import org.rf.ide.core.project.RobotProjectConfig.RemoteLocation;
import org.rf.ide.core.testdata.text.read.recognizer.RobotToken;

public class RemoteArgumentsResolver {

    private final Optional<RobotToken> uriToken;

    private final Optional<String> uri;

    private final ArgumentsDescriptor descriptor;

    public RemoteArgumentsResolver(final List<RobotToken> arguments) {
        this.uriToken = selectUriToken(arguments);
        this.uri = selectUri(uriToken, arguments);
        this.descriptor = ArgumentsDescriptor.createDescriptor("uri=" + RemoteLocation.DEFAULT_ADDRESS, "timeout=None");
    }

    public Optional<RobotToken> getUriToken() {
        return uriToken;
    }

    public Optional<String> getUri() {
        return uri;
    }

    public ArgumentsDescriptor getDescriptor() {
        return descriptor;
    }

    private Optional<RobotToken> selectUriToken(final List<RobotToken> arguments) {
        if (arguments.size() == 1 || arguments.size() == 2) {
            if (isPositional(arguments.get(0).getText())) {
                return Optional.of(arguments.get(0));
            } else {
                final List<RobotToken> allUris = arguments.stream().filter(uri -> isUri(uri.getText())).collect(
                        Collectors.toList());
                if (allUris.size() == 1) {
                    return Optional.of(allUris.get(0));
                }
            }
        }
        return Optional.empty();
    }

    private Optional<String> selectUri(final Optional<RobotToken> uriToken, final List<RobotToken> arguments) {
        if (arguments.isEmpty()) {
            return Optional.of(RemoteLocation.DEFAULT_ADDRESS);
        } else if (arguments.size() == 1 && isTimeout(arguments.get(0).getText())) {
            return Optional.of(RemoteLocation.DEFAULT_ADDRESS);
        } else if (uriToken.isPresent()) {
            return Optional.of(addProtocolIfNecessary(stripArgumentPrefixIfNecessary(uriToken.get().getText())));
        }
        return Optional.empty();
    }

    private static boolean isPositional(final String argument) {
        return !isUri(argument) && !isTimeout(argument);
    }

    private static boolean isUri(final String argument) {
        return argument.toLowerCase().startsWith("uri=");
    }

    private static boolean isTimeout(final String argument) {
        return argument.toLowerCase().startsWith("timeout=");
    }

    private static String addProtocolIfNecessary(final String argument) {
        return argument.contains("://") ? argument : "http://" + argument;
    }

    private static String stripArgumentPrefixIfNecessary(final String string) {
        return string.toLowerCase().startsWith("uri=") ? string.substring(4, string.length()) : string;
    }

    public static String stripLastSlashIfNecessary(final String string) {
        return string.endsWith("/") ? string.substring(0, string.length() - 1) : string;
    }
}
