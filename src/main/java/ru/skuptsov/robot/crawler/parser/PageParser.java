package ru.skuptsov.robot.crawler.parser;

import ru.skuptsov.robot.model.Page;
import ru.skuptsov.robot.model.ParsedPage;

import javax.validation.constraints.NotNull;

/**
 * @author Sergey Kuptsov
 * @since 17/03/2017
 */
public interface PageParser {
    ParsedPage parse(@NotNull Page page);
}
