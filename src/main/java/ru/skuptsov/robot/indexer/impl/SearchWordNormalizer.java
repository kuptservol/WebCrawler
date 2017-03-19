package ru.skuptsov.robot.indexer.impl;

import javax.validation.constraints.NotNull;

/**
 * @author Sergey Kuptsov
 * @since 19/03/2017
 */
public class SearchWordNormalizer {
    public static String normalize(@NotNull String s) {
        return s.toLowerCase();
    }
}
