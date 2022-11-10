package info.spark.starter.mybatis.handler;

import info.spark.starter.basic.context.ExpandIds;
import info.spark.starter.basic.util.JsonUtils;

import org.apache.ibatis.reflection.MetaObject;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 处理新增和更新的基础数据填充,配合 BaseEntity 和 MyBatisPlusConfig 使用 </p>
 * {@link info.spark.starter.common.base.BasePO}
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.26 20:16
 * @since 1.0.0
 */
@Slf4j
public class ClientIdMetIdaObjectHandler extends AbstractDataIdMetaObjectHandler {

    /**
     * Sets field value *
     *
     * @param metaObject meta object
     * @param expandIds  expand ids
     * @since 1.8.0
     */
    @Override
    protected void setFieldValue(MetaObject metaObject, ExpandIds expandIds) {
        if (log.isDebugEnabled()) {
            log.debug("自动写入 clientId, originalObject: [{}], clientId: [{}]",
                      JsonUtils.toJson(metaObject.getOriginalObject()),
                      expandIds.getClientId().orElse(null));
        }
        this.setFieldValByName("clientId", expandIds.getClientId().orElse(""), metaObject);
    }

}
