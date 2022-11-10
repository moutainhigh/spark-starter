package info.spark.starter.logsystem.event;

import info.spark.starter.common.event.BaseEvent;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 10:26
 * @since 1.0.0
 */
public abstract class LogEvent extends BaseEvent<Object> {

    /** serialVersionUID */
    private static final long serialVersionUID = -4449996576925041248L;

    /**
     * Instantiates a new Base event.
     *
     * @param source the source
     * @since 1.0.0
     */
    public LogEvent(Object source) {
        super(source);
    }
}
