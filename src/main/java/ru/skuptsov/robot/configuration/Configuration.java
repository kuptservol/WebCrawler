package ru.skuptsov.robot.configuration;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.skuptsov.robot.exception.ConfigurationException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;

import static com.google.common.util.concurrent.MoreExecutors.shutdownAndAwaitTermination;
import static java.lang.Integer.valueOf;
import static java.lang.Runtime.getRuntime;
import static java.nio.file.Files.*;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.commons.beanutils.BeanUtils.populate;
import static ru.skuptsov.robot.util.properties.PropUtils.asMap;

/**
 * @author Sergey Kuptsov
 * @since 15/03/2017
 */
public class Configuration implements LoadableConfiguration {
    private final static Logger log = LoggerFactory.getLogger(Configuration.class);

    private String crawlContentDir = "content";
    private int politenessDelayMs = 20;
    private int connectTimeout = 10000;
    private int readTimeout = 60000;
    private int maxRequestRetry = 2;
    private int maxPageTasks = 10000;
    private String pageIndexDirName = "page_index";
    private String searchIndexDirName = "search_index";
    private int pageFetchPoolSize = getRuntime().availableProcessors();
    private int parserServicePoolSize = getRuntime().availableProcessors();
    private int indexerServicePoolSize = getRuntime().availableProcessors();
    // HDD
    private int pageStorageServicePoolSize = 1;

    private int pageIndexBucketsNum = 32;
    private int searchIndexBucketNum = 128;

    private CsvMapper csvMapper = new CsvMapper();
    private AsyncHttpClient httpClient;
    private ExecutorService fetcherExecutorService;
    private ExecutorService parserExecutorService;
    private ExecutorService pageIndexerExecutorService;
    private ExecutorService pageStorageExecutorService;
    private Path contentDir;
    private Path pageIndexDir;
    private Path searchIndexDir;

    @Override
    public void load(Properties properties) {
        log.debug("Loading configuration from properties [{}]", properties);

        try {
            populate(this, asMap(properties));
        } catch (Exception e) {
            throw new ConfigurationException("Can't populate from properties", e);
        }

        log.debug("Loading configuration from properties [{}] completed");
    }

    public void initialize() {
        log.debug("Initializing configuration");

        initHttpClient();
        initThreadPools();
        initContentDir();

        log.debug("Initializing configuration");
    }

    private void initThreadPools() {
        fetcherExecutorService = getExecutorService(
                pageFetchPoolSize,
                getThreadFactory("PageParser-%d"));

        parserExecutorService = getExecutorService(
                parserServicePoolSize,
                getThreadFactory("PageParser-%d"));

        pageIndexerExecutorService = getExecutorService(
                indexerServicePoolSize,
                getThreadFactory("Indexer-%d")
        );

        pageStorageExecutorService = getExecutorService(
                pageStorageServicePoolSize,
                getThreadFactory("Storage-%d"));
    }

    private ThreadFactory getThreadFactory(String threadNamePattern) {
        return new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat(threadNamePattern)
                .build();
    }

    private ExecutorService getExecutorService(int pageStorageServicePoolSize, ThreadFactory build) {
        return newFixedThreadPool(
                pageStorageServicePoolSize,
                build);
    }

    private void initHttpClient() {
        AsyncHttpClientConfig.Builder asyncHttpClientConfigBuilder = new AsyncHttpClientConfig.Builder()
                .setAllowPoolingConnections(true)
                .setConnectTimeout(connectTimeout)
                .setReadTimeout(readTimeout)
                .setMaxRequestRetry(maxRequestRetry)
                .setMaxConnectionsPerHost(pageFetchPoolSize)
                .setFollowRedirect(true);

        httpClient = new AsyncHttpClient(asyncHttpClientConfigBuilder.build());
    }

    private void initContentDir() {
        contentDir = Paths.get(crawlContentDir);

        if (!isDirectory(contentDir)) {
            throw new ConfigurationException("Content directory [" + contentDir.toAbsolutePath() + "] not exists");
        }

        if (!isWritable(contentDir) || !isReadable(contentDir)) {
            throw new ConfigurationException("You dont have wright to content directory [" + contentDir.toAbsolutePath() + "]");
        }

        pageIndexDir = contentDir.resolve(pageIndexDirName);
        searchIndexDir = contentDir.resolve(searchIndexDirName);
    }

    public void destroy() {
        log.debug("Start destroying configuration");
        closeIfNotNul(httpClient, AsyncHttpClient::close);
        closeIfNotNul(fetcherExecutorService,
                service -> shutdownAndAwaitTermination(service, 1, SECONDS));
        closeIfNotNul(parserExecutorService,
                service -> shutdownAndAwaitTermination(service, 1, SECONDS));
        closeIfNotNul(pageStorageExecutorService,
                service -> shutdownAndAwaitTermination(service, 1, SECONDS));

        log.debug("Destroying configuration finished");
    }

    private <T> void closeIfNotNul(T object, Consumer<T> consumer) {
        ofNullable(object)
                .ifPresent(consumer);
    }

    public int getMaxPageTasks() {
        return maxPageTasks;
    }

    public int getPolitenessDelayMs() {
        return politenessDelayMs;
    }

    public ExecutorService getFetcherExecutorService() {
        return fetcherExecutorService;
    }

    public ExecutorService getParserExecutorService() {
        return parserExecutorService;
    }

    public ExecutorService getPageStorageExecutorService() {
        return pageStorageExecutorService;
    }

    public ExecutorService getPageIndexerExecutorService() {
        return pageIndexerExecutorService;
    }

    public AsyncHttpClient getHttpClient() {
        return httpClient;
    }

    public Path getContentDir() {
        return contentDir;
    }

    public Path getPageIndexDir() {
        return pageIndexDir;
    }

    public Path getSearchIndexDir() {
        return searchIndexDir;
    }

    public CsvMapper getCsvMapper() {
        return csvMapper;
    }

    public int getSearchIndexBucketNum() {
        return searchIndexBucketNum;
    }

    public int getPageIndexBucketsNum() {
        return pageIndexBucketsNum;
    }

    public void setCrawlContentDir(String crawlContentDir) {
        this.crawlContentDir = crawlContentDir;
    }

    public void setPolitenessDelayMs(String politenessDelayMs) {
        this.politenessDelayMs = valueOf(politenessDelayMs);
    }

    public void setConnectTimeout(String connectTimeout) {
        this.connectTimeout = valueOf(connectTimeout);
    }

    public void setReadTimeout(String readTimeout) {
        this.readTimeout = valueOf(readTimeout);
    }

    public void setMaxRequestRetry(String maxRequestRetry) {
        this.maxRequestRetry = valueOf(maxRequestRetry);
    }

    public void setMaxPageTasks(String maxPageTasks) {
        this.maxPageTasks = valueOf(maxPageTasks);
    }

    public void setPageIndexDirName(String pageIndexDirName) {
        this.pageIndexDirName = pageIndexDirName;
    }

    public void setSearchIndexDirName(String searchIndexDirName) {
        this.searchIndexDirName = searchIndexDirName;
    }

    public void setPageFetchPoolSize(String pageFetchPoolSize) {
        this.pageFetchPoolSize = valueOf(pageFetchPoolSize);
    }

    public void setParserServicePoolSize(String parserServicePoolSize) {
        this.parserServicePoolSize = valueOf(parserServicePoolSize);
    }

    public void setIndexerServicePoolSize(String indexerServicePoolSize) {
        this.indexerServicePoolSize = valueOf(indexerServicePoolSize);
    }

    public void setPageStorageServicePoolSize(String pageStorageServicePoolSize) {
        this.pageStorageServicePoolSize = valueOf(pageStorageServicePoolSize);
    }

    public void setPageIndexBucketsNum(String pageIndexBucketsNum) {
        this.pageIndexBucketsNum = valueOf(pageIndexBucketsNum);
    }

    public void setSearchIndexBucketNum(String searchIndexBucketNum) {
        this.searchIndexBucketNum = valueOf(searchIndexBucketNum);
    }
}
