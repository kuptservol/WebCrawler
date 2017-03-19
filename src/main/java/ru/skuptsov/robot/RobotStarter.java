package ru.skuptsov.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.skuptsov.robot.exception.ConfigurationException;

import java.util.Arrays;

import static ru.skuptsov.robot.configuration.GeneralConfiguration.getCommandFactory;

/**
 * @author Sergey Kuptsov
 * @since 15/03/2017
 */
public class RobotStarter {
    private final static Logger log = LoggerFactory.getLogger(RobotStarter.class);

    public static void main(String[] args) {
        log.info("Starting robot with arguments [{}]", Arrays.toString(args));

        try {
            getCommandFactory().resolveCommand(args).run();
        } catch (ConfigurationException ex) {
            log.error("Configuration exception occured", ex);
        }

        log.info("Robot finished job with arguments [{}]", Arrays.toString(args));
    }
}
