package info.spark.starter.security.matcher;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:16
 * @since 1.0.0
 */
@Slf4j
public final class SparkRegexUrlMatcher implements SparkUrlMatcher {

    /**
     * Pattern
     */
    private final Pattern pattern;
    /**
     * Http method
     */
    private final HttpMethod httpMethod;

    /**
     * Instantiates a new Spark regex url matcher.
     *
     * @param pattern    the pattern
     * @param httpMethod the http method
     * @since 1.0.0
     */
    public SparkRegexUrlMatcher(String pattern, String httpMethod) {
        this(pattern, httpMethod, false);
    }

    /**
     * Instantiates a new Spark regex url matcher.
     *
     * @param pattern         the pattern
     * @param httpMethod      the http method
     * @param caseInsensitive the case-insensitive
     * @since 1.0.0
     */
    private SparkRegexUrlMatcher(String pattern, String httpMethod, boolean caseInsensitive) {
        if (caseInsensitive) {
            this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        } else {
            this.pattern = Pattern.compile(pattern);
        }
        this.httpMethod = StringUtils.hasText(httpMethod) ? HttpMethod
            .valueOf(httpMethod) : null;
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
        log.debug("Checking match of request : '{}'; against '{}'", url, this.pattern);
        return this.pattern.matcher(url).matches();
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
        sb.append("Regex [pattern='").append(this.pattern).append("'");

        if (this.httpMethod != null) {
            sb.append(", ").append(this.httpMethod);
        }

        sb.append("]");

        return sb.toString();
    }

    /**
     * Equals boolean
     *
     * @param o o
     * @return the boolean
     * @since 1.0.0
     */
    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SparkRegexUrlMatcher that = (SparkRegexUrlMatcher) o;
        return Objects.equals(this.pattern, that.pattern) && this.httpMethod == that.httpMethod;
    }

    /**
     * Hash code int
     *
     * @return the int
     * @since 1.0.0
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.pattern, this.httpMethod);
    }
}
