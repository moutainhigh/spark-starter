package info.spark.starter.rest.utis;

import info.spark.starter.basic.constant.ConfigDefaultValue;
import info.spark.starter.rest.annotation.ResponseWrapper;
import info.spark.starter.rest.annotation.RestControllerWrapper;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Annotation;
import java.util.Objects;

import lombok.experimental.UtilityClass;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.12 19:05
 * @since 1.7.1
 */
@UtilityClass
public class RestUtils {

    /**
     * 只要在类上使用了 RestController, RestControllerWrapper, ResponseWrapper 或在方法上使用了 ResponseBody, ResponseWrapper 都要处理包装.
     *
     * @param returnType return type
     * @return the boolean
     * @since 1.0.0
     */
    public boolean supportsAdvice(@NotNull MethodParameter returnType) {
        Annotation[] annotations = returnType.getDeclaringClass().getAnnotations();
        if (annotations.length > 0) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof RestController
                    || annotation instanceof RestControllerWrapper
                    || annotation instanceof ResponseWrapper) {
                    return true;
                }
            }

            for (Annotation annotation : Objects.requireNonNull(returnType.getMethod()).getAnnotations()) {
                if (annotation instanceof ResponseBody || annotation instanceof ResponseWrapper) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 只处理 ConfigDefaultValue.BASE_PACKAGES 路径下的类
     *
     * @param returnType return type
     * @return the boolean
     * @since 1.7.3
     */
    public boolean sparkClass(@NotNull MethodParameter returnType) {
        return StringUtils.containsIgnoreCase(returnType.getDeclaringClass().getPackage().getName(),
                                              ConfigDefaultValue.BASE_PACKAGES);
    }
}
