package ru.skuptsov.robot.searcher;

import ru.skuptsov.robot.model.SearchResult;
import javax.validation.constraints.NotNull;

/**
 * @author Sergey Kuptsov
 * @since 19/03/2017
 */
public interface Searcher {
    SearchResult search(@NotNull String word);
}
