package info.spark.feign.adapter.interceptor;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: header 处理, 将 client 端的 header 传递到下游服务 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.23 17:50
 * @since 1.0.0
 */
@Slf4j
public class RequestHeaderInterceptor implements RequestInterceptor {

    /**
     * 将原始的 request 中的 header 设置到 requestTemplate.header 中.
     * 原始的 request 中的 header 全是小写的 key, 与 requestTemplate.header 标准的首字母大写 key 不一样, 将造成以小写的 key 获取 header 为 null 的问题
     * 这里将 request 中的 header key 转换为 大写开头的 key
     *
     * @param requestTemplate request template
     * @since 1.0.0
     */
    @Override
    public void apply(@NotNull RequestTemplate requestTemplate) {
        log.debug("requestTemplate body: [{}]", requestTemplate.requestBody().asString());
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            // 获取原始的 request 数据
            HttpServletRequest request = attributes.getRequest();
            Enumeration<String> headerNames = request.getHeaderNames();
            // 获取 feign-client 配置信息, 包括 header
            Map<String, Collection<String>> headers = requestTemplate.headers();
            if (headerNames != null) {
                log.debug("original request Content-Type = [{}]", request.getHeader(HttpHeaders.CONTENT_TYPE));
                log.debug("requestTemplate Content-Type = [{}]", headers.get(HttpHeaders.CONTENT_TYPE));
                while (headerNames.hasMoreElements()) {
                    // 将原始 request 的 header 添加到 requestTemplate,
                    // 除了 content-type (因此需要在每个 feign-client 明确指定 Content-Type,
                    // 不然将默认使用 x-www-form-urlencoded)
                    String name = headerNames.nextElement();
                    if (!HttpHeaders.CONTENT_TYPE.equalsIgnoreCase(name)) {
                        // 将原始 request 的 header 添加到 feign-client
                        requestTemplate.header(processHeaderName(name), request.getHeader(name));
                        log.debug("添加 requestTemplate header: [{}:{}] -> [{}:{}]",
                                  processHeaderName(name),
                                  headers.get(processHeaderName(name)),
                                  processHeaderName(name),
                                  request.getHeader(name));
                    }
                }
            }
            log.debug("final requestTemplate header: [{}]", requestTemplate.headers());
        }
    }

    /**
     * 处理 header 的 key, 全部转为首字母大写的格式.
     * example: content-type --> Content-Type
     *
     * @param str str
     * @return the string
     * @since 1.0.0
     */
    @NotNull
    private static String processHeaderName(@NotNull String str) {
        String[] strings = str.split("-");
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : strings) {
            stringBuilder.append(firstCharToUpper(string)).append("-");
        }

        return stringBuilder.substring(0, stringBuilder.length() - 1);

    }

    /**
     * 首字母大写
     *
     * @param str str
     * @return the string
     * @since 1.0.0
     */
    private static String firstCharToUpper(@NotNull String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= LOWER_A && firstChar <= LOWER_Z) {
            char[] arr = str.toCharArray();
            arr[0] -= (LOWER_A - UPPER_A);
            str = new String(arr);
        }
        return str;
    }

    /** UPPER_A */
    private static final char UPPER_A = 'A';
    /** LOWER_A */
    private static final char LOWER_A = 'a';
    /** LOWER_Z */
    private static final char LOWER_Z = 'z';
}
