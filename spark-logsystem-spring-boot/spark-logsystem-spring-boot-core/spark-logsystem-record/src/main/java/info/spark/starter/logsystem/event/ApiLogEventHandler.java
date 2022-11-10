package info.spark.starter.logsystem.event;

import info.spark.starter.common.event.BaseEventHandler;
import info.spark.starter.common.event.EventEnum;
import info.spark.starter.logsystem.entity.AbstractLog;
import info.spark.starter.logsystem.entity.ApiLog;
import info.spark.starter.logsystem.factory.LogStorageFactory;
import info.spark.starter.logsystem.storage.ILogStorage;
import info.spark.starter.logsystem.util.LogRecordUtils;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 异步监听日志事件 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 05:00
 * @since 1.0.0
 */
@Slf4j
@Component
public class ApiLogEventHandler extends BaseEventHandler<ApiLogEvent> {
    /** Log storage factory */
    @Resource
    private LogStorageFactory logStorageFactory;

    /**
     * 发送日志
     *
     * @param event event
     * @since 1.0.0
     */
    @Order
    @Async
    @Override
    @EventListener
    public void handler(@NotNull ApiLogEvent event) {
        Map<String, AbstractLog> source = event.getSource();
        ApiLog apiLog = (ApiLog) source.get(EventEnum.EVENT_LOG.getName());
        LogRecordUtils.addOtherInfoToLog(apiLog);

        ILogStorage<ApiLog> logStorage = this.logStorageFactory.getApiLogStorage();

        LogRecordUtils.save(apiLog, logStorage);
    }
}
