package info.spark.starter.captcha.enhancer;

import info.spark.starter.captcha.filter.CaptchaCodeFilter;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 请求频率检查: 直接在 {@link CaptchaCodeFilter} 进行计数器自增 </p>
 * todo-dong4j : (2020年02月21日 21:38) [重设过期时间, key 生成策略]
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.21 19:22
 * @since 1.0.0
 */
@Slf4j
public class FrequencyCheckEnhancer extends AbstractCaptchaCheckEnhancer {

    /**
     * Abstract captcha check enhancer
     *
     * @param limit limit
     * @since 1.0.0
     */
    public FrequencyCheckEnhancer(Integer limit) {
        super(limit);
    }

    /**
     * 接口调用次数计数
     *
     * @param request  request
     * @param response response
     * @param handler  handler
     * @param ex       ex
     * @since 1.0.0
     */
    @Override
    public void counter(@NotNull HttpServletRequest request,
                        @NotNull HttpServletResponse response,
                        @NotNull Object handler,
                        Exception ex) {

        log.info("{} {} {} {}", request, response, handler, ex);
        // 请求成功才增加次数
        if (response.getStatus() == HttpStatus.OK.value() && ex == null) {
            log.info("接口请求次数 + 1");
            this.increment(request.getRequestURI() + request.getRemoteUser());
        }
    }
}
