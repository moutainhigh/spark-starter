package info.spark.feign.adapter.interceptor;

import com.google.common.collect.Maps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.spark.feign.adapter.exception.FeignAdapterException;
import info.spark.starter.basic.util.JsonUtils;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 处理 GET 请求时, 传输 POJO 的相关问题 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.26 16:56
 * @since 1.0.0
 */
@Slf4j
public class RequestParamterInterceptor implements RequestInterceptor {
    /** Object mapper */
    private final ObjectMapper objectMapper = JsonUtils.getCopyMapper();

    /**
     * Request paramter interceptor
     *
     * @since 1.0.0
     */
    public RequestParamterInterceptor() {

    }

    /**
     * POST 和 GET 入参为 pojo 时, 都会将 pojo 序列化为 json, 只有明确指定了 Content-Type=x-www-form-urlencoded 后数据会被以 url 参数的方式写入 body 中.
     * 一些 client 不支持 GET 请求时在 body 中添加参数, 比如 okhttpclient 就不允许, 因此这里将数据进行转换:
     * <p>
     * 1.  GET 请求, 且数据是以 xxx=yyy&zzz=uuu 的形式存在 body 中, 此方法会将 body 的数据拼接到 url 中 (Content-Type=x-www-form-urlencoded).
     * 2.  GET 请求, 且数据为 json 字符串时, 将 json 解析为 url 参数 (Content-Type=json).
     *
     * @param template template
     * @since 1.0.0
     */
    @Override
    public void apply(@NotNull RequestTemplate template) {
        if (template.method().equalsIgnoreCase(Request.HttpMethod.GET.name()) && template.requestBody().asBytes() != null) {
            Map<String, Collection<String>> queries = Maps.newHashMapWithExpectedSize(16);
            String bodyString = template.requestBody().asString();
            try {
                // 转换 json 数据到 url
                if (JsonUtils.isJson(bodyString)) {
                    JsonNode jsonNode = this.objectMapper.readTree(template.requestBody().asBytes());
                    // feign 不支持 GET 方法传 POJO, json body 转 query
                    this.buildQuery(jsonNode, "", queries);

                } else {
                    // 如果是 xxx=yyy&zzz=uuu 则直接写入 url
                    this.buildQuery(bodyString, queries);
                }
                // 设置新的查询参数
                template.queries(queries);
                // 删除 body 中的数据, 解决使用 okhttp client 'method GET must not have a request body' 问题
                template.body(Request.Body.empty());
            } catch (IOException e) {
                throw new FeignAdapterException(String.format("数据转换异常: [{%s}]", e.getMessage()));
            }
        }
    }

    /**
     * 将 url 参数分解为 map
     *
     * @param bodyString body string
     * @param queries    queries
     * @since 1.0.0
     */
    private void buildQuery(@NotNull String bodyString, Map<String, Collection<String>> queries) {
        String[] params = bodyString.split("&");
        for (String s : params) {
            String[] p = s.split("=");
            if (p.length == 2) {
                queries.put(p[0], Collections.singletonList(p[1]));
            }
        }
    }

    /**
     * 将 json 转为 url 参数
     *
     * @param jsonNode json node
     * @param path     path
     * @param queries  queries
     * @since 1.0.0
     */
    @SuppressWarnings("checkstyle:ReturnCount")
    private void buildQuery(@NotNull JsonNode jsonNode, String path, Map<String, Collection<String>> queries) {
        // 叶子节点
        if (!jsonNode.isContainerNode()) {
            if (jsonNode.isNull()) {
                return;
            }
            Collection<String> values = queries.computeIfAbsent(path, k -> new ArrayList<>());
            values.add(jsonNode.asText());
            return;
        }
        // 数组节点
        if (jsonNode.isArray()) {
            Iterator<JsonNode> it = jsonNode.elements();
            while (it.hasNext()) {
                this.buildQuery(it.next(), path, queries);
            }
        } else {
            Iterator<Map.Entry<String, JsonNode>> it = jsonNode.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> entry = it.next();
                if (StringUtils.hasText(path)) {
                    this.buildQuery(entry.getValue(), path + "." + entry.getKey(), queries);
                } else { //根节点
                    this.buildQuery(entry.getValue(), entry.getKey(), queries);
                }
            }
        }
    }

}
