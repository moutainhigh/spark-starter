package info.spark.starter.mybatis.handler;

import org.apache.ibatis.reflection.MetaObject;

import java.util.Objects;

/**
 * <p>Description: 元数据责任链接口 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.04.07 20:36
 * @since 1.8.0
 */
public interface MetaObjectChain {

    /**
     * 新增数据执行
     *
     * @param metaObject meta object
     * @param chain      chain
     * @since 1.8.0
     */
    default void insertFill(MetaObject metaObject, MetaObjectChain chain) {}

    /**
     * 更新元对象字段填充（用于更新时对公共字段的填充）
     *
     * @param metaObject 元对象
     * @param chain      chain
     * @since 1.8.0
     */
    default void updateFill(MetaObject metaObject, MetaObjectChain chain) {}

    /**
     * Sets field val by name *
     *
     * @param fieldName  field name
     * @param fieldVal   field val
     * @param metaObject meta object
     * @since 1.8.0
     */
    default void setFieldValByName(String fieldName, Object fieldVal, MetaObject metaObject) {
        if (Objects.nonNull(fieldVal) && metaObject.hasSetter(fieldName)) {
            metaObject.setValue(fieldName, fieldVal);
        }
    }
}
