package info.spark.starter.openness.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description: open api 注解, 用于open api 接口统计, 鉴权 </p>
 *
 * @author liujintao
 * @version 1.6.0
 * @email "mailto:liujintao@gmail.com"
 * @date 2020.07.23 16:31
 * @since 1.6.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.TYPE})
public @interface Openness {
}
