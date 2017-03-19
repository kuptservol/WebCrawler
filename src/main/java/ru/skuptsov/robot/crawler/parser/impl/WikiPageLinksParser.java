package ru.skuptsov.robot.crawler.parser.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.skuptsov.robot.crawler.parser.PageParser;
import ru.skuptsov.robot.model.Page;
import ru.skuptsov.robot.model.ParsedPage;

import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.copyOf;
import static java.lang.String.format;
import static java.util.regex.Pattern.compile;

/**
 * @author Sergey Kuptsov
 * @since 17/03/2017
 */
public class WikiPageLinksParser implements PageParser {
    private final static Logger log = LoggerFactory.getLogger(WikiPageLinksParser.class);
    private static final String WIKI_FORMAT = "/wiki/%s";
    private final Pattern linksPattern = compile("<a href=\"\\/wiki\\/(?<link>[\\w%,]+)\".*title=\".+\">.+<\\/a>");
    private final Pattern titlePattern = compile("<title>(?<title>.+)<\\/title>");

    @Override
    public ParsedPage parse(@NotNull Page page) {
        checkNotNull(page);
        log.debug("Start to parse page [{}]", page);

        List<URL> outgoingLinks = new ArrayList<>();

        String content = new String(page.getContentData(), UTF_8);

        findAllOutgoingLinks(outgoingLinks, linksPattern.matcher(content), page.getUrl());

        String title = resolveTitle(titlePattern.matcher(content));

        ParsedPage parsedPage = new ParsedPage(page, title, copyOf(outgoingLinks));
        parsedPage.setContent(content);

        log.debug("Finished parsing page [{}] result [{}]", page, parsedPage);
        log.trace("Resolved outgoing links [{}]", outgoingLinks);

        return parsedPage;
    }

    private String resolveTitle(Matcher matcher) {
        if (matcher.find()) {
            return matcher.group("title");
        } else {
            log.info("Page title not found");
        }

        return null;
    }

    private void findAllOutgoingLinks(List<URL> outgoingLinks, Matcher matcher, URL pageUrl) {
        while (matcher.find()) {
            try {
                outgoingLinks.add(formWikiURL(matcher.group("link"), pageUrl));
            } catch (MalformedURLException e) {
                log.error("MalformedURL parsed with url {}", e);
            }
        }
    }

    private URL formWikiURL(String path, URL fromURL) throws MalformedURLException {
        return new URL(fromURL.getProtocol(), fromURL.getHost(), format(WIKI_FORMAT, path));
    }
}
