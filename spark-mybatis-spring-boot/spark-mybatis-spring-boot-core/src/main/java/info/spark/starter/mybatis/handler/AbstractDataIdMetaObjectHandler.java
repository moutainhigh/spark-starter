package info.spark.starter.mybatis.handler;

import info.spark.starter.basic.context.ExpandIds;
import info.spark.starter.basic.context.ExpandIdsContext;

import org.apache.ibatis.reflection.MetaObject;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.05.13 18:10
 * @since 1.8.0
 */
@Slf4j
public abstract class AbstractDataIdMetaObjectHandler implements MetaObjectChain {

    /**
     * 新增数据执行
     *
     * @param metaObject meta object
     * @param chain      chain
     * @since 1.8.0
     */
    @Override
    public void insertFill(MetaObject metaObject, MetaObjectChain chain) {
        // agent 解析的 header，同一对象，不清除，只获取值.
        ExpandIds expandIds = ExpandIdsContext.context().get();
        if (null == expandIds) {
            log.debug("无法从当前线程获取 expandIds");
            return;
        }
        this.setFieldValue(metaObject, expandIds);
    }

    /**
     * Sets field value *
     *
     * @param metaObject meta object
     * @param expandIds  expand ids
     * @since 1.8.0
     */
    protected abstract void setFieldValue(MetaObject metaObject, ExpandIds expandIds);
}
