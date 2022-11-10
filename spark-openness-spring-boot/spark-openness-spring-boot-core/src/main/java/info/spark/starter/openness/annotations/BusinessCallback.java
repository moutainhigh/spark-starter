package info.spark.starter.openness.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description: 第三方回调权限验证跳过注解 </p>
 *
 * @author liujintao
 * @version 1.6.0
 * @email "mailto:liujintao@gmail.com"
 * @date 2020.07.23 16:39
 * @since 1.6.0
 */
@Target(value = {ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BusinessCallback {
}
