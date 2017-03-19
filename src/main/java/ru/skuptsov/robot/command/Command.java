package ru.skuptsov.robot.command;

/**
 * @author Sergey Kuptsov
 * @since 15/03/2017
 */
public interface Command {
    void run();

    void destroy();
}
