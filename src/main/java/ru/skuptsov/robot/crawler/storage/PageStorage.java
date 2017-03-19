package ru.skuptsov.robot.crawler.storage;

import ru.skuptsov.robot.model.ParsedPage;
import ru.skuptsov.robot.model.StoragePage;
import ru.skuptsov.robot.model.StoragePageIndex;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.List;

/**
 * @author Sergey Kuptsov
 * @since 17/03/2017
 */
public interface PageStorage {
    void start();

    boolean contains(@NotNull URL url);

    StoragePage save(@NotNull ParsedPage page);

    void stop();

    List<StoragePageIndex> getIndexByIds(List<String> pageIds);
}
