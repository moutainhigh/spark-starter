package info.spark.agent.event;

import info.spark.agent.entity.AgentAuditLog;
import info.spark.starter.common.event.BaseEvent;

import java.util.Map;

/**
 * <p>Description: 系统日志事件 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 10:07
 * @since 1.6.0
 */
public class AgentAuditEvent extends BaseEvent<Map<String, AgentAuditLog>> {

    /** serialVersionUID */
    private static final long serialVersionUID = -3789524764345821072L;

    /**
     * System log event
     *
     * @param source source
     * @since 1.6.0
     */
    public AgentAuditEvent(Map<String, AgentAuditLog> source) {
        super(source);
    }

}
