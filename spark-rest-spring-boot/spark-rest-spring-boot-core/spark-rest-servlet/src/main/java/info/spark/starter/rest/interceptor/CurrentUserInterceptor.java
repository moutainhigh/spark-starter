package info.spark.starter.rest.interceptor;

import info.spark.starter.auth.CurrentUser;
import info.spark.starter.auth.constant.AuthConstant;
import info.spark.starter.rest.advice.ResponseWrapperAdvice;
import info.spark.starter.rest.support.CurrentUserService;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: {@link CurrentUser} 处理 </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.02.04 11:36
 * @see ResponseWrapperAdvice
 * @since 1.0.0
 */
@Slf4j
@AllArgsConstructor
public class CurrentUserInterceptor implements HandlerInterceptor {
    /** Current user service */
    private final CurrentUserService currentUserService;

    /**
     * Pre handle
     *
     * @param request  request
     * @param response response
     * @param handler  handler
     * @return the boolean
     * @since 1.6.0
     */
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) {

        CurrentUser currentUser = this.currentUserService.getCurrentUser(request);
        if (currentUser != null) {
            request.setAttribute(AuthConstant.CURRENT_LOGIN_USER, currentUser);
        }

        return true;
    }
}
