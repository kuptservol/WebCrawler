package ru.skuptsov.robot.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.skuptsov.robot.configuration.Configuration;
import ru.skuptsov.robot.crawler.fetcher.PageFetcher;
import ru.skuptsov.robot.model.PageProcessTask;
import ru.skuptsov.robot.crawler.parser.PageParser;
import ru.skuptsov.robot.crawler.storage.PageStorage;
import ru.skuptsov.robot.indexer.PageSearchIndexer;
import ru.skuptsov.robot.util.concurrent.Completion;

import javax.validation.constraints.NotNull;
import java.util.concurrent.CompletableFuture;

import static ru.skuptsov.robot.model.PageProcessTask.nextTask;

/**
 * @author Sergey Kuptsov
 * @since 18/03/2017
 */
public class CrawlTask {
    private final static Logger log = LoggerFactory.getLogger(CrawlTask.class);

    private final PageStorage pageStorage;
    private final PageFetcher fetcher;
    private final PageParser pageParser;
    private final PageSearchIndexer pageSearchIndexer;
    private final Configuration config;
    private final Crawler crawler;

    public CrawlTask(PageStorage pageStorage,
                     PageFetcher fetcher,
                     PageParser pageParser,
                     PageSearchIndexer pageSearchIndexer,
                     Configuration config,
                     Crawler crawler) {
        this.pageStorage = pageStorage;
        this.fetcher = fetcher;
        this.pageParser = pageParser;
        this.pageSearchIndexer = pageSearchIndexer;
        this.config = config;
        this.crawler = crawler;
    }

    void execute(@NotNull Completion completion, @NotNull PageProcessTask currentTask) {
        log.debug("Start processing next task [{}]", currentTask);

        CompletableFuture
                .supplyAsync(
                        () -> fetcher.get(currentTask.getPageUrl()),
                        config.getFetcherExecutorService())
                .thenApplyAsync(
                        pageParser::parse,
                        config.getParserExecutorService())
                .thenApply(
                        htmlParsedPage -> {
                            htmlParsedPage
                                    .getOutgoingLinks()
                                    .forEach(url -> crawler.addTask(nextTask(currentTask, url)));
                            return htmlParsedPage;
                        }
                )
                .thenApplyAsync(
                        pageStorage::save,
                        config.getPageStorageExecutorService())
                .thenAcceptAsync(
                        (page) -> {
                            pageSearchIndexer.index(page);
                            completion.complete();
                        },
                        config.getPageIndexerExecutorService()
                )
                .exceptionally(ex -> {
                    log.error("Exception on execution phase, stop executing pipeline", ex);
                    completion.complete();

                    return null;
                });

        log.debug("Start processing next task finished [{}]", currentTask);
    }
}
