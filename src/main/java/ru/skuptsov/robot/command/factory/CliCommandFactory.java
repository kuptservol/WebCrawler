package ru.skuptsov.robot.command.factory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import ru.skuptsov.robot.command.Command;
import ru.skuptsov.robot.exception.ConfigurationException;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.google.common.collect.Maps.uniqueIndex;
import static java.util.Arrays.asList;
import static ru.skuptsov.robot.command.factory.init.CrawlerCommandInit.CRAWLER_COMMAND_INITIALIZER;
import static ru.skuptsov.robot.command.factory.init.CrawlerCommandInit.CRAWLER_COMMAND_OPTIONS;
import static ru.skuptsov.robot.command.factory.init.SearchCommandInit.SEARCH_COMMAND_INITIALIZER;
import static ru.skuptsov.robot.command.factory.init.SearchCommandInit.SEARCH_COMMAND_OPTIONS;
import static ru.skuptsov.robot.configuration.GeneralConfiguration.getCommandLineParser;

/**
 * @author Sergey Kuptsov
 * @since 15/03/2017
 */
public class CliCommandFactory {

    public Command resolveCommand(String[] args) throws ConfigurationException {
        CommandType commandType = CommandType.fromCommandName(getCommandName(args));

        if (commandType == null) {
            throw new ConfigurationException("Unknown command name");
        }

        Command command;
        try {
            CommandLine commandLine = getCommandLine(args, commandType);
            command = getCommand(commandType, commandLine);
        } catch (Exception exp) {
            throw new ConfigurationException("Error occured while initializing command", exp);
        }

        return command;
    }

    private Command getCommand(CommandType commandType, CommandLine commandLine) {
        return commandType.getCommandInitializer().apply(commandLine);
    }

    private CommandLine getCommandLine(String[] args, CommandType commandType) throws ParseException {
        return getCommandLineParser().parse(commandType.options.get(), args);
    }

    protected String getCommandName(String[] args) {
        if (args.length > 0) {
            return args[0];
        } else {
            throw new ConfigurationException("Must specify at least one command");
        }
    }

    private enum CommandType {
        CRAWL("crawl", CRAWLER_COMMAND_OPTIONS, CRAWLER_COMMAND_INITIALIZER),
        SEARCH("search", SEARCH_COMMAND_OPTIONS, SEARCH_COMMAND_INITIALIZER);

        private final String commandName;
        private final Supplier<Options> options;
        private final Function<CommandLine, Command> commandInitializer;
        private static final Map<String, CommandType> codeLookup;

        CommandType(String commandName, Supplier<Options> options, Function<CommandLine, Command> commandInitializer) {
            this.commandName = commandName;
            this.options = options;
            this.commandInitializer = commandInitializer;
        }

        public Supplier<Options> getOptions() {
            return options;
        }

        public String getCommandName() {
            return commandName;
        }

        public Function<CommandLine, Command> getCommandInitializer() {
            return commandInitializer;
        }

        public static CommandType fromCommandName(String commandName) {
            return codeLookup.get(commandName);
        }

        static {
            codeLookup = uniqueIndex(asList(values()), CommandType::getCommandName);
        }
    }
}
