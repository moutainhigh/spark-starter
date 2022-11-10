package info.spark.starter.doc.knife4j;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * Enable Knife4j enhanced annotation and use @EnableSwagger2 annotation together.
 *
 * inlude:
 * <ul>
 *     <li>Interface sorting </li>
 *     <li>Interface document download  (word)</li>
 * </ul>
 *
 * @author dong4j
 * @version 1.4.0
 * @email  "mailto:dong4j@gmail.com"
 * @date 2020.05.08 16:00
 * @since 1.4.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
@Documented
public @interface EnableKnife4j {
}
