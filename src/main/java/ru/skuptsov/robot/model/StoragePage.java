package ru.skuptsov.robot.model;

import java.util.UUID;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * @author Sergey Kuptsov
 * @since 18/03/2017
 */
public class StoragePage extends ParsedPage {

    private String path;
    private final String id;

    public StoragePage(ParsedPage page) {
        super(page);
        id = UUID.randomUUID().toString();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("url", getUrl())
                .add("name", getTitle())
                .add("path", path)
                .add("id", id)
                .toString();
    }
}
