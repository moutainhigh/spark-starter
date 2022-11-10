package info.spark.starter.openness.utils;

import info.spark.starter.basic.exception.ServiceInternalException;
import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.basic.util.StringPool;
import info.spark.starter.openness.entity.SignEntity;
import info.spark.starter.util.Base64Utils;
import info.spark.starter.util.DigestUtils;
import info.spark.starter.util.StringUtils;

import org.jetbrains.annotations.Nullable;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import lombok.experimental.UtilityClass;

/**
 * <p>Description: 提供给第三方的jar包， </p>
 *
 * @author wanghao
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.08.19 17:07
 * @since 1.9.0
 */
@UtilityClass
public final class OpennessSignUtils {

    /**
     * Sign
     *
     * @param signEntity sign entity
     * @return the string
     * @since 1.9.0
     */
    public static String sign(SignEntity signEntity) {

        if (null == signEntity
            || StringUtils.isBlank(signEntity.getTimestamp())
            || StringUtils.isBlank(signEntity.getHttpMethodType())
            || StringUtils.isBlank(signEntity.getContentTypeStr())
            || StringUtils.isBlank(signEntity.getNonce())
            || StringUtils.isBlank(signEntity.getUri())) {

            throw new ServiceInternalException(
                String.format("签名参数不能为空：%s、%s、%s、%s、%s", "Http_Method", "Content-Type", "timestamp", "nonce", "uri"));
        }
        int len = 6;
        if (Optional.ofNullable(signEntity.getSecretKey()).orElse(StringPool.EMPTY).length() < len) {
            throw new ServiceInternalException("失效的密钥");
        }
        StringJoiner joiner = new StringJoiner(StringPool.COLON);
        // req method 大写字符串 如：GET、POST
        joiner.add(signEntity.getHttpMethodType().toUpperCase());
        // 请求参数，或按字典顺序排序后的 md5 摘要值
        joiner.add(StringUtils.isBlank(signEntity.getParamMd5()) ? StringPool.EMPTY : signEntity.getParamMd5());
        // req Content-Type 大写字符串 如：APPLICATION/JSON;CHARSET=UTF-8
        joiner.add(signEntity.getContentTypeStr().toUpperCase());
        // req 时间戳
        joiner.add(signEntity.getTimestamp() + "");
        // nonce
        joiner.add(signEntity.getNonce() + "");
        // req 请求接口地址 大写，如：/YYY/XX
        joiner.add(Optional.ofNullable(signEntity.getUri()).map(String::toUpperCase).orElse(StringPool.EMPTY));
        // 签名key也一起参与签名
        joiner.add(signEntity.getSecretKey());

        return Base64Utils.encode(DigestUtils.sha256Hex(joiner.toString().getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Sort parameters
     *
     * @param args args
     * @return the string
     * @since 1.9.0
     */
    public static String sortParameters(Map<String, String> args) {
        if (CollectionUtils.isEmpty(args)) {
            return StringPool.EMPTY;
        }
        List<String> keyList = args.keySet().stream().sorted().collect(Collectors.toList());
        List<String> kvList = new ArrayList<>();
        for (String key : keyList) {
            kvList.add(key + StringPool.EQUALS + args.get(key));
        }
        return StringUtils.join(kvList, StringPool.AMPERSAND);
    }


    /**
     * Md 5 params
     *
     * @param params url params
     * @param body   post body
     * @return the string
     * @since 1.9.0
     */
    public static String md5Params(@Nullable Map<String, String> params, @Nullable Object body) {
        // 字典自然升序
        String sortParams = sortParameters(params);
        // body 里面的 json格式参数
        String bodyString = null == body ? "" : StringUtils.replaceBlank(JsonUtils.toJson(body));

        // 组装格式
        StringBuilder builder = new StringBuilder(sortParams);
        if (StringUtils.isNoneBlank(sortParams) && StringUtils.isNoneBlank(bodyString)) {
            builder.append(StringPool.AMPERSAND);
        }
        if (StringUtils.isNoneBlank(bodyString)) {
            builder.append("fapi-data=");
            builder.append(bodyString);
        }

        return StringPool.EMPTY.equals(builder.toString()) ? StringPool.EMPTY : md5(builder.toString());
    }

    /**
     * Md 5
     *
     * @param content content
     * @return the string
     * @since 1.9.0
     */
    public static String md5(String content) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            md.update(content.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                String str = Integer.toHexString(b & 0xFF);
                if (str.length() == 1) {
                    sb.append("0");
                }
                sb.append(str);
            }
            result = sb.toString();
        } catch (Exception ignored) {
            // nothing to do
        }
        return result;
    }

}
