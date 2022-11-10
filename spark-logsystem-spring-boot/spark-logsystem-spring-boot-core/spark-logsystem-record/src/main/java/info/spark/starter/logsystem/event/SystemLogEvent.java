package info.spark.starter.logsystem.event;

import info.spark.starter.common.event.BaseEvent;

import java.util.Map;

/**
 * <p>Description: 系统日志事件 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 10:07
 * @since 1.0.0
 */
public class SystemLogEvent extends BaseEvent<Map<String, Object>> {

    /** serialVersionUID */
    private static final long serialVersionUID = -3789524764345821072L;

    /**
     * System log event
     *
     * @param source source
     * @since 1.0.0
     */
    public SystemLogEvent(Map<String, Object> source) {
        super(source);
    }

}
