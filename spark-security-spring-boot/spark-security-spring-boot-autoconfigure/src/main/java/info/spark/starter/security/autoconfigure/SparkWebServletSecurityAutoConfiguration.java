package info.spark.starter.security.autoconfigure;

import info.spark.starter.common.start.SparkAutoConfiguration;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import javax.annotation.Resource;
import javax.servlet.Servlet;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 抽离 security 公共代码, 认证服务和鉴权服务继承此类实现自己的安全配置 </p>
 * todo-dong4j : (2019年11月06日 13:50) [antMatchers 使用动态权限配置, 从 Redis/Nacos/DB 中加载配置, 实时生效]
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:20
 * @since 1.0.0
 */
@Slf4j
@ConditionalOnClass(value = {Servlet.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SparkWebServletSecurityAutoConfiguration extends WebSecurityConfigurerAdapter implements SparkAutoConfiguration {

    /** Dynamic security url */
    @Resource
    private DynamicSecurityUrl dynamicSecurityUrl;

    /**
     * 用于配置需要拦截的 url 路径、jwt 过滤器及出异常后的处理器
     * anyRequest          |   匹配所有请求路径
     * access              |   SpringEl表达式结果为true时可以访问
     * anonymous           |   匿名可以访问
     * denyAll             |   用户不能访问
     * fullyAuthenticated  |   用户完全认证可以访问 (非remember-me下自动登录)
     * hasAnyAuthority     |   如果有参数,参数表示权限,则其中任何一个权限可以访问
     * hasAnyRole          |   如果有参数,参数表示角色,则其中任何一个角色可以访问
     * hasAuthority        |   如果有参数,参数表示权限,则其权限可以访问
     * hasIpAddress        |   如果有参数,参数表示IP地址,如果用户IP和参数匹配,则可以访问
     * hasRole             |   如果有参数,参数表示角色,则其角色可以访问
     * permitAll           |   用户可以任意访问
     * rememberMe          |   允许通过remember-me登录的用户访问
     * authenticated       |   用户登录后可访问
     *
     * @param http the http
     * @throws Exception the exception
     * @since 1.0.0
     */
    @Override
    public void configure(@NotNull HttpSecurity http) throws Exception {
        log.debug("SparkWebServerSecurityConfig --> antMatchers :[{}]", this.dynamicSecurityUrl.getAllIgnoreUrlList());
        // 由于使用的是 JWT,我们这里不需要 csrf
        http.csrf().disable()
            // 基于token,所以不需要session
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            // 监控节点不设置权限 默认配置 + 自定义配置
            .antMatchers(this.dynamicSecurityUrl.getAllIgnoreUrlList().toArray(new String[0]))
            .permitAll()
            // 跨域请求会先进行一次options请求
            .antMatchers(HttpMethod.OPTIONS)
            .permitAll()
            .anyRequest().authenticated()
            .and()
            .formLogin().permitAll();

    }

    /**
     * 监控节点不设置权限 默认配置 + 自定义配置
     *
     * @param web the web
     * @since 1.0.0
     */
    @Override
    public void configure(@NotNull WebSecurity web) {
        // web.ignoring().antMatchers(permitUri());
    }
}
