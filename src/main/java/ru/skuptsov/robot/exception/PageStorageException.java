package ru.skuptsov.robot.exception;

/**
 * @author Sergey Kuptsov
 * @since 18/03/2017
 */
public class PageStorageException extends RuntimeException {
    public PageStorageException() {
    }

    public PageStorageException(String message) {
        super(message);
    }

    public PageStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
