package info.spark.starter.mongo.util;

import com.google.common.collect.Lists;

import info.spark.starter.mongo.conditions.query.LambdaQueryWrapper;
import info.spark.starter.mongo.conditions.query.QueryWrapper;
import info.spark.starter.mongo.conditions.update.LambdaUpdateWrapper;
import info.spark.starter.mongo.conditions.update.UpdateWrapper;
import info.spark.starter.mongo.enums.Operator;
import info.spark.starter.mongo.mapper.Model;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.Arrays;
import java.util.List;

/**
 * <p>Description: Wrapper 条件构造 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.17 06:36
 * @since 1.0.0
 */
public final class Wrappers {

    /** 空的 EmptyWrapper */
    private static final QueryWrapper<?> EMPTY_WRAPPER = new EmptyWrapper<>();

    /**
     * Wrappers
     *
     * @since 1.0.0
     */
    @Contract(pure = true)
    private Wrappers() {
    }

    /**
     * 获取 QueryWrapper&lt;T&gt;
     *
     * @param <M> 实体类泛型
     * @return QueryWrapper &lt;T&gt;
     * @since 1.0.0
     */
    @NotNull
    @Contract(" -> new")
    public static <M extends Model<M>> QueryWrapper<M> query() {
        return new QueryWrapper<>();
    }

    /**
     * 获取 QueryWrapper&lt;T&gt;
     *
     * @param <M>    实体类泛型
     * @param entity 实体类
     * @return QueryWrapper &lt;T&gt;
     * @since 1.0.0
     */
    @NotNull
    @Contract("_ -> new")
    public static <M extends Model<M>> QueryWrapper<M> query(M entity) {
        return new QueryWrapper<>(entity);
    }

    /**
     * 获取 LambdaQueryWrapper&lt;T&gt;
     *
     * @param <M> 实体类泛型
     * @return LambdaQueryWrapper &lt;T&gt;
     * @since 1.0.0
     */
    @NotNull
    @Contract(" -> new")
    public static <M extends Model<M>> LambdaQueryWrapper<M> lambdaQuery() {
        return new LambdaQueryWrapper<>();
    }

    /**
     * 获取 LambdaQueryWrapper&lt;T&gt;
     *
     * @param <M>    实体类泛型
     * @param entity 实体类
     * @return LambdaQueryWrapper &lt;T&gt;
     * @since 1.0.0
     */
    @NotNull
    @Contract("_ -> new")
    public static <M extends Model<M>> LambdaQueryWrapper<M> lambdaQuery(M entity) {
        return new LambdaQueryWrapper<>(entity);
    }

    /**
     * 获取 LambdaQueryWrapper&lt;T&gt;
     *
     * @param <M>         实体类泛型
     * @param entityClass 实体类class
     * @return LambdaQueryWrapper &lt;T&gt;
     * @since 3.3.1
     */
    @NotNull
    @Contract("_ -> new")
    public static <M extends Model<M>> LambdaQueryWrapper<M> lambdaQuery(Class<M> entityClass) {
        return new LambdaQueryWrapper<>(entityClass);
    }

    /**
     * 获取 UpdateWrapper&lt;T&gt;
     *
     * @param <M> 实体类泛型
     * @return UpdateWrapper &lt;T&gt;
     * @since 1.0.0
     */
    @NotNull
    @Contract(" -> new")
    public static <M extends Model<M>> UpdateWrapper<M> update() {
        return new UpdateWrapper<>();
    }

    /**
     * 获取 UpdateWrapper&lt;T&gt;
     *
     * @param <M>    实体类泛型
     * @param entity 实体类
     * @return UpdateWrapper &lt;T&gt;
     * @since 1.0.0
     */
    @NotNull
    @Contract("_ -> new")
    public static <M extends Model<M>> UpdateWrapper<M> update(M entity) {
        return new UpdateWrapper<>(entity);
    }

    /**
     * 获取 LambdaUpdateWrapper&lt;T&gt;
     *
     * @param <M> 实体类泛型
     * @return LambdaUpdateWrapper &lt;T&gt;
     * @since 1.0.0
     */
    @NotNull
    @Contract(" -> new")
    public static <M extends Model<M>> LambdaUpdateWrapper<M> lambdaUpdate() {
        return new LambdaUpdateWrapper<>();
    }

    /**
     * 获取 LambdaUpdateWrapper&lt;T&gt;
     *
     * @param <M>    实体类泛型
     * @param entity 实体类
     * @return LambdaUpdateWrapper &lt;T&gt;
     * @since 1.0.0
     */
    @NotNull
    @Contract("_ -> new")
    public static <M extends Model<M>> LambdaUpdateWrapper<M> lambdaUpdate(M entity) {
        return new LambdaUpdateWrapper<>(entity);
    }

    /**
     * 获取 LambdaUpdateWrapper&lt;T&gt;
     *
     * @param <M>         实体类泛型
     * @param entityClass 实体类class
     * @return LambdaUpdateWrapper &lt;T&gt;
     * @since 3.3.1
     */
    @NotNull
    @Contract("_ -> new")
    public static <M extends Model<M>> LambdaUpdateWrapper<M> lambdaUpdate(Class<M> entityClass) {
        return new LambdaUpdateWrapper<>(entityClass);
    }

    /**
     * 获取 EmptyWrapper&lt;T&gt;
     *
     * @param <M> 任意泛型
     * @return EmptyWrapper &lt;T&gt;
     * @see EmptyWrapper
     * @since 1.0.0
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <M extends Model<M>> QueryWrapper<M> emptyWrapper() {
        return (QueryWrapper<M>) EMPTY_WRAPPER;
    }

    /**
     * 一个空的QueryWrapper子类该类不包含任何条件
     *
     * @param <M> parameter
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.03.17 18:30
     * @since 1.0.0
     */
    private static class EmptyWrapper<M extends Model<M>> extends QueryWrapper<M> {

        /** serialVersionUID */
        private static final long serialVersionUID = -2515957613998092272L;

        /**
         * Gets entity *
         *
         * @return the entity
         * @since 1.0.0
         */
        @Override
        public M getEntity() {
            return null;
        }

        /**
         * Sets entity *
         *
         * @param entity entity
         * @return the entity
         * @since 1.0.0
         */
        @Override
        public EmptyWrapper<M> setEntity(M entity) {
            throw new UnsupportedOperationException();
        }

        /**
         * Sets entity class *
         *
         * @param entityClass entity class
         * @return the entity class
         * @since 1.0.0
         */
        @Override
        public QueryWrapper<M> setEntityClass(Class<M> entityClass) {
            throw new UnsupportedOperationException();
        }

        /**
         * Gets entity class *
         *
         * @return the entity class
         * @since 1.0.0
         */
        @Override
        protected Class<M> getEntityClass() {
            return null;
        }

        /**
         * Instance empty wrapper
         *
         * @return the empty wrapper
         * @since 1.0.0
         */
        @Override
        protected EmptyWrapper<M> instance() {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * Gen or criteria list
     *
     * @param key   key
     * @param value value
     * @return the list
     * @since 1.0.0
     */
    public static List<Criteria> genOrCriteria(String[] key, @NotNull Object[] value) {
        key = FieldConvertUtils.convert(key);
        List<Criteria> criterias = Lists.newArrayListWithExpectedSize(key.length);
        for (int i = 0; i < value.length; i++) {
            criterias.add(Criteria.where(key[i]).is(value[i]));
        }
        return criterias;
    }

    /**
     * Gen or criteria list
     *
     * @param key   key
     * @param value value
     * @return the list
     * @since 1.0.0
     */
    public static List<Criteria> genOrCriteria(String key, @NotNull Object[] value) {
        List<Criteria> criterias = Lists.newArrayListWithExpectedSize(value.length);
        Arrays.stream(value).forEach(o -> criterias.add(Criteria.where(FieldConvertUtils.convert(key)).is(o)));
        return criterias;
    }

    /**
     * Gen or criteria list
     *
     * @param key       key
     * @param value     value
     * @param operators operators
     * @return the list
     * @since 1.0.0
     */
    public static List<Criteria> genOrCriteria(String[] key, @NotNull Object[] value, Operator[] operators) {
        key = FieldConvertUtils.convert(key);
        List<Criteria> criterias = Lists.newArrayListWithExpectedSize(value.length);
        for (int i = 0; i < value.length; i++) {
            if (Operator.in.equals(operators[i])) {
                Object[] tmp = (Object[]) value[i];
                criterias.add(Criteria.where(key[i]).in(tmp));
            }
            if (Operator.nin.equals(operators[i])) {
                Object[] tmp = (Object[]) value[i];
                criterias.add(Criteria.where(key[i]).nin(tmp));
            }
            if (Operator.regex.equals(operators[i])) {
                String re = String.valueOf(value[i]);
                re = re.replace("(", "\\(").replace(")", "\\)");
                criterias.add(Criteria.where(key[i]).regex(re, "i"));
            }
            if (Operator.eq.equals(operators[i])) {
                criterias.add(Criteria.where(key[i]).is(value[i]));
            }
            if (Operator.gt.equals(operators[i])) {
                criterias.add(Criteria.where(key[i]).gt(value[i]));
            }
            if (Operator.lt.equals(operators[i])) {
                criterias.add(Criteria.where(key[i]).lt(value[i]));
            }
            if (Operator.gte.equals(operators[i])) {
                criterias.add(Criteria.where(key[i]).gte(value[i]));
            }
            if (Operator.lte.equals(operators[i])) {
                criterias.add(Criteria.where(key[i]).lte(value[i]));
            }
            if (Operator.ne.equals(operators[i])) {
                criterias.add(Criteria.where(key[i]).ne(value[i]));
            }
        }
        return criterias;
    }

}
