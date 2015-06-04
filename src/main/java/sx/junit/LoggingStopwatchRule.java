package sx.junit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AssumptionViolatedException;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;

import java.util.concurrent.TimeUnit;

/**
 * Created by masc on 14.04.15.
 */
public class LoggingStopwatchRule extends Stopwatch {
    private static final Log logger = LogFactory.getLog(LoggingStopwatchRule.class);

    private static void logInfo(Description description, String status, long nanos) {
        String testName = description.getDisplayName();
        logger.info(String.format("Test %s %s, spent %d ms",
                testName, status, TimeUnit.NANOSECONDS.toMillis(nanos)));
    }

    @Override
    protected void succeeded(long nanos, Description description) {
        logInfo(description, "succeeded", nanos);
    }

    @Override
    protected void failed(long nanos, Throwable e, Description description) {
        logInfo(description, "failed", nanos);
    }

    @Override
    protected void skipped(long nanos, AssumptionViolatedException e, Description description) {
        logInfo(description, "skipped", nanos);
    }

    @Override
    protected void finished(long nanos, Description description) {
        logInfo(description, "finished", nanos);
    }
}
