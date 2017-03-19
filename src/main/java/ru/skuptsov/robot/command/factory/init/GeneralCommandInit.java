package ru.skuptsov.robot.command.factory.init;

import org.apache.commons.cli.CommandLine;
import ru.skuptsov.robot.configuration.Configuration;
import ru.skuptsov.robot.configuration.LoadableConfiguration;
import ru.skuptsov.robot.exception.ConfigurationException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Sergey Kuptsov
 * @since 16/03/2017
 */
public class GeneralCommandInit {
    protected final static String CONFIG = "config";

    protected static void loadConfigurationFromFile(CommandLine cl, LoadableConfiguration configuration) {
        Properties properties = new Properties();
        try (InputStream fileInputStream = new FileInputStream(cl.getOptionValue(CONFIG))) {
            properties.load(fileInputStream);
            configuration.load(properties);
        } catch (IOException e) {
            throw new ConfigurationException("Configuration file not found", e);
        }
    }

    protected static Configuration getConfiguration(CommandLine cl) {
        Configuration configuration = new Configuration();

        if (cl.hasOption(CONFIG)) {
            loadConfigurationFromFile(cl, configuration);
        }

        try {
            configuration.initialize();
        } catch (Exception ex) {
            configuration.destroy();
            throw ex;
        }
        return configuration;
    }
}
