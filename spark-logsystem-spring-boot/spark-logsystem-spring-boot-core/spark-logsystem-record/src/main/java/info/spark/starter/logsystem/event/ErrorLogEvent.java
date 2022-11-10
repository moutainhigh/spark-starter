package info.spark.starter.logsystem.event;


import info.spark.starter.common.event.BaseEvent;

import java.util.Map;

/**
 * <p>Description: 错误日志事件 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 09:28
 * @since 1.0.0
 */
public class ErrorLogEvent extends BaseEvent<Map<String, Object>> {

    /** serialVersionUID */
    private static final long serialVersionUID = -1076180722459253421L;

    /**
     * Error log event
     *
     * @param source source
     * @since 1.0.0
     */
    public ErrorLogEvent(Map<String, Object> source) {
        super(source);
    }

}
