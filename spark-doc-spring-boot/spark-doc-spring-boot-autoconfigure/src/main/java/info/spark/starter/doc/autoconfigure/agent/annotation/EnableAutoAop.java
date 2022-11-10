package info.spark.starter.doc.autoconfigure.agent.annotation;

import info.spark.starter.doc.autoconfigure.agent.config.AutoAopSelect;

import org.springframework.context.annotation.AdviceMode;
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
@Import({AutoAopSelect.class})
public @interface EnableAutoAop {

    /**
     * Mode
     *
     * @return the advice mode
     * @since 1.0.0
     */
    AdviceMode mode() default AdviceMode.PROXY;

    /**
     * Proxy target class
     *
     * @return the boolean
     * @since 1.0.0
     */
    boolean proxyTargetClass() default false;
}
