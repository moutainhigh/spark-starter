package info.spark.starter.logsystem.event;

import info.spark.starter.common.event.BaseEvent;
import info.spark.starter.logsystem.entity.AbstractLog;

import java.util.Map;

/**
 * <p>Description: 系统日志事件 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 04:59
 * @since 1.0.0
 */
public class ApiLogEvent extends BaseEvent<Map<String, AbstractLog>> {

    /** serialVersionUID */
    private static final long serialVersionUID = -4183669564491640060L;

    /**
     * Api log event
     *
     * @param source source
     * @since 1.0.0
     */
    public ApiLogEvent(Map<String, AbstractLog> source) {
        super(source);
    }

}
