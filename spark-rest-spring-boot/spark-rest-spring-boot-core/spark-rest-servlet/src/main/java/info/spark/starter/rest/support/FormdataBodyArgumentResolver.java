package info.spark.starter.rest.support;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.spark.starter.basic.util.IoUtils;
import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.common.enums.SerializeEnum;
import info.spark.starter.core.util.WebUtils;
import info.spark.starter.rest.annotation.FormDataBody;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.http.HttpInputMessage;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 处理 @FormDataBody, 用于将 formdata 数据转换为实体 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.03 10:41
 * @since 1.0.0
 */
@Slf4j
public class FormdataBodyArgumentResolver extends AbstractMethodArgumentResolver<FormDataBody> {

    /**
     * Request single param handler method argument resolver
     *
     * @param objectMapper               object mapper
     * @param globalEnumConverterFactory global enum converter factory
     * @since 1.0.0
     */
    @Contract(pure = true)
    public FormdataBodyArgumentResolver(ObjectMapper objectMapper,
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
    protected Class<FormDataBody> supportsAnnotation() {
        return FormDataBody.class;
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
    @Override
    protected Object bundleArgument(@NotNull MethodParameter parameter,
                                    @NotNull JavaType javaType,
                                    @NotNull HttpInputMessage inputMessage,
                                    @NotNull FormDataBody annotation) {

        Class<?> rawClass = javaType.getRawClass();

        try {
            return JsonUtils.parse(JsonUtils.toJson(WebUtils.converterToMap(IoUtils.toString(inputMessage.getBody()))), rawClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
