package ru.skuptsov.robot.model;

import com.google.common.base.MoreObjects;

import java.net.URL;

/**
 * @author Sergey Kuptsov
 * @since 17/03/2017
 */
public class Page {
    private final URL url;
    private final byte[] contentData;
    private String contentType;

    public Page(URL url, byte[] contentData) {
        this.url = url;
        this.contentData = contentData;
    }

    public URL getUrl() {
        return url;
    }

    public byte[] getContentData() {
        return contentData;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("contentType", contentType)
                .add("url", url)
                .toString();
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }
}
