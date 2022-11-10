package info.spark.starter.logsystem.event;

import info.spark.starter.common.event.BaseEvent;

import java.util.Map;

/**
 * <p>Description: 日志等级改变事件 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.09.06 13:56
 * @since 1.6.0
 */
public class ChangeLogLevelEvent extends BaseEvent<Map<String, String>> {
    /** serialVersionUID */
    private static final long serialVersionUID = -7263310380223565799L;

    /**
     * Instantiates a new Base event.
     *
     * @param source the source
     * @since 1.6.0
     */
    public ChangeLogLevelEvent(Map<String, String> source) {
        super(source);
    }
}
