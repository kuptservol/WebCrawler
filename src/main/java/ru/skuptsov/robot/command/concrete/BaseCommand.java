package ru.skuptsov.robot.command.concrete;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import ru.skuptsov.robot.command.Command;

import java.util.UUID;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author Sergey Kuptsov
 * @since 15/03/2017
 */
public abstract class BaseCommand implements Command {
    private final static Logger log = LoggerFactory.getLogger(BaseCommand.class);
    private final static String COMMAND_ID = "commandId";

    @Override
    public void run() {
        setLoggingContext();
        log.info("Starting executing command [{}]", this.getClass().getName());

        Stopwatch watch = Stopwatch.createStarted();

        try {
            execute();
        } catch (Exception ex) {
            log.error("Error occured while executing command [{}]", this.getClass().getName(), ex);
        } finally {
            log.debug("Destroying command");
            destroy();
        }

        watch.stop();

        log.info("Executing command [{}] finished. Elapsed time : [{}] ms",
                this.getClass().getName(), watch.elapsed(MILLISECONDS));
    }

    protected abstract void execute();

    private void setLoggingContext() {
        MDC.put(COMMAND_ID, getCommandExecutionId());
    }

    protected String getCommandExecutionId() {
        return UUID.randomUUID().toString();
    }
}
