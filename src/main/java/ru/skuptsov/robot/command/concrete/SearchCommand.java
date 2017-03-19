package ru.skuptsov.robot.command.concrete;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.skuptsov.robot.configuration.Configuration;
import ru.skuptsov.robot.model.SearchResult;
import ru.skuptsov.robot.searcher.Searcher;
import ru.skuptsov.robot.searcher.impl.SearcherImpl;

import javax.validation.constraints.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Sergey Kuptsov
 * @since 19/03/2017
 */
public class SearchCommand extends BaseCommand {
    private final static Logger log = LoggerFactory.getLogger(SearchCommand.class);

    private final String word;
    private final Configuration configuration;

    public SearchCommand(@NotNull String word, @NotNull Configuration configuration) {
        checkNotNull(word);
        checkNotNull(configuration);
        this.word = word;
        this.configuration = configuration;
    }

    @Override
    protected void execute() {
        Searcher searcher = new SearcherImpl(configuration);
        SearchResult searchResult = searcher.search(word);
        printResults(searchResult);
    }

    private void printResults(SearchResult searchResult) {
        log.info("------ SEARCH RESULTS -----");
        log.info("FOUND [{}] MATCHES", searchResult.getCount());
        searchResult.getPages().stream().forEach(
                result -> log.info(result.getUrl())
        );

        log.info("------ SEARCH RESULTS -----");
    }

    @Override
    public void destroy() {
        configuration.destroy();
    }
}
