package ru.skuptsov.robot.crawler.fetcher;

import ru.skuptsov.robot.model.Page;

import javax.validation.constraints.NotNull;
import java.net.URL;

/**
 * @author Sergey Kuptsov
 * @since 17/03/2017
 */
public interface PageFetcher {
    Page get(@NotNull URL url);
}
