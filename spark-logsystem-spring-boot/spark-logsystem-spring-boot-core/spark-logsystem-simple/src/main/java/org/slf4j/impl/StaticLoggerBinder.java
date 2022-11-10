
package org.slf4j.impl;

import info.spark.starter.logsystem.SimpleLoggerFactory;

import org.jetbrains.annotations.Contract;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

/**
 * The binding of {@link LoggerFactory} class with an actual instance of
 * {@link ILoggerFactory} is performed using information returned by this class.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.08 11:48
 * @since 1.0.0
 */
public final class StaticLoggerBinder implements LoggerFactoryBinder {

    /**
     * The unique instance of this class.
     */
    private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();

    /**
     * Return the singleton of this class.
     *
     * @return the StaticLoggerBinder singleton
     * @since 1.0.0
     */
    @Contract(pure = true)
    public static StaticLoggerBinder getSingleton() {
        return SINGLETON;
    }

    /** LOGGER_FACTORY_CLASS_STR */
    private static final String LOGGER_FACTORY_CLASS_STR = SimpleLoggerFactory.class.getName();

    /**
     * The ILoggerFactory instance returned by the {@link #getLoggerFactory}
     * method should always be the same object
     */
    private final ILoggerFactory loggerFactory;

    /**
     * Static logger binder
     *
     * @since 1.0.0
     */
    private StaticLoggerBinder() {
        this.loggerFactory = new SimpleLoggerFactory();
    }

    /**
     * Gets logger factory *
     *
     * @return the logger factory
     * @since 1.0.0
     */
    @Override
    public ILoggerFactory getLoggerFactory() {
        return this.loggerFactory;
    }

    /**
     * Gets logger factory class str *
     *
     * @return the logger factory class str
     * @since 1.0.0
     */
    @Override
    public String getLoggerFactoryClassStr() {
        return LOGGER_FACTORY_CLASS_STR;
    }
}
