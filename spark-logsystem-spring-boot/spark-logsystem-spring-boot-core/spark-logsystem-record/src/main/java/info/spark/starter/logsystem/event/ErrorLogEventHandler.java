package info.spark.starter.logsystem.event;

import info.spark.starter.common.event.BaseEventHandler;
import info.spark.starter.common.event.EventEnum;
import info.spark.starter.logsystem.entity.ErrorLog;
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
 * <p>Description: 监听错误日志事件 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 09:29
 * @since 1.0.0
 */
@Slf4j
@Component
public class ErrorLogEventHandler extends BaseEventHandler<ErrorLogEvent> {
    /** Log storage factory */
    @Resource
    private LogStorageFactory logStorageFactory;

    /**
     * Handler *
     *
     * @param event event
     * @since 1.0.0
     */
    @Async
    @Order
    @Override
    @EventListener
    public void handler(@NotNull ErrorLogEvent event) {
        Map<String, Object> source = event.getSource();
        ErrorLog errorLog = (ErrorLog) source.get(EventEnum.EVENT_LOG.getName());
        LogRecordUtils.addOtherInfoToLog(errorLog);

        ILogStorage<ErrorLog> logStorage = this.logStorageFactory.getErrorLogStorage();

        LogRecordUtils.save(errorLog, logStorage);
    }

}
