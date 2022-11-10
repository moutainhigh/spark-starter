package info.spark.starter.doc.agent.hand;

import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.basic.util.StringPool;
import info.spark.starter.doc.agent.constant.AgentDocConstant;
import info.spark.starter.util.StringUtils;

import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.spec.HttpServletRequestImpl;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: agent-doc GET调式 </p>
 *
 * @author wanghao
 * @version 1.7.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.06 14:46
 * @since 1.7.0
 */
@Slf4j
public class AgentDocHandlerFilter extends OncePerRequestFilter {

    /**
     * Do filter internal
     *
     * @param request     request
     * @param response    response
     * @param filterChain filter chain
     * @throws IOException      io exception
     * @throws ServletException servlet exception
     * @since 1.7.0
     */
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws IOException, ServletException {
        String testDocHeader = request.getHeader(AgentDocConstant.X_AGENT_DOC_TEST);

        if (StringUtils.isNotBlank(testDocHeader)
            && HttpMethod.GET.matches(request.getMethod())
            && AgentDocConstant.AGENT_PATH.equals(request.getRequestURI())
            && StringPool.TRUE.equalsIgnoreCase(testDocHeader)) {

            Map<String, String[]> copyMap = new HashMap<>(request.getParameterMap());
            copyMap.remove(AgentDocConstant.API);
            String value = copyMap.values().stream().map(arr -> arr[0]).collect(Collectors.joining(StringPool.EMPTY));

            ArrayDeque<String> list = new ArrayDeque<>();
            list.addFirst(Base64.encodeBase64URLSafeString(JsonUtils.toJsonAsBytes(value)));

            if (request instanceof HttpServletRequestImpl) {
                HttpServletRequestImpl servletRequest = (HttpServletRequestImpl) request;
                HttpServerExchange exchange = servletRequest.getExchange();
                exchange.getQueryParameters().put(AgentDocConstant.DATA, list);
            } else {
                // 如果不是 HttpServletRequestImpl 类型, 那么肯定是使用了可缓存的 Request, 则需要直接重新添加
                request.getParameterMap().put(AgentDocConstant.DATA, list.toArray(new String[0]));
            }
        }

        filterChain.doFilter(request, response);
    }
}
