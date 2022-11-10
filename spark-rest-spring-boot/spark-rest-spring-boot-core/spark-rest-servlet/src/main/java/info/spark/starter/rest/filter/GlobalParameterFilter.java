package info.spark.starter.rest.filter;

import info.spark.starter.auth.constant.AuthConstant;
import info.spark.starter.auth.entity.AuthorizationUser;
import info.spark.starter.auth.util.JwtUtils;
import info.spark.starter.basic.exception.BasicException;
import info.spark.starter.util.ObjectUtils;
import info.spark.starter.util.StringUtils;
import info.spark.starter.rest.support.CacheRequestEnhanceWrapper;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 解析 token 字段, 注入到 controller, 优先级最低 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.04 11:52
 * @since 1.0.0
 */
@Slf4j
public class GlobalParameterFilter extends OncePerRequestFilter {
    /**
     * Do filter internal *
     *
     * @param request     request
     * @param response    response
     * @param filterChain filter chain
     * @throws ServletException servlet exception
     * @throws IOException      io exception
     * @since 1.0.0
     */
    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request,
                                 @NotNull HttpServletResponse response,
                                 @NotNull FilterChain filterChain) throws ServletException, IOException {

        if (request instanceof CacheRequestEnhanceWrapper) {
            filterChain.doFilter(new TokenRequestWrapper(((CacheRequestEnhanceWrapper) request).getCachingRequestWrapper()), response);
        } else {
            filterChain.doFilter(new TokenRequestWrapper(new ContentCachingRequestWrapper(request)), response);
        }
    }

    /**
         * <p>Description: 解析 token 参数 </p>
     *
     * @author dong4j
     * @version 1.2.3
     * @email "mailto:dong4j@gmail.com"
     * @date 2019.12.25 23:51
     * @since 1.0.0
     */
    private static class TokenRequestWrapper extends CacheRequestEnhanceWrapper {
        /** CURRENT_USER_ID */
        private static final String CURRENT_USER_ID = "currentUserId";
        /** CURRENT_TENANT_ID */
        private static final String CURRENT_TENANT_ID = "currentTenantId";

        /**
         * Token request wrapper
         *
         * @param request request
         * @since 1.0.0
         */
        TokenRequestWrapper(ContentCachingRequestWrapper request) {
            super(request);
        }

        /**
         * 修改此方法主要是因为当 RequestMapper 中的参数为 pojo 类型时,
         * 会通过此方法获取所有的请求参数并进行遍历,对 pojo 属性赋值
         *
         * @return parameter names
         * @since 1.0.0
         */
        @Override
        public Enumeration<String> getParameterNames() {
            Enumeration<String> enumeration = super.getParameterNames();
            ArrayList<String> list = Collections.list(enumeration);
            String token = this.getToken();
            if (StringUtils.isNotBlank(token)) {
                list.add(CURRENT_USER_ID);
                list.add(CURRENT_TENANT_ID);
                return Collections.enumeration(list);
            } else {
                return super.getParameterNames();
            }
        }

        private String getToken() {
            // 当有 token 字段时动态的添加 currentUserId 和 currentClientId 字段
            String authentication = JwtUtils.getToken(this.getHeader(AuthConstant.OAUTH_HEADER_TYPE));
            return StringUtils.isBlank(authentication) ? this.getHeader(AuthConstant.X_CLIENT_TOKEN) : "";
        }

        /**
         * controller 入参如果符合则直接注入
         *
         * @param name name
         * @return the string [ ]
         * @since 1.0.0
         */
        @Override
        @SuppressWarnings("checkstyle:ReturnCount")
        public String[] getParameterValues(String name) {
            if (CURRENT_USER_ID.equals(name) || CURRENT_TENANT_ID.equals(name)) {

                String token = this.getToken();
                if (StringUtils.isNotBlank(token)) {
                    AuthorizationUser user = JwtUtils.PlayGround.getUser(token);
                    if (ObjectUtils.isNull(user)) {
                        log.error("[{}] 使用了 [{}] 或 [{}] 参数名, 但是解析 token 失败",
                                  this.cachingRequestWrapper.getPathInfo(),
                                  CURRENT_USER_ID,
                                  CURRENT_TENANT_ID);
                        throw new BasicException("B.A-40001", "请求未授权");
                    }

                    if (CURRENT_USER_ID.equals(name)) {
                        return new String[] {String.valueOf(user.getId())};
                    } else {
                        return new String[] {String.valueOf(user.getTenantId())};
                    }
                } else {
                    log.error("[{}] 使用了 [{}] 或 [{}] 参数名, 但是未获取到 token",
                              this.cachingRequestWrapper.getPathInfo(),
                              CURRENT_USER_ID,
                              CURRENT_TENANT_ID);

                    throw new BasicException("B.A-40001", "请求未授权");
                }
            }
            return super.getParameterValues(name);
        }
    }
}
