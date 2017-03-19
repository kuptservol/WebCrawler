package ru.skuptsov.robot.indexer.impl;

import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import ru.skuptsov.robot.configuration.Configurable;
import ru.skuptsov.robot.configuration.Configuration;
import ru.skuptsov.robot.exception.PageSearchIndexException;
import ru.skuptsov.robot.model.SearchIndex;
import ru.skuptsov.robot.util.io.IOUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.google.common.collect.HashMultimap.create;
import static com.google.common.collect.ImmutableList.of;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.nio.file.Files.*;
import static java.nio.file.Paths.get;
import static java.util.stream.Collectors.*;
import static ru.skuptsov.robot.util.io.IOUtils.createIfNotExists;

/**
 * @author Sergey Kuptsov
 * @since 19/03/2017
 */
public class PageSearchIndex extends Configurable<Configuration> {

    private final static String WORD_INDEX_DELIMITER = "::::";
    private final static String INDEX_PAGE_ID_DELIMITER = ",";
    private final static String INDEX_FILE_FORMAT = "%s" + WORD_INDEX_DELIMITER + "%s";

    public final Function<String, String> SEARCH_INDEX_WORD_BUCKET_KEY_FUNCTION = (s) ->
            valueOf((s.hashCode() & 0xfffffff) % getConfig().getSearchIndexBucketNum());

    private final Path searchIndexDir;

    protected PageSearchIndex(Configuration configuration) {
        super(configuration);
        searchIndexDir = getConfig().getSearchIndexDir();

        createIfNotExists(searchIndexDir);
    }

    public SetMultimap<String, String> read() {
        SetMultimap<String, String> storagePageIndex = create();

        try (DirectoryStream<Path> directoryStream = newDirectoryStream(searchIndexDir)) {
            directoryStream.forEach(indexFile -> readIndexFile(indexFile, storagePageIndex));
        } catch (Exception ex) {
            throw new PageSearchIndexException("IOException: ", ex);
        }

        return storagePageIndex;
    }

    public void write(Multimap<String, String> index) {
        Map<String, List<Map.Entry<String, Collection<String>>>> wordIndexBuckets = index.asMap().entrySet().stream()
                .collect(groupingBy(v -> SEARCH_INDEX_WORD_BUCKET_KEY_FUNCTION.apply(v.getKey()), toList()));

        wordIndexBuckets.entrySet()
                .forEach(this::index);
    }

    private void index(Map.Entry<String, List<Map.Entry<String, Collection<String>>>> wordIndexBucket) {
        try (BufferedWriter writer = IOUtils.newBufferedWriter(searchIndexDir.resolve(get(wordIndexBucket.getKey())))) {
            wordIndexBucket.getValue().stream()
                    .map(this::getIndexFileLine)
                    .forEach(nextLine -> writeIndexLine(writer, nextLine));
        } catch (Exception x) {
            throw new PageSearchIndexException("IOException: ", x);
        }
    }

    private String getIndexFileLine(Map.Entry<String, Collection<String>> wordEntry) {
        return format(INDEX_FILE_FORMAT,
                wordEntry.getKey(),
                wordEntry.getValue()
                        .stream()
                        .collect(joining(INDEX_PAGE_ID_DELIMITER)));
    }

    private void writeIndexLine(BufferedWriter writer, String nextLine) {
        try {
            writer.write(nextLine, 0, nextLine.length());
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readIndexFile(Path indexFile, Multimap<String, String> searchPageIndex) {
        try (Stream<String> lines = lines(indexFile)) {

            lines
                    .filter(line -> !line.isEmpty())
                    .map(this::readIndexFileLine)
                    .forEach(searchIndex -> searchPageIndex.putAll(searchIndex.getWord(), searchIndex.getStorageIds()));

        } catch (Exception x) {
            throw new PageSearchIndexException("IOException: ", x);
        }
    }

    private SearchIndex readIndexFileLine(String line) {
        String[] splitedLine = line.split(WORD_INDEX_DELIMITER);

        if (splitedLine.length != 2) {
            throw new IllegalArgumentException("Unknown index file format on line [" + line + "]");
        }

        String[] pageIds = splitedLine[1].split(INDEX_PAGE_ID_DELIMITER);

        return new SearchIndex(splitedLine[0], Arrays.stream(pageIds).collect(toList()));
    }

    List<String> findWordInBucket(String word) {
        String indexBucketFileName = SEARCH_INDEX_WORD_BUCKET_KEY_FUNCTION.apply(word);

        Path searchIndexFileBucket = searchIndexDir.resolve(indexBucketFileName);
        if (notExists(searchIndexFileBucket)) {
            return of();
        }

        try (Stream<String> lines = lines(searchIndexFileBucket)) {
            return lines
                    .filter(line -> !line.isEmpty())
                    .map(this::readIndexFileLine)
                    .filter(index -> index.getWord().equals(word))
                    .findFirst()
                    .map(SearchIndex::getStorageIds)
                    .orElse(of());
        } catch (Exception x) {
            throw new PageSearchIndexException("IOException: ", x);
        }
    }
}
