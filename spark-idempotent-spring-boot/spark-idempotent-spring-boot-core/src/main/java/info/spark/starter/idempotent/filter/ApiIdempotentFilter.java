package info.spark.starter.idempotent.filter;

import info.spark.starter.basic.AbstractSkipFilter;
import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.idempotent.annotations.ApiIdempotent;
import info.spark.starter.idempotent.service.TokenService;
import info.spark.starter.util.core.api.R;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author liujintao
 * @version 1.0.0
 * @email "mailto:liujintao@gmail.com"
 * @date 2020.07.22 10:04
 * @since 1.0.0
 */
@Slf4j
public class ApiIdempotentFilter extends AbstractSkipFilter {
    /** Request mapping handler mapping */
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    /** Token service */
    private final TokenService tokenService;

    /**
     * Api idempotent filter
     *
     * @param requestMappingHandlerMapping request mapping handler mapping
     * @param tokenService                 tokenService
     * @since 1.0.0
     */
    public ApiIdempotentFilter(RequestMappingHandlerMapping requestMappingHandlerMapping,
                               TokenService tokenService) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        this.tokenService = tokenService;
    }

    /**
     * Do filter internal
     *
     * @param httpServletRequest  http servlet request
     * @param httpServletResponse http servlet response
     * @param filterChain         filter chain
     * @throws ServletException servlet exception
     * @throws IOException      io exception
     * @since 1.0.0
     */
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest httpServletRequest,
                                    @NotNull HttpServletResponse httpServletResponse,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        HandlerExecutionChain handler;
        try {
            handler = this.requestMappingHandlerMapping.getHandler(httpServletRequest);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            httpServletResponse.getWriter().write(JsonUtils.toJson(e.getMessage()));
            return;
        }

        if (handler == null) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler.getHandler();
        ApiIdempotent idempotent = handlerMethod.getMethod().getAnnotation(ApiIdempotent.class);

        if (idempotent != null) {
            String method = httpServletRequest.getMethod();
            boolean needProcess = method.equals(HttpMethod.PATCH.name())
                                  || method.equals(HttpMethod.POST.name())
                                  || method.equals(HttpMethod.PUT.name());
            if (!needProcess) {
                log.warn("{} 不需要幂等性处理, 请删除 @ApiIdempotent 标识", handler.getHandler());
                filterChain.doFilter(httpServletRequest, httpServletResponse);
                return;
            }

            try {
                this.tokenService.checkToken(httpServletRequest);
            } catch (Exception e) {
                log.warn("{} 幂等验证: {}", handler.getHandler(), e.getMessage());
                PrintWriter out = httpServletResponse.getWriter();
                out.write(JsonUtils.toJson(R.failed(e.getMessage())));
                return;
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);

    }
}
