package info.spark.starter.es.annotation;

import info.spark.starter.es.enums.ESMappingType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description: es mapping </p>
 *
 * @author wanghao
 * @version 1.8.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.25 17:39
 * @since 1.8.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EsMapping {

    /**
     * Value
     *
     * @return the es mapping type
     * @since 1.8.0
     */
    //映射类型
    ESMappingType value() default ESMappingType.keyword;

    /**
     * Boost
     *
     * @return the int
     * @since 1.8.0
     */
    //加权
    int boost() default 1;

    /**
     * Index
     *
     * @return the string
     * @since 1.8.0
     */
    //分词标识analyzed、not_analyzed
    String index() default "analyzed";

    /**
     * Analyzer
     *
     * @return the string
     * @since 1.8.0
     */
    //分词器ik_max_word、standard
    String analyzer() default "ik_max_word";

    /**
     * Field data
     *
     * @return the boolean
     * @since 1.8.0
     */
    //String作为分组聚合字段的时候需要设置为true
    boolean fieldData() default false;
}
