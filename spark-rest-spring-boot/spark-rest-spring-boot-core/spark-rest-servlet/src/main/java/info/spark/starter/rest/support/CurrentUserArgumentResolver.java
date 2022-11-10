package info.spark.starter.rest.support;

import info.spark.starter.auth.CurrentUser;
import info.spark.starter.auth.constant.AuthConstant;
import info.spark.starter.basic.exception.BasicException;
import info.spark.starter.rest.interceptor.CurrentUserInterceptor;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 注入 {@link CurrentUser} 到 controller </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.09.11 13:48
 * @see CurrentUserInterceptor
 * @since 1.6.0
 */
@Slf4j
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * Supports parameter
     *
     * @param parameter parameter
     * @return the boolean
     * @since 1.6.0
     */
    @Override
    public boolean supportsParameter(@NotNull MethodParameter parameter) {
        return CurrentUser.class.equals(parameter.getParameterType());
    }

    /**
     * Resolve argument
     *
     * @param parameter     parameter
     * @param modelAndView  model and view
     * @param webRequest    web request
     * @param binderFactory binder factory
     * @return the object
     * @since 1.6.0
     */
    @Override
    public Object resolveArgument(@NotNull MethodParameter parameter,
                                  ModelAndViewContainer modelAndView,
                                  @NotNull NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        Object currentUserInfo = webRequest.getAttribute(AuthConstant.CURRENT_LOGIN_USER, 0);
        if (null == currentUserInfo) {
            throw new BasicException("B.A-40001", "未授权");
        }
        return currentUserInfo;
    }

}
