package info.spark.starter.rest.condition;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Description: 处理 优先匹配问题的 condition，目前暂时无法使用，路径为精确路径 /v1/demo/hello，不再是/{path:v\\d+}/demo/hello </p>
 *
 * @author wanghao
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.09.26 10:53
 * @since 2.0.0
 */
public class ApiVersionCondition implements RequestCondition<ApiVersionCondition> {

    /** 路径中版本的前缀， 这里用 /v[1-9]/的形式 */
    private final Pattern versionPrefixPattern = Pattern.compile("v(\\d+)/");

    /** Api version */
    private final int apiVersion;

    /**
     * Api version condition
     *
     * @param apiVersion api version
     * @since 2.0.0
     */
    public ApiVersionCondition(int apiVersion) {
        this.apiVersion = apiVersion;
    }

    /**
     * Combine
     *
     * @param other other
     * @return the api version condition
     * @since 2.0.0
     */
    public @NotNull ApiVersionCondition combine(ApiVersionCondition other) {
        // 采用最后定义优先原则，则方法上的定义覆盖类上面的定义
        return new ApiVersionCondition(other.getApiVersion());
    }

    /**
     * Gets matching condition *
     *
     * @param request request
     * @return the matching condition
     * @since 2.0.0
     */
    public ApiVersionCondition getMatchingCondition(HttpServletRequest request) {
        Matcher m = this.versionPrefixPattern.matcher(Optional.ofNullable(request.getPathInfo()).orElse(request.getRequestURI()));
        if (m.find()) {
            int version = Integer.parseInt(m.group(1));
            // 如果请求的版本号大于配置版本号， 则满足
            if (version >= this.apiVersion) {
                return this;
            }
        }
        return null;
    }

    /**
     * Compare to
     *
     * @param other   other
     * @param request request
     * @return the int
     * @since 2.0.0
     */
    public int compareTo(ApiVersionCondition other, @NotNull HttpServletRequest request) {
        // 优先匹配最新的版本号
        return other.getApiVersion() - this.apiVersion;
    }

    /**
     * Gets api version *
     *
     * @return the api version
     * @since 2.0.0
     */
    public int getApiVersion() {
        return apiVersion;
    }

}
