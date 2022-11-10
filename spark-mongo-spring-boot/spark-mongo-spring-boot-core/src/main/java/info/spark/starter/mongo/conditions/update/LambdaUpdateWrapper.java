package info.spark.starter.mongo.conditions.update;

import info.spark.starter.mongo.conditions.AbstractLambdaWrapper;
import info.spark.starter.mongo.mapper.Model;
import info.spark.starter.mongo.support.SFunction;

/**
 * Lambda 更新封装
 *
 * @param <M> parameter
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.17 18:34
 * @since 1.0.0
 */
public class LambdaUpdateWrapper<M extends Model<M>> extends AbstractLambdaWrapper<M, LambdaUpdateWrapper<M>>
    implements Update<LambdaUpdateWrapper<M>, SFunction<M, ?>> {

    /** serialVersionUID */
    private static final long serialVersionUID = -6599291295058183001L;

    /**
     * 不建议直接 new 该实例, 使用 Wrappers.lambdaUpdate()
     *
     * @since 1.0.0
     */
    public LambdaUpdateWrapper() {
        // 如果无参构造函数, 请注意实体 NULL 情况 SET 必须有否则 SQL 异常
        this((M) null);
    }

    /**
     * 不建议直接 new 该实例, 使用 Wrappers.lambdaUpdate(entity)
     *
     * @param entity entity
     * @since 1.0.0
     */
    public LambdaUpdateWrapper(M entity) {
        super.setEntity(entity);
    }

    /**
     * 不建议直接 new 该实例, 使用 Wrappers.lambdaUpdate(entity)
     *
     * @param entityClass entity class
     * @since 1.0.0
     */
    public LambdaUpdateWrapper(Class<M> entityClass) {
        super.setEntityClass(entityClass);
    }

    /**
     * 不建议直接 new 该实例, 使用 Wrappers.lambdaUpdate(...)
     *
     * @param entity      entity
     * @param entityClass entity class
     * @since 1.0.0
     */
    private LambdaUpdateWrapper(M entity, Class<M> entityClass) {
        super.setEntity(entity);
        super.setEntityClass(entityClass);
    }

    /**
     * Instance lambda update wrapper
     *
     * @return the lambda update wrapper
     * @since 1.0.0
     */
    @Override
    protected LambdaUpdateWrapper<M> instance() {
        return new LambdaUpdateWrapper<>(this.getEntity(), this.getEntityClass());
    }

}
