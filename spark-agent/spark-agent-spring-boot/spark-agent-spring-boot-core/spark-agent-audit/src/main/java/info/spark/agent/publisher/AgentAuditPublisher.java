package info.spark.agent.publisher;

import com.google.common.collect.Maps;

import info.spark.agent.annotation.AgentAudit;
import info.spark.agent.entity.AgentAuditLog;
import info.spark.agent.event.AgentAuditEvent;
import info.spark.agent.util.LogRecordUtils;
import info.spark.starter.common.context.SpringContext;
import info.spark.starter.common.event.EventEnum;
import info.spark.starter.core.util.WebUtils;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * 系统日志信息事件发送
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 10:04
 * @since 1.6.0
 */
@Slf4j
@UtilityClass
public class AgentAuditPublisher {

    /**
     * Publish event
     *
     * @param methodName  method name
     * @param methodClass method class
     * @param agentAudit  agent audit
     * @param time        time
     * @since 1.6.0
     */
    public static void publishEvent(String methodName,
                                    String methodClass,
                                    AgentAudit agentAudit,
                                    long time) {
        HttpServletRequest request = WebUtils.getRequest();
        AgentAuditLog agentAuditLog = AgentAuditLog.builder()
            .title(agentAudit.value())
            .actionType(agentAudit.action())
            .build();

        agentAuditLog.setTime(time);
        agentAuditLog.setMethodClass(methodClass);
        agentAuditLog.setMethodName(methodName);

        LogRecordUtils.addRequestInfoToLog(request, agentAuditLog);
        Map<String, AgentAuditLog> event = Maps.newHashMapWithExpectedSize(2);
        event.put(EventEnum.EVENT_LOG.getName(), agentAuditLog);
        log.debug("发送保存审计日志事件. [{}]", agentAuditLog);
        SpringContext.publishEvent(new AgentAuditEvent(event));
    }

}
