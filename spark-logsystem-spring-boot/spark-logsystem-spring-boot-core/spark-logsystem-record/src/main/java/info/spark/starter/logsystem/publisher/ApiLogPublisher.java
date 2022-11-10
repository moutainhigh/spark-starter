package info.spark.starter.logsystem.publisher;

import com.google.common.collect.Maps;

import info.spark.starter.auth.CurrentUser;
import info.spark.starter.auth.util.AuthUtils;
import info.spark.starter.auth.util.JwtUtils;
import info.spark.starter.basic.util.StringUtils;
import info.spark.starter.common.context.SpringContext;
import info.spark.starter.common.event.EventEnum;
import info.spark.starter.core.util.WebUtils;
import info.spark.starter.logsystem.constant.LogSystem;
import info.spark.starter.logsystem.entity.AbstractLog;
import info.spark.starter.logsystem.entity.ApiLog;
import info.spark.starter.logsystem.event.ApiLogEvent;
import info.spark.starter.logsystem.util.LogRecordUtils;
import info.spark.starter.logsystem.annotation.RestLog;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: API日志信息事件发送 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 05:38
 * @since 1.0.0
 */
@Slf4j
public class ApiLogPublisher {

    /**
     * Publish event *
     *
     * @param methodName  method name
     * @param methodClass method class
     * @param restLog     api log
     * @param time        time
     * @since 1.0.0
     */
    public static void publishEvent(String methodName,
                                    String methodClass,
                                    @NotNull RestLog restLog,
                                    long time) {
        HttpServletRequest request = WebUtils.getRequest();
        ApiLog logApi = ApiLog.builder()
            .type(LogSystem.LOG_NORMAL_TYPE)
            .title(restLog.value())
            .build();

        logApi.setType(LogSystem.LOG_NORMAL_TYPE);
        logApi.setTitle(restLog.value());
        logApi.setTime(time);
        logApi.setMethodClass(methodClass);
        logApi.setMethodName(methodName);
        LogRecordUtils.addRequestInfoToLog(request, logApi);

        // 从 token 获取用户信息, 如果 request 没有传 token 则 user 为 null
        String token = AuthUtils.getToken(request);
        if (StringUtils.isNotBlank(token)) {
            CurrentUser user = JwtUtils.PlayGround.getUser(token);
            logApi.setUser(user);
            if (user != null) {
                logApi.setCreateBy(user.getUsername());
            }
        }

        Map<String, AbstractLog> event = Maps.newHashMapWithExpectedSize(2);
        event.put(EventEnum.EVENT_LOG.getName(), logApi);
        log.debug("发送保存 API 日志事件. [{}]", logApi);
        SpringContext.publishEvent(new ApiLogEvent(event));
    }

}
