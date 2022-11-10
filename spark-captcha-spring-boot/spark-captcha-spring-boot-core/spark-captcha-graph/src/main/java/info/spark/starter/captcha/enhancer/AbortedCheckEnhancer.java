package info.spark.starter.captcha.enhancer;

import info.spark.starter.basic.Result;
import info.spark.starter.basic.util.IoUtils;
import info.spark.starter.basic.util.JsonUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 请求失败检查: 需要动态设置 Interceptor, 拦截 配置的 filter uri, 或者使用注解 aop </p>
 * todo-dong4j : (2020年02月21日 21:38) [重设过期时间, key 生成策略]
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.21 19:21
 * @since 1.0.0
 */
@Slf4j
public class AbortedCheckEnhancer extends AbstractCaptchaCheckEnhancer {

    /**
     * Aborted check enhancer
     *
     * @param limit limit
     * @since 1.0.0
     */
    @Contract(pure = true)
    public AbortedCheckEnhancer(Integer limit) {
        super(limit);
    }

    /**
     * 接口调用失败次数计数
     *
     * @param request  request
     * @param response response
     * @param handler  handler
     * @param ex       ex
     * @since 1.0.0
     */
    @SneakyThrows
    @Override
    public void counter(@NotNull HttpServletRequest request,
                        @NotNull HttpServletResponse response,
                        @NotNull Object handler,
                        Exception ex) {

        log.info("{} {} {} {}", request, response, handler, ex);
        if (response.getStatus() != HttpStatus.OK.value() || ex != null) {
            this.increment(request.getRequestURI() + request.getRemoteUser());
            log.info("请求错误次数 + 1");
            return;
        }

        // todo-dong4j : (2020年02月24日 17:48) [有问题]
        if (response instanceof ContentCachingResponseWrapper) {
            ContentCachingResponseWrapper cacheResponseEnhanceWrapper = (ContentCachingResponseWrapper) response;

            String body = IoUtils.toString(cacheResponseEnhanceWrapper.getContentAsByteArray());

            Result<?> result = JsonUtils.parse(body, Result.class);

            if (Result.isFail(result)) {
                this.increment(request.getRequestURI() + request.getRemoteUser());
                log.info("请求错误次数 + 1");
            }
        }
    }
}
