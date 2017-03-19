package ru.skuptsov.robot.crawler.storage.impl;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.skuptsov.robot.configuration.Configurable;
import ru.skuptsov.robot.configuration.Configuration;
import ru.skuptsov.robot.exception.PageStorageException;
import ru.skuptsov.robot.model.StoragePageIndex;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.lang.String.valueOf;
import static java.nio.file.Files.*;
import static java.nio.file.Paths.get;
import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static ru.skuptsov.robot.util.io.IOUtils.createIfNotExists;
import static ru.skuptsov.robot.util.io.IOUtils.newBufferedWriter;

/**
 * @author Sergey Kuptsov
 * @since 18/03/2017
 */
class PageStorageIndex extends Configurable<Configuration> {
    private final static Logger log = LoggerFactory.getLogger(PageStorageImpl.class);

    final Function<String, String> PAGE_BUCKET_KEY_FUNCTION = (s) ->
            valueOf((s.hashCode() & 0xfffffff) % getConfig().getPageIndexBucketsNum());

    private final Path pageIndexDir;
    private final CsvSchema schema;

    PageStorageIndex(Configuration configuration) {
        super(configuration);
        pageIndexDir = getConfig().getPageIndexDir();
        schema = getConfig().getCsvMapper().schemaFor(StoragePageIndex.class);

        createIfNotExists(pageIndexDir);
    }

    ConcurrentMap<String, StoragePageIndex> read() {
        ConcurrentMap<String, StoragePageIndex> storagePageIndex = new ConcurrentHashMap<>();

        try (DirectoryStream<Path> directoryStream = newDirectoryStream(pageIndexDir)) {

            stream(directoryStream.spliterator(), false)
                    .map(this::readIndexFile)
                    .flatMap(Collection::stream)
                    .forEach(index -> storagePageIndex.put(index.getId(), index));

        } catch (IOException ex) {
            throw new PageStorageException("IOException: ", ex);
        }

        return storagePageIndex;
    }

    void write(Map<String, StoragePageIndex> index) {
        Map<String, List<StoragePageIndex>> indexByBuckets =
                index.values().stream()
                        .collect(groupingBy(v -> PAGE_BUCKET_KEY_FUNCTION.apply(v.getId()), toList()));

        indexByBuckets.entrySet()
                .forEach(this::index);
    }

    StoragePageIndex getIndexById(String id) {
        String storageIndexBucketFileName = PAGE_BUCKET_KEY_FUNCTION.apply(id);
        log.debug("Searching file with id [{}], resolved path [{}]", id, storageIndexBucketFileName);

        Path storageIndexFileBucket = pageIndexDir.resolve(storageIndexBucketFileName);
        if (notExists(storageIndexFileBucket)) {
            throw new IllegalArgumentException("Storage doesn't contain file " + storageIndexFileBucket);
        }

        return readIndexFile(storageIndexFileBucket).stream()
                .filter(index -> index.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Storage index id doesn't exists"));
    }

    private void index(Map.Entry<String, List<StoragePageIndex>> indexEntry) {
        try (BufferedWriter writer = newBufferedWriter(pageIndexDir.resolve(get(indexEntry.getKey())))) {
            getConfig().getCsvMapper().writer(schema.withUseHeader(true))
                    .writeValues(writer)
                    .writeAll(indexEntry.getValue());
        } catch (IOException x) {
            throw new PageStorageException("IOException: ", x);
        }
    }

    private List<StoragePageIndex> readIndexFile(Path indexFile) {
        try (BufferedReader reader = newBufferedReader(indexFile)) {

            Stream<StoragePageIndex> stream = stream(spliteratorUnknownSize(
                    getConfig().getCsvMapper()
                            .readerFor(StoragePageIndex.class)
                            .with(schema.withUseHeader(true))
                            .readValues(reader),
                    ORDERED),
                    false);

            return stream
                    .collect(toList());

        } catch (IOException x) {
            throw new PageStorageException("IOException: ", x);
        }
    }
}
