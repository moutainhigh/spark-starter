package info.spark.starter.security.util;

import info.spark.starter.security.matcher.SparkAntPathMatcher;
import info.spark.starter.security.matcher.SparkRegexUrlMatcher;
import info.spark.starter.security.matcher.SparkUrlMatcher;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.HashSet;
import java.util.Set;

import lombok.experimental.UtilityClass;

/**
 * <p>Description: 添加 RequestMatchers, 然后来比较哪些 url 符合要求 </p>
 * {@link RequestMatcher}
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:24
 * @since 1.0.0
 */
@UtilityClass
public class SkipRequestMatchers {

    /**
     * Ant matchers set
     *
     * @param antPatterns ant patterns
     * @return the set
     * @since 1.0.0
     */
    @NotNull
    public static Set<SparkUrlMatcher> antMatchers(String... antPatterns) {
        return antMatchers(null, antPatterns);
    }

    /**
     * Ant matchers set
     *
     * @param httpMethod  http method
     * @param antPatterns ant patterns
     * @return the set
     * @since 1.0.0
     */
    @NotNull
    public static Set<SparkUrlMatcher> antMatchers(HttpMethod httpMethod,
                                                   @NotNull String... antPatterns) {
        String method = httpMethod == null ? null : httpMethod.toString();
        Set<SparkUrlMatcher> matchers = new HashSet<>();
        for (String pattern : antPatterns) {
            matchers.add(new SparkAntPathMatcher(pattern, method));
        }
        return matchers;
    }

    /**
     * Regex matchers set
     *
     * @param regexPatterns regex patterns
     * @return the set
     * @since 1.0.0
     */
    @NotNull
    public static Set<SparkUrlMatcher> regexMatchers(String... regexPatterns) {
        return regexMatchers(null, regexPatterns);
    }

    /**
     * Regex matchers set
     *
     * @param httpMethod    http method
     * @param regexPatterns regex patterns
     * @return the set
     * @since 1.0.0
     */
    @NotNull
    public static Set<SparkUrlMatcher> regexMatchers(HttpMethod httpMethod,
                                                     @NotNull String... regexPatterns) {
        String method = httpMethod == null ? null : httpMethod.toString();
        Set<SparkUrlMatcher> matchers = new HashSet<>();
        for (String pattern : regexPatterns) {
            matchers.add(new SparkRegexUrlMatcher(pattern, method));
        }
        return matchers;
    }
}
