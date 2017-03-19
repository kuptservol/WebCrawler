package ru.skuptsov.robot.model;

import com.google.common.base.MoreObjects;

import java.util.List;

/**
 * @author Sergey Kuptsov
 * @since 19/03/2017
 */
public class SearchResult {
    private int count;
    private List<StoragePageIndex> pages;

    public int getCount() {
        return count;
    }

    public List<StoragePageIndex> getPages() {
        return pages;
    }

    public SearchResult(List<StoragePageIndex> pages) {
        this.pages = pages;
        this.count = pages.size();
    }

    public void setPages(List<StoragePageIndex> pages) {
        this.pages = pages;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("count", count)
                .add("pages", pages)
                .toString();
    }
}
