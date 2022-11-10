package info.spark.starter.security.matcher;

import info.spark.starter.basic.util.StringPool;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UrlPathHelper;

import java.util.Collections;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:21
 * @since 1.0.0
 */
@Slf4j
public final class SparkAntPathMatcher implements SparkUrlMatcher {
    /**
     * MATCH_ALL
     */
    private static final String MATCH_ALL = "/**";

    /**
     * Matcher
     */
    private final Matcher matcher;
    /**
     * Pattern
     */
    private final String pattern;
    /**
     * Http method
     */
    private final HttpMethod httpMethod;
    /**
     * Case sensitive
     */
    private final boolean caseSensitive;


    /**
     * Spark ant path matcher
     *
     * @param pattern pattern
     * @since 1.0.0
     */
    public SparkAntPathMatcher(String pattern) {
        this(pattern, null);
    }


    /**
     * Spark ant path matcher
     *
     * @param pattern    pattern
     * @param httpMethod http method
     * @since 1.0.0
     */
    public SparkAntPathMatcher(String pattern, String httpMethod) {
        this(pattern, httpMethod, true);
    }


    /**
     * Spark ant path matcher
     *
     * @param pattern       pattern
     * @param httpMethod    http method
     * @param caseSensitive case sensitive
     * @since 1.0.0
     */
    private SparkAntPathMatcher(String pattern, String httpMethod,
                                boolean caseSensitive) {
        this(pattern, httpMethod, caseSensitive, null);
    }

    /**
     * Instantiates a new Spark ant path matcher.
     *
     * @param pattern       the pattern
     * @param httpMethod    the http method
     * @param caseSensitive the case-sensitive
     * @param urlPathHelper the url path helper
     * @since 1.0.0
     */
    private SparkAntPathMatcher(String pattern, String httpMethod,
                                boolean caseSensitive, UrlPathHelper urlPathHelper) {
        Assert.hasText(pattern, "Pattern cannot be null or empty");
        this.caseSensitive = caseSensitive;

        if (MATCH_ALL.equals(pattern) || StringPool.DOUBLE_ASTERISK.equals(pattern)) {
            pattern = MATCH_ALL;
            this.matcher = null;
        } else {
            // If the pattern ends with {@code /**} and has no other wildcards or path
            // variables, then optimize to a sub-path match
            boolean noPlaceholder = pattern.indexOf('?') == -1 && pattern.indexOf('{') == -1 && pattern.indexOf('}') == -1;
            if (pattern.endsWith(MATCH_ALL)
                && noPlaceholder
                && pattern.indexOf("*") == pattern.length() - 2) {
                this.matcher = new SubpathMatcher(
                    pattern.substring(0, pattern.length() - 3), caseSensitive);
            } else {
                this.matcher = new SpringAntMatcher(pattern, caseSensitive);
            }
        }

        this.pattern = pattern;
        this.httpMethod = StringUtils.hasText(httpMethod) ? HttpMethod.valueOf(httpMethod)
                                                          : null;
    }

    /**
     * Value of http method
     *
     * @param method method
     * @return the http method
     * @since 1.0.0
     */
    @Nullable
    private static HttpMethod valueOf(String method) {
        try {
            return HttpMethod.valueOf(method);
        } catch (IllegalArgumentException ignored) {
        }

        return null;
    }

    /**
     * Matches boolean
     *
     * @param url url
     * @return the boolean
     * @since 1.0.0
     */
    @Override
    public boolean matches(String url) {
        if (MATCH_ALL.equals(this.pattern)) {
            log.debug("url '{}' matched by a universal pattern '/**'", url);
            return true;
        }
        return this.matcher.matches(url);
    }

    /**
     * Hash code int
     *
     * @return the int
     * @since 1.0.0
     */
    @Override
    public int hashCode() {
        int code = 31 ^ this.pattern.hashCode();
        if (this.httpMethod != null) {
            code ^= this.httpMethod.hashCode();
        }
        return code;
    }

    /**
     * Equals boolean
     *
     * @param obj obj
     * @return the boolean
     * @since 1.0.0
     */
    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SparkAntPathMatcher)) {
            return false;
        }

        SparkAntPathMatcher other = (SparkAntPathMatcher) obj;
        return this.pattern.equals(other.pattern) && this.httpMethod == other.httpMethod
               && this.caseSensitive == other.caseSensitive;
    }

    /**
     * To string string
     *
     * @return the string
     * @since 1.0.0
     */
    @NotNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Ant [pattern='").append(this.pattern).append("'");

        if (this.httpMethod != null) {
            sb.append(", ").append(this.httpMethod);
        }

        sb.append("]");

        return sb.toString();
    }

    /**
     * Matcher
     *
     * @author dong4j
     * @version 1.2.3
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.01.27 18:21
     * @since 1.0.0
     */
    private interface Matcher {
        /**
         * Matches boolean.
         *
         * @param path the path
         * @return the boolean
         * @since 1.0.0
         */
        boolean matches(String path);

        /**
         * Extract uri template variables map.
         *
         * @param path the path
         * @return the map
         * @since 1.0.0
         */
        Map<String, String> extractUriTemplateVariables(String path);
    }

    /**
     * Spring ant matcher
     *
     * @author dong4j
     * @version 1.2.3
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.01.27 18:21
     * @since 1.0.0
     */
    private static final class SpringAntMatcher implements Matcher {
        /**
         * Ant matcher
         */
        private final AntPathMatcher antMatcher;

        /**
         * Pattern
         */
        private final String pattern;

        /**
         * Spring ant matcher
         *
         * @param pattern       pattern
         * @param caseSensitive case sensitive
         * @since 1.0.0
         */
        private SpringAntMatcher(String pattern, boolean caseSensitive) {
            this.pattern = pattern;
            this.antMatcher = createMatcher(caseSensitive);
        }

        /**
         * Create matcher ant path matcher
         *
         * @param caseSensitive case sensitive
         * @return the ant path matcher
         * @since 1.0.0
         */
        private static AntPathMatcher createMatcher(boolean caseSensitive) {
            AntPathMatcher matcher = new AntPathMatcher();
            matcher.setTrimTokens(false);
            matcher.setCaseSensitive(caseSensitive);
            return matcher;
        }

        /**
         * Matches boolean
         *
         * @param path path
         * @return the boolean
         * @since 1.0.0
         */
        @Override
        public boolean matches(String path) {
            return this.antMatcher.match(this.pattern, path);
        }

        /**
         * Extract uri template variables map
         *
         * @param path path
         * @return the map
         * @since 1.0.0
         */
        @NotNull
        @Override
        public Map<String, String> extractUriTemplateVariables(String path) {
            return this.antMatcher.extractUriTemplateVariables(this.pattern, path);
        }
    }

    /**
     * Subpath matcher
     *
     * @author dong4j
     * @version 1.2.3
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.01.27 18:21
     * @since 1.0.0
     */
    private static final class SubpathMatcher implements Matcher {
        /**
         * Subpath
         */
        private final String subpath;
        /**
         * Length
         */
        private final int length;
        /**
         * Case sensitive
         */
        private final boolean caseSensitive;

        /**
         * Subpath matcher
         *
         * @param subpath       subpath
         * @param caseSensitive case sensitive
         * @since 1.0.0
         */
        private SubpathMatcher(@NotNull String subpath, boolean caseSensitive) {
            assert !subpath.contains("*");
            this.subpath = caseSensitive ? subpath : subpath.toLowerCase();
            this.length = subpath.length();
            this.caseSensitive = caseSensitive;
        }

        /**
         * Matches boolean
         *
         * @param path path
         * @return the boolean
         * @since 1.0.0
         */
        @Override
        public boolean matches(String path) {
            if (!this.caseSensitive) {
                path = path.toLowerCase();
            }
            return path.startsWith(this.subpath)
                   && (path.length() == this.length || path.charAt(this.length) == '/');
        }

        /**
         * Extract uri template variables map
         *
         * @param path path
         * @return the map
         * @since 1.0.0
         */
        @NotNull
        @Contract(pure = true)
        @Override
        public Map<String, String> extractUriTemplateVariables(String path) {
            return Collections.emptyMap();
        }
    }
}
