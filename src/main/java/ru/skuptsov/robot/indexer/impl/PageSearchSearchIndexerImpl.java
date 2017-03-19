package ru.skuptsov.robot.indexer.impl;

import com.beust.jcommander.internal.Nullable;
import com.google.common.collect.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.skuptsov.robot.configuration.Configurable;
import ru.skuptsov.robot.configuration.Configuration;
import ru.skuptsov.robot.indexer.PageSearchIndexer;
import ru.skuptsov.robot.model.StoragePage;

import javax.validation.constraints.NotNull;
import java.util.List;

import static com.google.common.collect.HashMultimap.create;
import static com.google.common.collect.Multimaps.synchronizedSetMultimap;
import static ru.skuptsov.robot.indexer.impl.SearchWordNormalizer.normalize;
import static ru.skuptsov.robot.indexer.impl.WordTokenizer.getTokens;

/**
 * @author Sergey Kuptsov
 * @since 18/03/2017
 */
public class PageSearchSearchIndexerImpl extends Configurable<Configuration> implements PageSearchIndexer {
    private final static Logger log = LoggerFactory.getLogger(PageSearchSearchIndexerImpl.class);

    private Multimap<String, String> searchReverseIndex = synchronizedSetMultimap(create());
    private final PageSearchIndex pageSearchIndex;

    public PageSearchSearchIndexerImpl(Configuration configuration) {
        super(configuration);
        pageSearchIndex = new PageSearchIndex(configuration);
    }

    @Override
    public void start() {
        log.debug("Starting page indexer at directory [{}]", getConfig().getContentDir().toAbsolutePath());
        searchReverseIndex = synchronizedSetMultimap(pageSearchIndex.read());
        log.debug("Starting page indexer finished");
    }

    @Override
    public void index(@Nullable StoragePage page) {
        log.debug("Start to index page [{}]", page);
        if (page == null) {
            return;
        }

        getTokens(page.getContent())
                .stream()
                .forEach(token -> searchReverseIndex.put(token, page.getId()));

        log.debug("Finished indexing page [{}]", page);
    }

    @Override
    public void stop() {
        log.debug("Stopping");
        pageSearchIndex.write(searchReverseIndex);
        log.debug("Stopped");
    }

    @Override
    @NotNull
    public List<String> getPageIdsByWord(@NotNull String word) {
        return pageSearchIndex.findWordInBucket(normalize(word));
    }
}
