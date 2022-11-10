package info.spark.starter.rest.support;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.common.enums.SerializeEnum;
import info.spark.starter.rest.annotation.RequestSingleParam;
import info.spark.starter.util.core.exception.BaseException;
import info.spark.starter.core.util.DataTypeUtils;
import info.spark.starter.util.ObjectUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.http.HttpInputMessage;

import java.util.Map;

import lombok.SneakyThrows;

/**
 * <p>Description: 处理 @RequestSingleParam </p>
 * 1. 只支持 POST/PUT json 格式的数据解析
 * 2. 可解析多个字段, 前提是 request 允许多次读取
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.03 10:41
 * @since 1.0.0
 */
public class RequestSingleParamHandlerMethodArgumentResolver extends AbstractMethodArgumentResolver<RequestSingleParam> {
    /**
     * Request single param handler method argument resolver
     *
     * @param objectMapper               object mapper
     * @param globalEnumConverterFactory global enum converter factory
     * @since 1.0.0
     */
    @Contract(pure = true)
    public RequestSingleParamHandlerMethodArgumentResolver(ObjectMapper objectMapper,
                                                           ConverterFactory<String, SerializeEnum<?>> globalEnumConverterFactory) {
        super(objectMapper, globalEnumConverterFactory);
    }

    /**
     * Supports annotation
     *
     * @return the class
     * @since 1.4.0
     */
    @Override
    protected Class<RequestSingleParam> supportsAnnotation() {
        return RequestSingleParam.class;
    }

    /**
     * Bundle argument
     *
     * @param parameter    parameter
     * @param javaType     java type
     * @param inputMessage input message
     * @param annotation   request single param
     * @return the object
     * @since 1.4.0
     */
    @SneakyThrows
    @Override
    @SuppressWarnings("unchecked")
    protected Object bundleArgument(MethodParameter parameter,
                                    JavaType javaType,
                                    @NotNull HttpInputMessage inputMessage,
                                    @NotNull RequestSingleParam annotation) {

        String key = annotation.value();
        Map<Object, Object> map = JsonUtils.toMap(inputMessage.getBody(), String.class, Object.class);

        if (annotation.required() && ObjectUtils.isNull(map.get(key))) {
            throw new BaseException("[{}] is required", annotation.value());
        }

        Class<?> rawClass = javaType.getRawClass();
        // 如果是 EntityEnum 类型, 则先使用 value() 进行转换, 然后是 name(), 最后才是 ordinal()
        if (SerializeEnum.class.isAssignableFrom(rawClass)) {
            return this.convert((Class<? extends SerializeEnum<?>>) rawClass, map.get(key));
        }
        return DataTypeUtils.convert(rawClass, map.get(key));
    }

}
