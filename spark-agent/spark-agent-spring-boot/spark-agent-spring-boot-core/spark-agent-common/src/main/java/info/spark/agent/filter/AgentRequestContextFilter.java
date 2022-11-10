package info.spark.agent.filter;

import info.spark.starter.basic.context.AgentRequestContextHolder;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>Description: </p>处理多线程环境下注入到 Controller 的 HttpServletRequest 不可用的问题
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.03.11 11:30
 * @since 1.8.0
 */
public class AgentRequestContextFilter extends OncePerRequestFilter {

    /**
     * Should not filter async dispatch
     *
     * @return the boolean
     * @since 1.8.0
     */
    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    /**
     * Should not filter error dispatch
     *
     * @return the boolean
     * @since 1.8.0
     */
    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }

    /**
     * Do filter internal
     *
     * @param request     request
     * @param response    response
     * @param filterChain filter chain
     * @throws ServletException servlet exception
     * @throws IOException      io exception
     * @since 1.8.0
     */
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        ServletRequestAttributes attributes = new ServletRequestAttributes(request, response);
        this.initContextHolders(request, attributes);

        try {
            filterChain.doFilter(request, response);
        } finally {
            this.resetContextHolders();
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Cleared thread-bound request context: " + request);
            }
            attributes.requestCompleted();
        }
    }

    /**
     * Init context holders
     *
     * @param request           request
     * @param requestAttributes request attributes
     * @since 1.8.0
     */
    private void initContextHolders(HttpServletRequest request, ServletRequestAttributes requestAttributes) {
        AgentRequestContextHolder.setRequestAttributes(requestAttributes);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Bound request context to thread: " + request);
        }
    }

    /**
     * Reset context holders
     *
     * @since 1.8.0
     */
    private void resetContextHolders() {
        AgentRequestContextHolder.resetRequestAttributes();
    }

}
