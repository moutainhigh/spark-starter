package info.spark.starter.rest.xss;

import info.spark.starter.rest.support.CacheRequestEnhanceWrapper;

import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.*;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import lombok.AllArgsConstructor;

/**
 * <p>Description: XSS过滤 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.11 17:01
 * @since 1.0.0
 */
@AllArgsConstructor
public class XssFilter implements Filter {

    /** Xss properties */
    private final List<String> excludePatterns;

    /**
     * Init *
     *
     * @param config config
     * @since 1.0.0
     */
    @Override
    public void init(FilterConfig config) { }

    /**
     * Do filter *
     *
     * @param request  request
     * @param response response
     * @param chain    chain
     * @throws IOException      io exception
     * @throws ServletException servlet exception
     * @since 1.0.0
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String path = ((HttpServletRequest) request).getServletPath();
        if (this.excludePatterns.stream().anyMatch(path::contains)) {
            chain.doFilter(request, response);
        } else {
            if (request instanceof CacheRequestEnhanceWrapper) {
                chain.doFilter(new XssHttpServletRequestWrapper(((CacheRequestEnhanceWrapper) request).getCachingRequestWrapper()),
                               response);
            } else {
                chain.doFilter(new XssHttpServletRequestWrapper(new ContentCachingRequestWrapper((HttpServletRequest) request)),
                               response);
            }
        }
    }

    /**
     * Destroy
     *
     * @since 1.0.0
     */
    @Override
    public void destroy() { }

}
