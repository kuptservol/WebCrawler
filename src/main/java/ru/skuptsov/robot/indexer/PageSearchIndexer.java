package ru.skuptsov.robot.indexer;

import com.beust.jcommander.internal.Nullable;
import ru.skuptsov.robot.model.StoragePage;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Sergey Kuptsov
 * @since 18/03/2017
 */
public interface PageSearchIndexer {
    void start();

    void index(@Nullable StoragePage page);

    void stop();

    @NotNull
    List<String> getPageIdsByWord(@NotNull String word);
}
