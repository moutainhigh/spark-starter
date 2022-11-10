package info.spark.starter.mybatis.logger;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.FormattedLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Description:  </p>
 *
 * @author zhubo
 * @version 1.7.1
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.26 15:45
 * @since 1.7.1
 */
@SuppressWarnings("all")
public class SparkP6spySlf4jLogger extends FormattedLogger {

    /** Log */
    private Logger log;

    /**
     * Spark p6spy slf4j logger
     *
     * @since 1.7.1
     */
    public SparkP6spySlf4jLogger() {
        log = LoggerFactory.getLogger("p6spy");
    }

    /**
     * Log exception
     *
     * @param e e
     * @since 1.7.1
     */
    @Override
    public void logException(Exception e) {
        log.info("", e);
    }

    /**
     * Log text
     *
     * @param text text
     * @since 1.7.1
     */
    @Override
    public void logText(String text) {
        log.info(text);
    }

    /**
     * Log sql
     *
     * @param connectionId connection id
     * @param now          now
     * @param elapsed      elapsed
     * @param category     category
     * @param prepared     prepared
     * @param sql          sql
     * @param url          url
     * @since 1.7.1
     */
    @Override
    public void logSQL(int connectionId, String now, long elapsed,
                       Category category, String prepared, String sql, String url) {
        final String msg = strategy.formatMessage(connectionId, now, elapsed,
                                                  category.toString(), prepared, sql, url);

        if (Category.ERROR.equals(category)) {
            log.error(msg);
        } else if (Category.WARN.equals(category)) {
            log.warn(msg);
        } else if (Category.INFO.equals(category)) {
            log.info(msg);
        } else {
            log.debug(msg);
        }
    }

    /**
     * Is category enabled
     *
     * @param category category
     * @return the boolean
     * @since 1.7.1
     */
    @Override
    public boolean isCategoryEnabled(Category category) {
        if (Category.ERROR.equals(category)) {
            return log.isErrorEnabled();
        } else if (Category.WARN.equals(category)) {
            return log.isWarnEnabled();
        } else if (Category.INFO.equals(category)) {
            return log.isInfoEnabled();
        } else {
            return log.isDebugEnabled();
        }
    }
}
