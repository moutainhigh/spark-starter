package info.spark.starter.captcha.filter;

import info.spark.starter.captcha.AbstractCaptcha;
import info.spark.starter.captcha.entity.FilterBean;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 使用过滤器验证 code </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.19 10:35
 * @since 1.0.0
 */
@Slf4j
public class CaptchaCodeFilter extends OncePerRequestFilter {

    /** Blocking rules */
    private final BlockingRules blockingRules;
    /** Captcha */
    private final AbstractCaptcha captcha;
    /** Filter */
    private final List<FilterBean> filter;

    /**
     * Captcha code filter
     *
     * @param blockingRules blocking rules
     * @param filter        filter
     * @param captcha       captcha
     * @since 1.8.0
     */
    public CaptchaCodeFilter(BlockingRules blockingRules, List<FilterBean> filter, AbstractCaptcha captcha) {
        this.blockingRules = blockingRules;
        this.filter = filter;
        this.captcha = captcha;
    }

    /**
     * Do filter internal *
     *
     * @param request     the http servlet request
     * @param response    the http servlet response
     * @param filterChain the filter chain
     * @throws ServletException the servlet exception
     * @throws IOException      the io exception
     * @since 1.0.0
     */
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {

        this.blockingRules.filter(request, this.captcha, this.filter);
        filterChain.doFilter(request, response);
    }
}
