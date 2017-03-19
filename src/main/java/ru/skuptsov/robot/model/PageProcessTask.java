package ru.skuptsov.robot.model;

import com.google.common.base.MoreObjects;

import javax.validation.constraints.NotNull;
import java.net.URL;

import static com.google.common.base.Preconditions.checkNotNull;
import static ru.skuptsov.robot.model.PageProcessTask.PageProcessTaskBuilder.pageProcessTask;

/**
 * @author Sergey Kuptsov
 * @since 17/03/2017
 */
public class PageProcessTask {
    private URL pageUrl;
    private Integer depth;
    private Integer maxDepth;

    public URL getPageUrl() {
        return pageUrl;
    }

    public int getDepth() {
        return depth;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public static PageProcessTask nextTask(@NotNull PageProcessTask pageProcessTask, @NotNull URL nextURL) {
        return pageProcessTask()
                .withPageUrl(nextURL)
                .withDepth(pageProcessTask.depth + 1)
                .withMaxDepth(pageProcessTask.getMaxDepth())
                .build();
    }

    public static class PageProcessTaskBuilder {
        private PageProcessTask pageProcessTask;

        private PageProcessTaskBuilder() {
            pageProcessTask = new PageProcessTask();
        }

        public PageProcessTaskBuilder withPageUrl(URL pageUrl) {
            pageProcessTask.pageUrl = pageUrl;
            return this;
        }

        public PageProcessTaskBuilder withDepth(int depth) {
            pageProcessTask.depth = depth;
            return this;
        }

        public PageProcessTaskBuilder withMaxDepth(int maxDepth) {
            pageProcessTask.maxDepth = maxDepth;
            return this;
        }

        public static PageProcessTaskBuilder pageProcessTask() {
            return new PageProcessTaskBuilder();
        }

        public PageProcessTask build() {
            checkNotNull(pageProcessTask.depth);
            checkNotNull(pageProcessTask.pageUrl);
            return pageProcessTask;
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("pageUrl", pageUrl)
                .add("depth", depth)
                .add("maxDepth", maxDepth)
                .toString();
    }
}
