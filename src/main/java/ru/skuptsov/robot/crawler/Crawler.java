package ru.skuptsov.robot.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.skuptsov.robot.configuration.Configurable;
import ru.skuptsov.robot.configuration.Configuration;
import ru.skuptsov.robot.crawler.fetcher.PageFetcher;
import ru.skuptsov.robot.crawler.fetcher.impl.AsyncHttpPageFetcher;
import ru.skuptsov.robot.crawler.filter.URLFilter;
import ru.skuptsov.robot.crawler.filter.impl.WikiURLFilterImpl;
import ru.skuptsov.robot.model.PageProcessTask;
import ru.skuptsov.robot.crawler.parser.PageParser;
import ru.skuptsov.robot.crawler.parser.impl.WikiPageLinksParser;
import ru.skuptsov.robot.crawler.storage.PageStorage;
import ru.skuptsov.robot.crawler.storage.impl.PageStorageImpl;
import ru.skuptsov.robot.indexer.PageSearchIndexer;
import ru.skuptsov.robot.indexer.impl.PageSearchSearchIndexerImpl;
import ru.skuptsov.robot.util.concurrent.Completion;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Thread.sleep;
import static ru.skuptsov.robot.model.PageProcessTask.PageProcessTaskBuilder.pageProcessTask;

/**
 * @author Sergey Kuptsov
 * @since 15/03/2017
 */
public class Crawler extends Configurable<Configuration> {
    private final static Logger log = LoggerFactory.getLogger(Crawler.class);
    private static final int START_DEPTH = 0;

    private final BlockingQueue<PageProcessTask> pageProcessTaskQueue;
    private volatile boolean isWaitingForNewTasks;
    private PageStorage pageStorage;
    private URLFilter urlFilter;
    private PageFetcher fetcher;
    private PageParser pageParser;
    private PageSearchIndexer pageSearchIndexer;
    private CrawlTask crawlTask;
    private Completion completion = new Completion();


    public Crawler(Configuration configuration) {
        super(configuration);
        pageProcessTaskQueue = new LinkedBlockingQueue<>(configuration.getMaxPageTasks());
        pageSearchIndexer = new PageSearchSearchIndexerImpl(configuration);
        fetcher = new AsyncHttpPageFetcher(configuration);
        pageParser = new WikiPageLinksParser();
        pageStorage = new PageStorageImpl(configuration);
        urlFilter = new WikiURLFilterImpl();

        crawlTask = new CrawlTask(pageStorage, fetcher, pageParser, pageSearchIndexer, configuration, this);
    }

    public void process(@NotNull URL startUrl, int depth) {
        checkNotNull(startUrl, "Start url cannot be null");
        log.info("Starting to process from url [{}] with depth [{}]", startUrl, depth);

        pageStorage.start();
        pageSearchIndexer.start();

        isWaitingForNewTasks = true;

        addTask(pageProcessTask()
                .withPageUrl(startUrl)
                .withDepth(START_DEPTH)
                .withMaxDepth(depth)
                .build());

        startMainLoop();

        pageStorage.stop();
        pageSearchIndexer.stop();

        log.info("Crawling finished from url [{}] with depth [{}]", startUrl, depth);
    }

    public void addTask(@NotNull PageProcessTask pageProcessTask) {
        if (!isWaitingForNewTasks) {
            log.info("Main process loop is down, task want be processed");
            return;
        }

        try {
            if (canProcess(pageProcessTask)) {
                completion.register();
                pageProcessTaskQueue.put(pageProcessTask);
            } else {
                log.trace("Can't process next pageProcessTask : [{}] due to filter conditions", pageProcessTask);
            }
        } catch (InterruptedException e) {
            log.error("Can't process addTask [{}]", pageProcessTask, e);
        }
    }

    private void startMainLoop() {
        log.debug("Starting crawler main loop");

        while (isWaitingForNewTasks) {
            PageProcessTask currentTask = pageProcessTaskQueue.poll();

            if (currentTask != null) {
                crawlTask.execute(completion, currentTask);
            }

            sleepPoliteDelay();

            if (completion.isAllCompleted()) {
                isWaitingForNewTasks = false;
            }
        }

        log.debug("Crawler main loop finished");
    }

    private void sleepPoliteDelay() {
        try {
            sleep(getConfig().getPolitenessDelayMs());
        } catch (InterruptedException e) {
            log.error("Sleep error", e);
        }
    }

    private boolean canProcess(PageProcessTask task) {
        return task.getDepth() < task.getMaxDepth()
                && notAlreadyProcessed(task)
                && urlFilter.test(task.getPageUrl());
    }

    private boolean notAlreadyProcessed(PageProcessTask nextTask) {
        return !pageStorage.contains(nextTask.getPageUrl());
    }
}
