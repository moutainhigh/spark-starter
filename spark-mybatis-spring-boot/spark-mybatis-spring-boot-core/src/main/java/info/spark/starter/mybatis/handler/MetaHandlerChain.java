package info.spark.starter.mybatis.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import info.spark.starter.util.CollectionUtils;

import org.apache.ibatis.reflection.MetaObject;

import java.util.List;
import java.util.Objects;

import lombok.AllArgsConstructor;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.04.07 20:39
 * @since 1.8.0
 */
@AllArgsConstructor
public class MetaHandlerChain implements MetaObjectHandler {
    /** Chains */
    private final List<MetaObjectChain> chains;

    /**
     * 新增数据执行
     *
     * @param metaObject the meta object
     * @since 1.0.0
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        if (CollectionUtils.isEmpty(this.chains)) {
            return;
        }

        this.chains.stream().filter(Objects::nonNull).forEach(c -> c.insertFill(metaObject, c));
    }

    /**
     * 更新数据执行
     *
     * @param metaObject the meta object
     * @since 1.0.0
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        if (CollectionUtils.isEmpty(this.chains)) {
            return;
        }

        this.chains.stream().filter(Objects::nonNull).forEach(c -> c.updateFill(metaObject, c));
    }

}
