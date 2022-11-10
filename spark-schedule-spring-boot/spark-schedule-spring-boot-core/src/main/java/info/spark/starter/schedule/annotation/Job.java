package info.spark.starter.schedule.annotation;

import com.xxl.job.core.handler.annotation.XxlJob;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.08 10:44
 * @since 1.0.0
 */
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@XxlJob("")
public @interface Job {

    /**
     * jobhandler name
     *
     * @return the string
     * @since 1.0.0
     */
    @AliasFor(annotation = XxlJob.class, value = "value")
    String value() default "";

    /**
     * init handler, invoked when JobThread init
     *
     * @return the string
     * @since 1.0.0
     */
    @AliasFor(annotation = XxlJob.class, value = "init")
    String init() default "";

    /**
     * destroy handler, invoked when JobThread destroy
     *
     * @return the string
     * @since 1.0.0
     */
    @AliasFor(annotation = XxlJob.class, value = "destroy")
    String destroy() default "";

    /**
     * 任务描述
     *
     * @return the string
     * @since 1.9.0
     */
    @AliasFor(annotation = XxlJob.class, value = "desc")
    String desc() default "";

    /**
     * 负责人
     *
     * @return the string
     * @since 1.9.0
     */
    @AliasFor(annotation = XxlJob.class, value = "owner")
    String owner() default "";

    /**
     * Cron
     *
     * @return the string
     * @since 1.9.0
     */
    @AliasFor(annotation = XxlJob.class, value = "cron")
    String cron() default "";
}
