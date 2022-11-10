package info.spark.starter.rest.interceptor;

import info.spark.starter.auth.CurrentUser;
import info.spark.starter.basic.exception.BasicException;
import info.spark.starter.rest.annotation.TokenRequired;
import info.spark.starter.rest.support.CurrentUserService;
import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * <p>Description: 使用注解代替接口注入的 CurrentUser, 避免 swagger 出现不必要的入参说明 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.09.29 00:02
 * @since 2.0.0
 */
@AllArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {
    /** Current user service */
    private final CurrentUserService currentUserService;


    /**
     * Pre handle
     *
     * @param request  request
     * @param response response
     * @param handler  handler
     * @return the boolean
     * @since 2.0.0
     */
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) {

        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        // 判断接口是否需要登录
        TokenRequired methodAnnotation = method.getAnnotation(TokenRequired.class);
        // 有 @TokenRequired 注解，需要认证
        if (methodAnnotation != null) {
            CurrentUser currentUser = this.currentUserService.getCurrentUser(request);
            if (null == currentUser) {
                throw new BasicException("B.A-40001", "未授权");
            }
        }
        return true;
    }
}

