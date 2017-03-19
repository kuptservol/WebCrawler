package ru.skuptsov.robot.model;

import com.google.common.base.MoreObjects;

import java.util.List;

/**
 * @author Sergey Kuptsov
 * @since 19/03/2017
 */
public class SearchIndex {
    private final String word;
    private final List<String> storageIds;

    public SearchIndex(String word, List<String> storageIds) {
        this.word = word;
        this.storageIds = storageIds;
    }

    public String getWord() {
        return word;
    }

    public List<String> getStorageIds() {
        return storageIds;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("word", word)
                .add("storageIds", storageIds)
                .toString();
    }
}
