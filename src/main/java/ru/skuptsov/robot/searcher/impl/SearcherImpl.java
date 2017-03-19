package ru.skuptsov.robot.searcher.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.skuptsov.robot.configuration.Configurable;
import ru.skuptsov.robot.configuration.Configuration;
import ru.skuptsov.robot.crawler.storage.impl.PageStorageImpl;
import ru.skuptsov.robot.indexer.impl.PageSearchSearchIndexerImpl;
import ru.skuptsov.robot.model.SearchResult;
import ru.skuptsov.robot.searcher.Searcher;

import javax.validation.constraints.NotNull;

/**
 * @author Sergey Kuptsov
 * @since 19/03/2017
 */
public class SearcherImpl extends Configurable<Configuration> implements Searcher {
    private final static Logger log = LoggerFactory.getLogger(SearcherImpl.class);
    private final PageSearchSearchIndexerImpl pageSearchIndexer;
    private final PageStorageImpl pageStorage;

    public SearcherImpl(Configuration configuration) {
        super(configuration);
        pageSearchIndexer = new PageSearchSearchIndexerImpl(configuration);
        pageStorage = new PageStorageImpl(configuration);
    }

    @Override
    public SearchResult search(@NotNull String word) {
        log.debug("Trying to search word [{}] in [{}]", word, getConfig().getContentDir());

        SearchResult searchResult =
                new SearchResult(pageStorage.getIndexByIds(pageSearchIndexer.getPageIdsByWord(word)));

        log.debug("Search results for to search word [{}] is \n [{}]", word, searchResult);

        return searchResult;
    }
}
