package info.spark.starter.logsystem.publisher;

import com.google.common.collect.Maps;

import info.spark.starter.common.context.SpringContext;
import info.spark.starter.common.event.EventEnum;
import info.spark.starter.core.util.WebUtils;
import info.spark.starter.logsystem.entity.SystemLog;
import info.spark.starter.logsystem.enums.OperationAction;
import info.spark.starter.logsystem.event.SystemLogEvent;
import info.spark.starter.logsystem.util.LogRecordUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * 系统日志信息事件发送
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 10:04
 * @since 1.0.0
 */
@Slf4j
public class SystemLogPublisher {

    /**
     * Publish event *
     *
     * @param operationAction operation action
     * @param operationName   operationName
     * @since 1.0.0
     */
    public static void publishEvent(@NotNull OperationAction operationAction, String operationName) {
        HttpServletRequest request = WebUtils.getRequest();
        SystemLog systemLog = SystemLog.builder().operationName(operationName).build();
        systemLog.setOperationAction(operationAction.getCode());
        LogRecordUtils.addRequestInfoToLog(request, systemLog);

        Map<String, Object> event = Maps.newHashMapWithExpectedSize(2);
        event.put(EventEnum.EVENT_LOG.getName(), systemLog);
        event.put(EventEnum.EVENT_REQUEST.getName(), request);
        log.debug("发送保存操作日志事件. [{}]", systemLog);
        SpringContext.publishEvent(new SystemLogEvent(event));
    }

}
