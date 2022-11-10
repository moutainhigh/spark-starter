package info.spark.agent.validation;

import info.spark.starter.basic.util.StringUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.annotation.Validated;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.31 11:37
 * @since 1.9.0
 */
@Slf4j
public class Validater {
    /** Validator */
    private final Validator validator;

    /**
     * Hibernate validate
     *
     * @param validator validator
     * @since 1.9.0
     */
    @Contract(pure = true)
    public Validater(Validator validator) {
        this.validator = validator;
    }

    /**
     * Validate list
     *
     * @param o      o
     * @param groups groups
     * @return the list
     * @since 1.9.0
     */
    public List<ValidateMessage> validate(Object o, Class<?>... groups) {
        List<ValidateMessage> validateMessages = new ArrayList<>(4);
        if (o == null) {
            validateMessages.add(ValidateMessage.builder()
                                     .property("Object")
                                     // info.spark.starter.util.core.api.BaseCodes.PARAM_VERIFY_ERROR
                                     .code(4100)
                                     .message("数据为空")
                                     .build());
            return validateMessages;
        }
        Set<ConstraintViolation<Object>> validResult = this.validator.validate(o, groups);
        if (validResult != null && !validResult.isEmpty()) {
            for (ConstraintViolation<Object> constraintViolation : validResult) {
                validateMessages.add(ValidateMessage.builder()
                                         .property(constraintViolation.getPropertyPath().toString()
                                                   + "("
                                                   + constraintViolation.getInvalidValue()
                                                   + ")")
                                         // info.spark.starter.util.core.api.BaseCodes.PARAM_VERIFY_ERROR
                                         .code(4100)
                                         .message(StringUtils.isBlank(constraintViolation.getMessage())
                                                  ? "不合法"
                                                  : constraintViolation.getMessage())
                                         .build());
            }
        }
        return validateMessages;
    }

    /**
     * 获取方法参数上的注解
     *
     * @param method 要获取参数名的方法
     * @return 按参数顺序排列的参数名列表 class [ ]
     * @since 1.9.0
     */
    @NotNull
    public static Class<?>[] getMethodParameterNamesByAnnotation(@NotNull Method method) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if (parameterAnnotations.length == 0) {
            return new Class[0];
        }
        for (Annotation[] parameterAnnotation : parameterAnnotations) {
            for (Annotation annotation : parameterAnnotation) {
                if (annotation.annotationType().equals(Validated.class)) {
                    Validated validated = (Validated) annotation;
                    return validated.value();
                }
            }
        }

        return new Class[0];
    }
}
