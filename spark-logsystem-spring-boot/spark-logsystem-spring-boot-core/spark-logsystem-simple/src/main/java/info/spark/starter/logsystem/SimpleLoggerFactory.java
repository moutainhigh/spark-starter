package info.spark.starter.logsystem;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * An implementation of {@link ILoggerFactory} which always returns
 * {@link SimpleLogger} instances.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.08 11:46
 * @since 1.0.0
 */
public class SimpleLoggerFactory implements ILoggerFactory {

    /** Logger map */
    private final ConcurrentMap<String, Logger> loggerMap;

    /**
     * Simple logger factory
     *
     * @since 1.0.0
     */
    public SimpleLoggerFactory() {
        this.loggerMap = new ConcurrentHashMap<>();
        SimpleLogger.lazyInit();
    }

    /**
     * Return an appropriate {@link SimpleLogger} instance by name.
     *
     * @param name name
     * @return the logger
     * @since 1.0.0
     */
    @Override
    public Logger getLogger(String name) {
        Logger simpleLogger = this.loggerMap.get(name);
        if (simpleLogger != null) {
            return simpleLogger;
        } else {
            Logger newInstance = new SimpleLogger(name);
            Logger oldInstance = this.loggerMap.putIfAbsent(name, newInstance);
            return oldInstance == null ? newInstance : oldInstance;
        }
    }

    /**
     * Clear the internal logger cache.
     * <p>
     * This method is intended to be called by classes (in the same package) for
     * testing purposes. This method is internal. It can be modified, renamed or
     * removed at any time without notice.
     * <p>
     * You are strongly discouraged from calling this method in production code.
     *
     * @since 1.0.0
     */
    void reset() {
        this.loggerMap.clear();
    }
}
