package ru.skuptsov.robot.crawler.url;

import javax.validation.constraints.NotNull;
import java.net.URL;

/**
 * @author Sergey Kuptsov
 * @since 18/03/2017
 */
public class URLCanonicalizer {
    public static String getCanonicalURL(@NotNull URL url) {
        return url.toExternalForm().toLowerCase();
    }
}
