
package org.slf4j.impl;

import org.jetbrains.annotations.Contract;
import org.slf4j.helpers.NOPMDCAdapter;
import org.slf4j.spi.MDCAdapter;

/**
 * This implementation is bound to {@link NOPMDCAdapter}.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.08 11:49
 * @since 1.0.0
 */
@SuppressWarnings("all")
public final class StaticMDCBinder {

    /**
     * The unique instance of this class.
     */
    private static final StaticMDCBinder SINGLETON = new StaticMDCBinder();

    /**
     * Static mdc binder
     *
     * @since 1.0.0
     */
    @Contract(pure = true)
    private StaticMDCBinder() {
    }

    /**
     * Return the singleton of this class.
     *
     * @return the StaticMDCBinder singleton
     * @since 1.7.14
     */
    @Contract(pure = true)
    public static StaticMDCBinder getSingleton() {
        return SINGLETON;
    }

    /**
     * Currently this method always returns an instance of
     * {@link StaticMDCBinder}.
     *
     * @return the mdca
     * @since 1.0.0
     */
    public MDCAdapter getMDCA() {
        return new NOPMDCAdapter();
    }

    /**
     * Gets mdc adapter class str *
     *
     * @return the mdc adapter class str
     * @since 1.0.0
     */
    public String getMDCAdapterClassStr() {
        return NOPMDCAdapter.class.getName();
    }
}
