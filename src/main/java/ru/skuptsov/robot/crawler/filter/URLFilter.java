package ru.skuptsov.robot.crawler.filter;

import java.net.URL;
import java.util.function.Predicate;

/**
 * @author Sergey Kuptsov
 * @since 17/03/2017
 */
public interface URLFilter extends Predicate<URL> {
}
