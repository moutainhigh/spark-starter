package info.spark.feign.adapter.util;

import com.fasterxml.jackson.core.type.TypeReference;
import info.spark.feign.adapter.constant.FeignAdapter;
import info.spark.feign.adapter.exception.FeignAdapterException;
import info.spark.starter.basic.Result;
import info.spark.starter.basic.StandardResult;
import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.basic.support.StrFormatter;
import info.spark.starter.basic.util.Charsets;
import info.spark.starter.basic.util.JsonUtils;

import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.SystemPropertyUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

import feign.Response;
import feign.Util;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.04 10:58
 * @since 1.0.0
 */
@Slf4j
@UtilityClass
public class FeignUtils {

    /**
     * 在不需要处理 data 的情况下使用此方法, 比如新增操作, 返回的结果中 data 没有数据.
     * 注意: 如果 data 有数据, 转换后是 LinkedHashMap, 如果对应的数据是一个实体, 将会造成类型转换异常,
     * 因此需要处理返回的数据, 请使用 {@link FeignUtils#convertor(Response, TypeReference)}
     * example:
     * Result result = FeignUtils.convertor(response);
     *
     * @param response the response
     * @return the result
     * @throws IOException the io exception
     * @since 1.0.0
     */
    @NotNull
    public static Result convertor(@NotNull Response response) {
        return convertor(response, Result.class, null);
    }

    /**
     * 在需要处理 data 的情况下使用此方法, 一般用于查询操作, 此方法为类型安全的转换, 需要注意传入的 T 类型
     * {@code
     * User convertor = FeignUtils.convertor(response, new TypeReference<User>() {}});
     * }**
     *
     * @param <T>      parameter
     * @param response response
     * @param type     type
     * @return the t
     * @since 1.0.0
     */
    @NotNull
    public static <T> T convertor(@NotNull Response response, TypeReference<T> type) {
        return convertor(response, null, type);
    }

    /**
     * Convertor t
     *
     * @param <T>      parameter
     * @param response response
     * @param clz      clz
     * @param type     type
     * @return the t
     * @since 1.0.0
     */
    @NotNull
    private static <T> T convertor(@NotNull Response response, Class<T> clz, TypeReference<T> type) {
        if (Objects.requireNonNull(HttpStatus.valueOf(response.status())).is2xxSuccessful()) {
            Response.Body body = response.body();
            if (body != null) {
                try {
                    String str = Util.decodeOrDefault(Util.toByteArray(body.asInputStream()),
                                                      Charsets.UTF_8,
                                                      JsonUtils.toJson(StandardResult.failed(Result.FAILURE_CODE, "数据转换失败")));
                    log.debug("Feign response body: [{}]", str);
                    if (JsonUtils.readTree(str).path(Result.SUCCESS).asBoolean()) {
                        if (clz != null) {
                            return JsonUtils.parse(str, clz);
                        } else {
                            return JsonUtils.parse(JsonUtils.readTree(str).path(Result.DATA).toString(), type);
                        }
                    } else {
                        // 只要 code != 2000, 则全部抛出异常 unchecked 异常
                        throw new FeignAdapterException(JsonUtils.readTree(str).path(Result.CODE).asText(),
                                                        JsonUtils.readTree(str).path(Result.MESSAGE).asText());
                    }
                } catch (IOException e) {
                    log.error("", e);
                }
            }
            throw new FeignAdapterException(Result.FAILURE_CODE, "响应结果为空");
        }
        throw new FeignAdapterException(String.valueOf(response.status()), response.reason());
    }

    /**
     * Build get form string
     *
     * @param object object
     * @return the string
     * @since 1.0.0
     */
    public static @NotNull String buildGetForm(Object object) {
        byte[] body = JsonUtils.toJsonAsBytes(object);
        String data = Base64.encodeBase64URLSafeString(body);
        data = data.replace("\n", "");
        data = data.replace("\r", "");
        return data;
    }

    /**
     * Gets path *
     *
     * @param path path
     * @return the path
     * @since 1.0.0
     */
    public static String getPath(String path) {
        if (StringUtils.hasText(path)) {
            path = path.trim();
            // 在行首添加 /
            if (!path.startsWith(FeignAdapter.PATH_SEPARATOR) && !path.startsWith(FeignAdapter.HTTP_PREFIX)) {
                path = FeignAdapter.PATH_SEPARATOR + path;
            }
            // 删除末尾的 /
            if (path.endsWith(FeignAdapter.PATH_SEPARATOR)) {
                path = path.substring(0, path.length() - 1);
            }
        }
        return path;
    }

    /**
     * Gets url *
     *
     * @param url url
     * @return the url
     * @since 1.0.0
     */
    public static String getUrl(String url) {
        if (StringUtils.hasText(url)) {
            boolean resolved = !(url.startsWith(SystemPropertyUtils.PLACEHOLDER_PREFIX)
                                 && url.endsWith(SystemPropertyUtils.PLACEHOLDER_SUFFIX));
            Assert.isTrue(resolved, "未成功替换占位符 [" + url + "], 请确保配置正确");
            Assert.isTrue(url.contains(ConfigKey.RibbonConfigKey.CLIENT_NAME),
                          StrFormatter.format("[{}] 没有包含 [ribbon.prefix:{}],注册的 Feign Clien 无法使用.",
                                              url,
                                              ConfigKey.RibbonConfigKey.CLIENT_NAME));
            if (!url.contains(FeignAdapter.PROTOCOL_PREFIX)) {
                url = FeignAdapter.HTTP_PROTOCOL + url;
            }
            try {
                new URL(url);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("不是合法的 url: " + url, e);
            }
        }
        return url;
    }

    /**
     * Gets name *
     *
     * @param name name
     * @return the name
     * @since 1.0.0
     */
    public static String getName(String name) {
        if (!StringUtils.hasText(name)) {
            return "";
        }

        String host = null;
        try {
            String url;
            if (!name.startsWith(FeignAdapter.HTTP_PROTOCOL) && !name.startsWith(FeignAdapter.HTTP_PROTOCOL)) {
                url = FeignAdapter.HTTP_PROTOCOL + name;
            } else {
                url = name;
            }
            host = new URI(url).getHost();

        } catch (URISyntaxException ignored) {
        }
        Assert.state(host != null, "不是有效的 hostname: [" + name + "], 请确保已在配置文件中正确配置");
        return name;
    }

    /**
     * Validate fallback *
     *
     * @param clazz clazz
     * @since 1.0.0
     */
    public static void validateFallback(@NotNull Class clazz) {
        Assert.isTrue(
            !clazz.isInterface(),
            "Fallback class must implement the interface annotated by @FeignClient");
    }

    /**
     * Validate fallback factory *
     *
     * @param clazz clazz
     * @since 1.0.0
     */
    public static void validateFallbackFactory(@NotNull Class clazz) {
        Assert.isTrue(!clazz.isInterface(),
                      "Fallback factory must produce instances of fallback classes "
                      + "that implement the interface annotated by @FeignClient");
    }
}
