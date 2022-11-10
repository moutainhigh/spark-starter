package info.spark.starter.logsystem.constant;

import info.spark.starter.basic.constant.ConfigDefaultValue;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:20
 * @since 1.0.0
 */
public final class LogSystem {

    /** 日志默认状态 */
    public static final String LOG_NORMAL_TYPE = "1";
    /** MODULE_NAME */
    public static final String MODULE_NAME = "spark-starter-logsystem";
    /** DEFAULT_LOGGING_LOCATION */
    public static final String DEFAULT_LOGGING_LOCATION = ConfigDefaultValue.DEFAULT_LOGGING_LOCATION;
    /** MARKER_PROPERTIES */
    public static final String MARKER_PROPERTIES = Marker.PROCESSOR.name().toLowerCase();
    /** MARKER_PROCESSOR */
    public static final String MARKER_PROCESSOR = Marker.PROPERTIES.name().toLowerCase();

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.03.08 13:37
     * @since 1.0.0
     */
    public enum Marker {
        /** Banner marker */
        BANNER,
        /** Properties marker */
        PROPERTIES,
        /** Processor marker */
        PROCESSOR
    }
}
