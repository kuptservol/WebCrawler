package ru.skuptsov.robot.exception;

/**
 * @author Sergey Kuptsov
 * @since 15/03/2017
 */
public class ConfigurationException extends RuntimeException {
    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
