
package org.slf4j.impl;

import org.jetbrains.annotations.Contract;
import org.slf4j.IMarkerFactory;
import org.slf4j.MarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.spi.MarkerFactoryBinder;

/**
 * The binding of {@link MarkerFactory} class with an actual instance of
 * {@link IMarkerFactory} is performed using information returned by this class.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.08 11:49
 * @since 1.0.0
 */
public final class StaticMarkerBinder implements MarkerFactoryBinder {

    /**
     * The unique instance of this class.
     */
    private static final StaticMarkerBinder SINGLETON = new StaticMarkerBinder();

    /** Marker factory */
    private final IMarkerFactory markerFactory = new BasicMarkerFactory();

    /**
     * Static marker binder
     *
     * @since 1.0.0
     */
    private StaticMarkerBinder() {
    }

    /**
     * Return the singleton of this class.
     *
     * @return the StaticMarkerBinder singleton
     * @since 1.7.14
     */
    @Contract(pure = true)
    public static StaticMarkerBinder getSingleton() {
        return SINGLETON;
    }

    /**
     * Currently this method always returns an instance of
     * {@link BasicMarkerFactory}.
     *
     * @return the marker factory
     * @since 1.0.0
     */
    @Override
    public IMarkerFactory getMarkerFactory() {
        return this.markerFactory;
    }

    /**
     * Currently, this method returns the class name of
     * {@link BasicMarkerFactory}.
     *
     * @return the marker factory class str
     * @since 1.0.0
     */
    @Override
    public String getMarkerFactoryClassStr() {
        return BasicMarkerFactory.class.getName();
    }

}
