package ru.skuptsov.robot.crawler.filter.impl;

import ru.skuptsov.robot.crawler.filter.URLFilter;

import java.net.URL;
import java.util.regex.Pattern;

/**
 * @author Sergey Kuptsov
 * @since 18/03/2017
 */
public class WikiURLFilterImpl implements URLFilter {
    private final static Pattern wikiPagePattern = Pattern.compile("^(https|http):\\/\\/\\w+.wikipedia.org\\/wiki\\/.*+$");

    @Override
    public boolean test(URL url) {
        return wikiPagePattern.matcher(url.toString()).matches();
    }
}
