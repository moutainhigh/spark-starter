package info.spark.starter.logsystem.publisher;

import com.google.common.collect.Maps;

import info.spark.starter.basic.util.Exceptions;
import info.spark.starter.common.context.SpringContext;
import info.spark.starter.common.event.EventEnum;
import info.spark.starter.logsystem.entity.ErrorLog;
import info.spark.starter.logsystem.event.ErrorLogEvent;
import info.spark.starter.logsystem.util.LogRecordUtils;
import info.spark.starter.util.ObjectUtils;
import info.spark.starter.core.util.UrlUtils;
import info.spark.starter.core.util.WebUtils;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Description: 异常信息事件发送 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 09:26
 * @since 1.0.0
 */
public class ErrorLogPublisher {

    /**
     * Publish event *
     *
     * @param error      error
     * @since 1.0.0
     */
    public static void publishEvent(Throwable error) {
        HttpServletRequest request = WebUtils.getRequest();
        ErrorLog errorLog = ErrorLog.builder().build();
        errorLog.setRequestUri(UrlUtils.getPath(request.getRequestURI()));
        if (ObjectUtils.isNotEmpty(error)) {
            errorLog.setStackTrace(Exceptions.getStackTraceAsString(error));
            errorLog.setExceptionName(error.getClass().getName());
            errorLog.setMessage(error.getMessage());
            StackTraceElement[] elements = error.getStackTrace();
            if (ObjectUtils.isNotEmpty(elements)) {
                StackTraceElement element = elements[0];
                errorLog.setMethodName(element.getMethodName());
                errorLog.setMethodClass(element.getClassName());
                errorLog.setFileName(element.getFileName());
                errorLog.setLineNumber(element.getLineNumber());
            }
        }
        LogRecordUtils.addRequestInfoToLog(request, errorLog);

        Map<String, Object> event = Maps.newHashMapWithExpectedSize(2);
        event.put(EventEnum.EVENT_LOG.getName(), errorLog);
        event.put(EventEnum.EVENT_REQUEST.getName(), request);
        SpringContext.publishEvent(new ErrorLogEvent(event));
    }

}
