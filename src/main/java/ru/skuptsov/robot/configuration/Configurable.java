package ru.skuptsov.robot.configuration;

/**
 * @author Sergey Kuptsov
 * @since 15/03/2017
 */
public abstract class Configurable<T extends Configuration> {

    private final T configuration;

    protected Configurable(T configuration) {
        this.configuration = configuration;
    }

    public T getConfig() {
        return configuration;
    }
}
