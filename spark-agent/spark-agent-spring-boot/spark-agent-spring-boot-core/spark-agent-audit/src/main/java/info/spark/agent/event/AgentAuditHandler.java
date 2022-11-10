package info.spark.agent.event;

import info.spark.agent.entity.AgentAuditLog;
import info.spark.agent.factory.LogStorageFactory;
import info.spark.agent.storage.ILogStorage;
import info.spark.agent.util.LogRecordUtils;
import info.spark.starter.common.event.BaseEventHandler;
import info.spark.starter.common.event.EventEnum;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;

import java.util.Map;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 异步监听日志事件 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 10:08
 * @since 1.6.0
 */
@Slf4j
public class AgentAuditHandler extends BaseEventHandler<AgentAuditEvent> {
    /** Log storage factory */
    @Resource
    private LogStorageFactory logStorageFactory;

    /**
     * Handler *
     *
     * @param event event
     * @since 1.6.0
     */
    @Async
    @Order
    @Override
    @EventListener
    public void handler(@NotNull AgentAuditEvent event) {
        Map<String, AgentAuditLog> source = event.getSource();
        AgentAuditLog agentAuditLog = source.get(EventEnum.EVENT_LOG.getName());
        LogRecordUtils.addOtherInfoToLog(agentAuditLog);
        ILogStorage<AgentAuditLog> logStorage = this.logStorageFactory.getAgentAuditLogStorage();
        LogRecordUtils.save(agentAuditLog, logStorage);
    }
}
