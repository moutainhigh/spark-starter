package info.spark.starter.security.matcher;

/**
 * <p>Description: 类似 {@link org.springframework.security.web.util.matcher.RequestMatcher}</p>
 * 但是因为要适配 webflux, 只有重写一个, 因为 webflux 用不了 HttpServletRequest
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:21
 * @since 1.0.0
 */
public interface SparkUrlMatcher {

    /**
     * Matches boolean.
     *
     * @param url the url
     * @return the boolean
     * @since 1.0.0
     */
    boolean matches(String url);

    /**
     * To strings string.
     *
     * @return the string
     * @since 1.0.0
     */
    @Override
    String toString();

}
