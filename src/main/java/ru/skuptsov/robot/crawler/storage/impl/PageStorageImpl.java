package ru.skuptsov.robot.crawler.storage.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.skuptsov.robot.configuration.Configurable;
import ru.skuptsov.robot.configuration.Configuration;
import ru.skuptsov.robot.crawler.storage.PageStorage;
import ru.skuptsov.robot.exception.PageStorageException;
import ru.skuptsov.robot.model.ParsedPage;
import ru.skuptsov.robot.model.StoragePage;
import ru.skuptsov.robot.model.StoragePageIndex;

import javax.validation.constraints.NotNull;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.Paths.get;
import static java.util.Optional.ofNullable;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static ru.skuptsov.robot.crawler.url.URLCanonicalizer.getCanonicalURL;
import static ru.skuptsov.robot.model.StoragePageIndex.from;
import static ru.skuptsov.robot.util.io.IOUtils.createIfNotExists;

/**
 * @author Sergey Kuptsov
 * @since 18/03/2017
 */
//TODO: add file disk compression possibility
public class PageStorageImpl extends Configurable<Configuration> implements PageStorage {
    private final static Logger log = LoggerFactory.getLogger(PageStorageImpl.class);

    private ConcurrentMap<String, StoragePageIndex> urlToStoragePageIndex = new ConcurrentHashMap<>();
    private final PageStorageIndex pageStorageIndex;

    public PageStorageImpl(Configuration configuration) {
        super(configuration);
        pageStorageIndex = new PageStorageIndex(configuration);
    }

    @Override
    public void start() {
        log.debug("Starting page storage at directory [{}]", getConfig().getContentDir().toAbsolutePath());
        urlToStoragePageIndex = pageStorageIndex.read();
        log.debug("Starting page storage finished");
    }

    @Override
    public boolean contains(@NotNull URL url) {
        boolean result = urlToStoragePageIndex.containsKey(getKey(url));

        log.debug("Is storage already contains url [{}] ? [{}]", url, result);
        return result;
    }

    @Override
    public StoragePage save(@NotNull ParsedPage page) {
        log.debug("Starting to save content of page [{}]", page);

        if (contains(page.getUrl())) {
            log.info("Already processed page {}", page);
            return null;
        }

        StoragePage storagePage = new StoragePage(page);

        Path pageDiskPath = getPagePath(storagePage);
        storagePage.setPath(pageDiskPath.toAbsolutePath().toString());

        saveContentToFile(page, pageDiskPath);

        urlToStoragePageIndex.put(getKey(page.getUrl()), from(storagePage));

        log.debug("Finished saving content of page [{}]", page);

        return storagePage;
    }

    @Override
    public void stop() {
        log.debug("Stopping page storage at directory [{}]", getConfig().getContentDir().toAbsolutePath());
        pageStorageIndex.write(urlToStoragePageIndex);
        log.debug("Stopping page storage finished");
    }

    @Override
    public List<StoragePageIndex> getIndexByIds(List<String> pageIds) {
        log.debug("Getting index by ids [{}]", pageIds);
        return pageIds.stream()
                .map(pageStorageIndex::getIndexById)
                .collect(toList());
    }

    private String getKey(URL url) {
        return getCanonicalURL(url);
    }

    private Path getPagePath(StoragePage page) {
        return getPageDir(page)
                .resolve(get(ofNullable(page.getTitle()).orElse(randomUUID().toString())));
    }

    private Path getPageDir(StoragePage page) {
        Path pageDir = getConfig().getContentDir()
                .resolve(get(pageStorageIndex.PAGE_BUCKET_KEY_FUNCTION.apply(page.getId())));

        createIfNotExists(pageDir);

        return pageDir;
    }

    private void saveContentToFile(ParsedPage page, Path pageDiskPath) {
        try (BufferedWriter writer = newBufferedWriter(pageDiskPath)) {
            writer.write(page.getContent(), 0, page.getContent().length());
        } catch (IOException x) {
            throw new PageStorageException("IOException: ", x);
        }
    }
}
