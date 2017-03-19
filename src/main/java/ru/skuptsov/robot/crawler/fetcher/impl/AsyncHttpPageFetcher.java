package ru.skuptsov.robot.crawler.fetcher.impl;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.skuptsov.robot.configuration.Configurable;
import ru.skuptsov.robot.configuration.Configuration;
import ru.skuptsov.robot.crawler.fetcher.PageFetcher;
import ru.skuptsov.robot.exception.CrawlPageFetchException;
import ru.skuptsov.robot.model.Page;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Sergey Kuptsov
 * @since 17/03/2017
 */
public class AsyncHttpPageFetcher extends Configurable<Configuration> implements PageFetcher {
    private final static Logger log = LoggerFactory.getLogger(AsyncHttpPageFetcher.class);

    public AsyncHttpPageFetcher(Configuration configuration) {
        super(configuration);
    }

    public Page get(@NotNull URL url) {
        log.debug("Start downloading content of [{}]", url);

        Response response;
        try {
            response = getHttpClient().executeRequest(
                    getHttpClient().prepareGet(url.toExternalForm()).build()
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new CrawlPageFetchException("Can't download page by url : " + url, e);
        }

        checkArgument(!is2XX(response.getStatusCode()), "Status code of page is not 2XX");

        Page page;
        try {
            page = new Page(url, response.getResponseBodyAsBytes());
            page.setContentType(response.getContentType());
        } catch (IOException e) {
            throw new CrawlPageFetchException("Can't process response data : " + url, e);
        }

        log.debug("Finished downloading content of [{}]", url);

        return page;
    }

    private boolean is2XX(int statusCode) {
        return statusCode / 100 != 2;
    }

    private AsyncHttpClient getHttpClient() {
        return getConfig().getHttpClient();
    }
}
