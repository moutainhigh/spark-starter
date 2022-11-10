package info.spark.starter.openness.filter;

import info.spark.starter.basic.AbstractSkipFilter;
import info.spark.starter.openness.handler.IBucketListHandler;
import info.spark.starter.openness.annotations.Openness;
import info.spark.starter.openness.constant.Constant;
import info.spark.starter.openness.entity.HandlerEntry;
import info.spark.starter.openness.exception.OpennessErrorCodes;
import info.spark.starter.openness.handler.IResourceAclHandler;
import info.spark.starter.openness.service.AbstractOpennessAlgorithm;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.StopWatch;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 1.三方用户接口调用 鉴权以及统计处理；2.回调方法白名单token处理 </p>
 *
 * @author liujintao
 * @version 1.6.0
 * @email "mailto:liujintao@gmail.com"
 * @date 2020.07.23 17:15
 * @since 1.6.0
 */
@Slf4j
public class OpennessFilter extends AbstractSkipFilter {
    /** Request mapping handler mapping */
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    /** Openness client auth service */
    private final AbstractOpennessAlgorithm opennessAlgorithmService;
    /** 资源 acl handler */
    private final IResourceAclHandler resourceAclHandler;
    /** 黑白名单规则 handler */
    private final IBucketListHandler bucketListHandler;

    /**
     * Openness filter
     *
     * @param requestMappingHandlerMapping request mapping handler mapping
     * @param opennessAlgorithmService     openness client auth service
     * @param resourceAclHandler           resource acl handler
     * @param bucketListHandler            bucket list handler
     * @since 1.6.0
     */
    public OpennessFilter(RequestMappingHandlerMapping requestMappingHandlerMapping,
                          AbstractOpennessAlgorithm opennessAlgorithmService,
                          IResourceAclHandler resourceAclHandler,
                          IBucketListHandler bucketListHandler) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        this.opennessAlgorithmService = opennessAlgorithmService;
        this.resourceAclHandler = resourceAclHandler;
        this.bucketListHandler = bucketListHandler;
    }

    /**
     * Do filter internal
     *
     * @param httpServletRequest  http servlet request
     * @param httpServletResponse http servlet response
     * @param filterChain         filter chain
     * @throws ServletException servlet exception
     * @throws IOException      io exception
     * @since 1.6.0
     */
    @Override
    @SuppressWarnings("InnerAssignment")
    protected void doFilterInternal(@NotNull HttpServletRequest httpServletRequest,
                                    @NotNull HttpServletResponse httpServletResponse,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        HandlerExecutionChain handler = this.findHandlerExecuteChain(httpServletRequest, httpServletResponse, filterChain);
        if (null == handler) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        HandlerMethod handlerMethod;
        // 如果标识 开发此 API 则需要进行 验签
        if (this.shouldIntercept((handlerMethod = (HandlerMethod) handler.getHandler()))) {
            StopWatch timer = new StopWatch();
            timer.start();

            // 核心处理，不需要 catch，由顶层filter重定向到error处理器，进行包装
            this.coreSteps(httpServletRequest, httpServletResponse, handlerMethod);

            // 鉴权成功，放行，执行业务逻辑
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            // 打印执行结果
            this.executeEfficiency(httpServletRequest, httpServletResponse, timer);
            return;
        }
        // 未配置 @OpenApi 注解，不拦截
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    /**
     * 步骤
     *
     * @param httpServletRequest  http servlet request
     * @param httpServletResponse http servlet response
     * @param handlerMethod       handler method
     * @since 1.9.0
     */
    private void coreSteps(@NotNull HttpServletRequest httpServletRequest,
                           @NotNull HttpServletResponse httpServletResponse,
                           HandlerMethod handlerMethod) {
        // 黑白名单检验
        if (!this.bucketListHandler.adopt(httpServletRequest.getHeader(Constant.HEADER_CLIENT_ID))
            // ACL检验
            || !this.resourceAclHandler.allow(httpServletRequest.getHeader(Constant.HEADER_CLIENT_ID), httpServletRequest)) {
            throw OpennessErrorCodes.NO_PERMISSION.newException();
        }

        // 参数解析，普通检验
        HandlerEntry handlerEntry = this.opennessAlgorithmService.checkParams(httpServletRequest);

        // 验证签名、验证参数是否被篡改
        this.opennessAlgorithmService.checkSign(httpServletRequest, handlerEntry);

        // 验证是否正常请求: 请求时间、是否请求重复
        this.opennessAlgorithmService.preventReplay(handlerEntry.getNonce(), handlerEntry.getTimestamp());
    }

    /**
     * Should intercept
     *
     * @param handlerMethod handler method
     * @return the boolean
     * @since 1.9.0
     */
    private boolean shouldIntercept(HandlerMethod handlerMethod) {
        Openness openness = handlerMethod.getMethod().getAnnotation(Openness.class);
        Openness classOpenness = handlerMethod.getBeanType().getAnnotation(Openness.class);
        return null != openness || null != classOpenness;
    }

    /**
     * Find handler execute chain
     *
     * @param httpServletRequest  http servlet request
     * @param httpServletResponse http servlet response
     * @param filterChain         filter chain
     * @return the handler execution chain
     * @since 1.9.0
     */
    @Nullable
    private HandlerExecutionChain findHandlerExecuteChain(@NotNull HttpServletRequest httpServletRequest,
                                                          @NotNull HttpServletResponse httpServletResponse,
                                                          @NotNull FilterChain filterChain) {
        HandlerExecutionChain handler;
        try {
            handler = this.requestMappingHandlerMapping.getHandler(httpServletRequest);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw OpennessErrorCodes.ROUTE_ERROR.newException(e.getMessage());
        }
        return handler;
    }

    /**
     * Execute efficiency
     *
     * @param httpServletRequest  http servlet request
     * @param httpServletResponse http servlet response
     * @param timer               timer
     * @since 1.9.0
     */
    private void executeEfficiency(@NotNull HttpServletRequest httpServletRequest,
                                   @NotNull HttpServletResponse httpServletResponse,
                                   StopWatch timer) {
        try {
            timer.stop();
            log.info("请求: {}, 耗时: {}ms",
                     httpServletRequest.getRequestURI(),
                     timer.getTotalTimeMillis());
        } catch (Exception e) {
            log.warn("open-api：执行效率统计异常：{}", e.getMessage());
        }
    }

}
