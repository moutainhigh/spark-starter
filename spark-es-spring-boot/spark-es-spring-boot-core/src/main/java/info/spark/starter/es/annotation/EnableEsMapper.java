package info.spark.starter.es.annotation;

import info.spark.starter.es.register.EsProMapperRegister;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description:  </p>
 *
 * @author wanghao
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.11.27 15:10
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({EsProMapperRegister.class})
public @interface EnableEsMapper {

    /**
     * Mapper package
     *
     * @return the string
     * @since 1.7.1
     */
    String mapperPackage() default "info.spark";
}
