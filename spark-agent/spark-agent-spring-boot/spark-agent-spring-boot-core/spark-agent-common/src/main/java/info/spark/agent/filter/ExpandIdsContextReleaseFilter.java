package info.spark.agent.filter;

import info.spark.starter.basic.context.ExpandIdsContext;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 请求完成后释放 clientId 和 tenantId:</p>
 * 写: info.spark.agent.sender.AgentService#buildApiExtend()
 * 读: info.spark.starter.mybatis.handler.TenantIdMetaHandler#insertFill()
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.04.29 10:37
 * @since 1.8.0
 */
@Slf4j
public class ExpandIdsContextReleaseFilter extends OncePerRequestFilter {

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
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } finally {
            log.trace("Release ExpandIds Context: [{}]", ExpandIdsContext.context().get());
            ExpandIdsContext.context().remove();
        }
    }
}
