package ru.skuptsov.robot.model;

import com.google.common.base.MoreObjects;

/**
 * @author Sergey Kuptsov
 * @since 18/03/2017
 */
public class StoragePageIndex {
    private String id;
    private long date;
    private String path;
    private String title;
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("date", date)
                .add("path", path)
                .add("title", title)
                .add("url", url)
                .toString();
    }

    public static StoragePageIndex from(StoragePage storagePage) {
        StoragePageIndex storagePageIndex = new StoragePageIndex();
        storagePageIndex.setId(storagePage.getId());
        storagePageIndex.setDate(System.currentTimeMillis());
        storagePageIndex.setPath(storagePage.getPath());
        storagePageIndex.setTitle(storagePage.getTitle());
        storagePageIndex.setUrl(storagePage.getUrl().toString());

        return storagePageIndex;
    }
}
