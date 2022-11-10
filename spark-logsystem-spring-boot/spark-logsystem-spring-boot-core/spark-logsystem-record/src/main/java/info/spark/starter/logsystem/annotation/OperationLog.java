package info.spark.starter.logsystem.annotation;

import info.spark.starter.logsystem.enums.OperationAction;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description: 操作日志注解 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 04:54
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /**
     * 动作
     *
     * @return the operation action
     * @since 1.5.0
     */
    OperationAction action() default OperationAction.DEFAULT;
    /**
     * 操作描述, 支持SpEL表达式调用输出方法中的参数值,
     * 如:\n
     * 1、普通参数phone的值: '用户登录手机号: #{#phone}'
     * 2、对象参数UserDTO的值:' 用户名#{#userDTO.name}, 密码: #{#userDTO.password}'
     * 3、时间expiryDate的值:'有效期: #{#dateFormat(#expiryDate)}'
     *
     * @return {String}
     * @since 1.0.0
     */
    String value() default "操作日志记录";
}
