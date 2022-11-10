package info.spark.agent.core;

import info.spark.agent.entity.ApiExtend;

/**
 * <p>Description: 业务接口, 统一输入输出 </p>
 *
 * @param <I> parameter
 * @param <O> parameter
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.30 06:01
 * @since 1.0.0
 */
public interface ApiService<I, O> extends ApiServiceDefinition {

    /**
     * Handler o
     *
     * @param i      入参
     * @param extend extend
     * @return the o
     * @since 1.0.0
     */
    O handler(I i, ApiExtend extend);

    /**
     * Handler
     *
     * @param i 入参
     * @return the o 出参
     * @since 1.7.0
     */
    default O handler(I i) {
        return this.handler(i, new ApiExtend());
    }

    /**
     * Service o
     *
     * @param i      入参
     * @param extend extend
     * @return the o
     * @since 1.0.0
     */
    O service(I i, ApiExtend extend);

    /**
     * Service
     *
     * @param i 入参
     * @return the o 出参
     * @since 1.7.0
     */
    default O service(I i) {
        return this.service(i, new ApiExtend());
    }

}
