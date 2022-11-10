package info.spark.starter.rest.support;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.common.enums.SerializeEnum;
import info.spark.starter.rest.annotation.RequestAbstractForm;
import info.spark.starter.util.core.exception.BaseException;
import info.spark.starter.util.ReflectionUtils;
import info.spark.starter.util.StringUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.http.HttpInputMessage;

import java.io.*;

/**
 * <p>Description: 处理 @RequestAbstractForm, 用于接口接收抽象类, 此注解将自动转换为对应子类 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.03 10:41
 * @since 1.0.0
 */
public class RequestAbstractFormMethodArgumentResolver extends AbstractMethodArgumentResolver<RequestAbstractForm> {

    /**
     * Request single param handler method argument resolver
     *
     * @param objectMapper               object mapper
     * @param globalEnumConverterFactory global enum converter factory
     * @since 1.0.0
     */
    @Contract(pure = true)
    public RequestAbstractFormMethodArgumentResolver(ObjectMapper objectMapper,
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
    protected Class<RequestAbstractForm> supportsAnnotation() {
        return RequestAbstractForm.class;
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
    @SuppressWarnings("unchecked")
    protected Object bundleArgument(@NotNull MethodParameter parameter,
                                    JavaType javaType,
                                    HttpInputMessage inputMessage,
                                    @NotNull RequestAbstractForm annotation) {

        Class<? extends SubClassType> subClass = annotation.value();

        if (!SerializeEnum.class.isAssignableFrom(subClass)) {
            throw new BaseException(
                StringUtils.format("参数转换失败, [{}] 需要实现 SerializeEnum 接口. "
                                   + "RequestAbstractForm.value: [{}]", subClass.getSimpleName(), subClass));
        }
        Object[] parse = new Object[1];
        ReflectionUtils.doWithFields(parameter.getParameter().getType(), field -> {
            ReflectionUtils.makeAccessible(field);
            try {
                Object typeValue = JsonUtils.toMap(inputMessage.getBody(), String.class, Object.class).get(field.getName());
                Class<?> type = field.getType();
                Object convert = this.convert((Class<? extends SerializeEnum<?>>) type, typeValue);
                SubClassType subClassType = (SubClassType) convert;
                parse[0] = JsonUtils.parse(inputMessage.getBody(), subClassType.getSubClass());
            } catch (IOException e) {
                throw new BaseException(e);
            }
        }, field -> field.getType().getName().equals(subClass.getName()));
        return parse[0];
    }

}
