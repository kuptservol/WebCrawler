package ru.skuptsov.robot.command.concrete;

import ru.skuptsov.robot.crawler.Crawler;
import ru.skuptsov.robot.configuration.Configuration;

import javax.validation.constraints.NotNull;
import java.net.URL;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Sergey Kuptsov
 * @since 15/03/2017
 */
public class CrawlerCommand extends BaseCommand {

    private final URL startUrl;
    private final int depth;
    private final Configuration configuration;

    public CrawlerCommand(@NotNull URL startUrl, int depth, @NotNull Configuration configuration) {
        checkNotNull(startUrl);
        checkNotNull(configuration);
        this.startUrl = startUrl;
        this.depth = depth;
        this.configuration = configuration;
    }

    @Override
    protected void execute() {
        Crawler crawler = new Crawler(configuration);
        crawler.process(startUrl, depth);
    }

    @Override
    public void destroy() {
        configuration.destroy();
    }
}
