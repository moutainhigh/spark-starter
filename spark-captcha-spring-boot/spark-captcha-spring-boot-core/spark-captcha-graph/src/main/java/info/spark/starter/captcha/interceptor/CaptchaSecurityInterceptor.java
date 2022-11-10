package info.spark.starter.captcha.interceptor;

import info.spark.starter.captcha.enhancer.CaptchaCheckEnhancer;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 请求拦截器, 拦截业务端配置需要验证码的请求, 用于统计计数
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.21 20:43
 * @since 1.0.0
 */
@Slf4j
public class CaptchaSecurityInterceptor extends HandlerInterceptorAdapter {

    /** Enhancers */
    private final List<CaptchaCheckEnhancer> enhancers;

    /**
     * Captcha interceptor
     *
     * @param enhancers enhancers
     * @since 1.0.0
     */
    public CaptchaSecurityInterceptor(List<CaptchaCheckEnhancer> enhancers) {
        this.enhancers = enhancers;
    }

    /**
     * preHandle 方法是进行处理器拦截用的, 该方法将在 Controller 处理之前进行调用,
     * SpringMVC 中的 Interceptor 拦截器是链式的, 可以同时存在多个 Interceptor,
     * 然后 SpringMVC 会根据声明的前后顺序一个接一个的执行,
     * 而且所有的 Interceptor 中的 preHandle 方法都会在 Controller 方法调用之前调用.
     * SpringMVC 的这种 Interceptor 链式结构也是可以进行中断的,
     * 这种中断方式是令 preHandle 的返回值为 false, 当 preHandle 的返回值为 false 的时候整个请求就结束了.
     *
     * @param request  the request
     * @param response the response
     * @param handler  the handler
     * @return the boolean
     * @throws Exception the exception
     * @since 1.0.0
     */
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) throws Exception {
        log.debug("invoked preHandle");
        return true;
    }

    /**
     * 这个方法只会在当前这个 Interceptor 的 preHandle 方法返回值为 true 的时候才会执行.
     * postHandle 是进行处理器拦截用的, 它的执行时间是在 Controller 的方法调用之后执行,
     * 但是它会在 DispatcherServlet 进行视图的渲染之前执行, 也就是说在这个方法中可以对 ModelAndView 进行操作.
     * 这个方法的链式结构跟正常访问的方向是相反的, 也就是说先声明的 Interceptor 拦截器该方法反而会后调用,
     *
     * @param request      the request
     * @param response     the response
     * @param handler      the handler
     * @param modelAndView the model and view
     * @throws Exception the exception
     * @since 1.0.0
     */
    @Override
    public void postHandle(@NotNull HttpServletRequest request,
                           @NotNull HttpServletResponse response,
                           @NotNull Object handler,
                           ModelAndView modelAndView) throws Exception {
        log.debug("invoked postHandle");
    }

    /**
     * 该方法也是需要当前对应的 Interceptor 的 preHandle 方法的返回值为 true 时才会执行.
     * 该方法将在整个请求完成之后, 也就是 DispatcherServlet 渲染了视图执行, 这个方法的主要作用是用于清理资源的,
     *
     * @param request  the request
     * @param response the response
     * @param handler  the handler
     * @param ex       the ex
     * @throws Exception the exception
     * @since 1.0.0
     */
    @Override
    public void afterCompletion(@NotNull HttpServletRequest request,
                                @NotNull HttpServletResponse response,
                                @NotNull Object handler, Exception ex) throws Exception {
        log.debug("invoked afterCompletion");
        this.enhancers.forEach(e -> e.counter(request, response, handler, ex));
    }

}
