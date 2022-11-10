package info.spark.starter.mongo.conditions.update;

import info.spark.starter.mongo.conditions.AbstractWrapper;
import info.spark.starter.mongo.mapper.Model;

/**
 * Update 条件封装
 *
 * @param <M> parameter
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.17 18:32
 * @since 1.0.0
 */
public class UpdateWrapper<M extends Model<M>> extends AbstractWrapper<M, String, UpdateWrapper<M>>
    implements Update<UpdateWrapper<M>, String> {

    /** serialVersionUID */
    private static final long serialVersionUID = 2484546315309024446L;

    /**
     * Update wrapper
     *
     * @since 1.0.0
     */
    public UpdateWrapper() {
        // 如果无参构造函数, 请注意实体 NULL 情况 SET 必须有否则 SQL 异常
        this(null);
    }

    /**
     * Update wrapper
     *
     * @param entity entity
     * @since 1.0.0
     */
    public UpdateWrapper(M entity) {
        super.setEntity(entity);
    }


    /**
     * 返回一个支持 lambda 函数写法的 wrapper
     *
     * @return the lambda update wrapper
     * @since 1.0.0
     */
    public LambdaUpdateWrapper<M> lambda() {
        return new LambdaUpdateWrapper<>(this.getEntity());
    }

    /**
     * Instance update wrapper
     *
     * @return the update wrapper
     * @since 1.0.0
     */
    @Override
    protected UpdateWrapper<M> instance() {
        return new UpdateWrapper<>(this.getEntity());
    }

}
