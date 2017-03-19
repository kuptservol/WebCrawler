package ru.skuptsov.robot.exception;

/**
 * @author Sergey Kuptsov
 * @since 18/03/2017
 */
public class PageSearchIndexException extends RuntimeException {
    public PageSearchIndexException() {
    }

    public PageSearchIndexException(String message) {
        super(message);
    }

    public PageSearchIndexException(String message, Throwable cause) {
        super(message, cause);
    }
}
