package ru.skuptsov.robot.model;

import com.google.common.base.MoreObjects;

import java.net.URL;
import java.util.List;

/**
 * @author Sergey Kuptsov
 * @since 17/03/2017
 */
public class ParsedPage extends Page {

    private final String title;
    private String content;
    private final List<URL> outgoingLinks;

    public ParsedPage(Page page, String title, List<URL> outgoingLinks) {
        super(page.getUrl(), page.getContentData());
        this.title = title;
        this.outgoingLinks = outgoingLinks;
    }

    public ParsedPage(ParsedPage page) {
        super(page.getUrl(), page.getContentData());
        this.title = page.getTitle();
        this.outgoingLinks = page.getOutgoingLinks();
        this.content = page.getContent();
    }

    public List<URL> getOutgoingLinks() {
        return outgoingLinks;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("url", getUrl())
                .add("title", title)
                .toString();
    }
}
