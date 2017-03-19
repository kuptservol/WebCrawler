package ru.skuptsov.robot.command.factory.init;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import ru.skuptsov.robot.command.Command;
import ru.skuptsov.robot.command.concrete.CrawlerCommand;
import ru.skuptsov.robot.exception.ConfigurationException;

import java.net.URL;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.Integer.valueOf;
import static org.apache.commons.cli.Option.builder;

/**
 * @author Sergey Kuptsov
 * @since 16/03/2017
 */
public class CrawlerCommandInit extends GeneralCommandInit {
    private final static String URL = "url";
    private final static String DEPTH = "depth";

    public final static Supplier<Options> CRAWLER_COMMAND_OPTIONS = () -> {
        Options options = new Options();

        options.addOption(
                builder()
                        .longOpt(DEPTH)
                        .argName("d")
                        .required()
                        .hasArg()
                        .desc("Crawl depth")
                        .build());

        options.addOption(
                builder()
                        .longOpt(URL)
                        .argName("u")
                        .required()
                        .hasArg()
                        .desc("Start url")
                        .build());

        options.addOption(
                builder()
                        .longOpt(CONFIG)
                        .hasArg()
                        .desc("Configuration file")
                        .build());

        return options;
    };

    public final static Function<CommandLine, Command> CRAWLER_COMMAND_INITIALIZER = (cl) -> {
        try {
            return new CrawlerCommand(
                    new URL(cl.getOptionValue(URL)),
                    valueOf(cl.getOptionValue(DEPTH)),
                    getConfiguration(cl));
        } catch (Exception ex) {
            throw new ConfigurationException("Configuration exception", ex);
        }
    };
}
