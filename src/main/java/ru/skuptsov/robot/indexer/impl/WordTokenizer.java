package ru.skuptsov.robot.indexer.impl;

import javax.validation.constraints.NotNull;
import java.util.Set;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;

/**
 * @author Sergey Kuptsov
 * @since 19/03/2017
 */
public class WordTokenizer {
    private static final String HTML_NON_ALPANUMERIC_SPLIT_REGEXP = "[^\\p{L}+]";

    //TODO: index words only at the html body
    public static Set<String> getTokens(@NotNull String page) {
        return stream(
                page.split(HTML_NON_ALPANUMERIC_SPLIT_REGEXP))
                .parallel()
                .map(SearchWordNormalizer::normalize)
                .filter(s -> !s.isEmpty())
                .collect(toSet());
    }
}
