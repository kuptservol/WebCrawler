package ru.skuptsov.robot.command.factory.init;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import ru.skuptsov.robot.command.Command;
import ru.skuptsov.robot.command.concrete.SearchCommand;
import ru.skuptsov.robot.exception.ConfigurationException;

import java.util.function.Function;
import java.util.function.Supplier;

import static org.apache.commons.cli.Option.builder;

/**
 * @author Sergey Kuptsov
 * @since 16/03/2017
 */
public class SearchCommandInit extends GeneralCommandInit {

    private static final String WORD = "word";

    public final static Supplier<Options> SEARCH_COMMAND_OPTIONS = () -> {
        Options options = new Options();

        options.addOption(
                builder()
                        .longOpt(WORD)
                        .argName("d")
                        .required()
                        .hasArg()
                        .desc("Crawl depth")
                        .build());

        options.addOption(
                builder()
                        .longOpt(CONFIG)
                        .hasArg()
                        .desc("Configuration file")
                        .build());

        return options;
    };

    public final static Function<CommandLine, Command> SEARCH_COMMAND_INITIALIZER = (cl) -> {

        try {
            return new SearchCommand(
                    cl.getOptionValue(WORD),
                    getConfiguration(cl));
        } catch (Exception ex) {
            throw new ConfigurationException("Configuration exception", ex);
        }
    };
}
