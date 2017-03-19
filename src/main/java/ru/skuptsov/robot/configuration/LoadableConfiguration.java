package ru.skuptsov.robot.configuration;

import java.util.Properties;

/**
 * @author Sergey Kuptsov
 * @since 17/03/2017
 */
public interface LoadableConfiguration {
    void load(Properties properties);
}
