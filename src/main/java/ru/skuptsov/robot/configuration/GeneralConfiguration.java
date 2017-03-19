package ru.skuptsov.robot.configuration;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import ru.skuptsov.robot.command.factory.CliCommandFactory;

/**
 * @author Sergey Kuptsov
 * @since 16/03/2017
 */
public class GeneralConfiguration {
    private static final CommandLineParser parser = new DefaultParser();
    private static final CliCommandFactory commandFactory = new CliCommandFactory();

    public static CommandLineParser getCommandLineParser() {
        return parser;
    }

    public static CliCommandFactory getCommandFactory() {
        return commandFactory;
    }
}
