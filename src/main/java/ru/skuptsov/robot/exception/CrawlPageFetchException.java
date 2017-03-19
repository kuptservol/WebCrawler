package ru.skuptsov.robot.exception;

/**
 * @author Sergey Kuptsov
 * @since 18/03/2017
 */
public class CrawlPageFetchException extends RuntimeException {

    public CrawlPageFetchException() {
    }

    public CrawlPageFetchException(String message) {
        super(message);
    }

    public CrawlPageFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}
