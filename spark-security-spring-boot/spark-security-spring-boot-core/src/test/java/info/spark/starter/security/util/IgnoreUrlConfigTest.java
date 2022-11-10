package info.spark.starter.security.util;

import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.basic.util.StringPool;
import info.spark.starter.common.enums.LibraryEnum;
import info.spark.starter.security.matcher.SparkUrlMatcher;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Stream;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:22
 * @since 1.0.0
 */
@Slf4j
class IgnoreUrlConfigTest {
    /** Default skip url */
    private final Set<String> DEFAULT_SKIP_URL = Sets.newHashSetWithExpectedSize(16);
    /** Url matcher set */
    private Set<SparkUrlMatcher> urlMatcherSet = Sets.newHashSetWithExpectedSize(16);

    /**
     * Before
     *
     * @since 1.0.0
     */
    @BeforeEach
    void before() {
        // 忽略启动信息接口
        this.DEFAULT_SKIP_URL.add(LibraryEnum.START_URL);
        this.DEFAULT_SKIP_URL.add(LibraryEnum.DRUID.getUri() + StringPool.DOUBLE_ASTERISK);
        // 忽略应用检查接口
        this.DEFAULT_SKIP_URL.add("/actuator/**");
        // 忽略 swagger 相关接口
        this.DEFAULT_SKIP_URL.add("/v2/api-docs/**");
        this.DEFAULT_SKIP_URL.add("/v2/api-docs-ext/**");
        // 忽略验证码相关接口
        this.DEFAULT_SKIP_URL.add("/kaptcha/**");
        // 忽略 oauth 相关接口
        this.DEFAULT_SKIP_URL.add("/oauth/**");
        // 忽略登录接口
        this.DEFAULT_SKIP_URL.add("/login/**");
        // 忽略登出接口
        this.DEFAULT_SKIP_URL.add("/logout/**");
        // 忽略日志相关接口
        this.DEFAULT_SKIP_URL.add("/log/**");
        // 忽略错误接口
        this.DEFAULT_SKIP_URL.add("/error/**");
        // 忽略静态资源
        this.DEFAULT_SKIP_URL.add("/**/*.ico");
        this.DEFAULT_SKIP_URL.add("/**/*.css");
        this.DEFAULT_SKIP_URL.add("/**/*.js");
        this.DEFAULT_SKIP_URL.add("/**/*.html");
        this.DEFAULT_SKIP_URL.add("/**/*.map");
        this.DEFAULT_SKIP_URL.add("/**/*.svg");
        this.DEFAULT_SKIP_URL.add("/**/*.png");
        this.urlMatcherSet = SkipRequestMatchers.antMatchers(this.DEFAULT_SKIP_URL.toArray(new String[0]));
    }

    /**
     * Test ignore ant path request matcher 1
     *
     * @since 1.0.0
     */
    @Test
    void testIgnoreAntPathRequestMatcher_1() {
        for (SparkUrlMatcher s : this.urlMatcherSet) {
            log.info("{} {}", s.toString(), s.matches("/oauth"));
        }
    }

    /**
     * Test ignore ant path request matcher 2
     *
     * @since 1.0.0
     */
    @Test
    void testIgnoreAntPathRequestMatcher_2() {
        for (SparkUrlMatcher s : this.urlMatcherSet) {
            log.info("{} {}", s.toString(), s.matches("/oauth/token"));
        }
    }

    /**
     * Test ignore ant path request matcher 3
     *
     * @since 1.0.0
     */
    @Test
    void testIgnoreAntPathRequestMatcher_3() {
        for (SparkUrlMatcher s : this.urlMatcherSet) {
            log.info("{} {}", s.toString(), s.matches("/oauth/token?eee=eee"));
        }
    }

    /**
     * Test
     *
     * @since 1.0.0
     */
    @Test
    void test() {
        String url = "/oauth/token";
        log.debug("更新 ignore url: {}\n {}", JsonUtils.toJson(this.DEFAULT_SKIP_URL, true), JsonUtils.toJson(this.urlMatcherSet, true));
        log.info("{}", Stream.of(this.urlMatcherSet.toArray(new SparkUrlMatcher[0]))
            .peek(matchers -> log.debug("matcher info = {}, request url = {} --> {}", matchers.toString(), url, matchers.matches(url)))
            .anyMatch(matchers -> matchers.matches(url)));
    }
}
