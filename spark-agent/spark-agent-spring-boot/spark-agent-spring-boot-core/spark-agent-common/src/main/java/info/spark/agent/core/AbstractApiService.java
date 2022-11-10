package info.spark.agent.core;

import info.spark.agent.entity.ApiExtend;
import info.spark.agent.exception.AgentServiceException;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 抽象业务类 </p>
 *
 * @param <I> parameter  入参
 * @param <O> parameter  出参
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.30 06:01
 * @since 1.0.0
 */
@Slf4j
public abstract class AbstractApiService<I, O> implements ApiService<I, O> {

    /**
     * 预留模板代理, 处理业务逻辑入口.
     *
     * @param i      int
     * @param extend extend
     * @return o out
     * @see ApiServiceContext#registerInvokers
     * @since 1.0.0
     */
    @SuppressWarnings("JavadocReference")
    @Override
    public O handler(I i, ApiExtend extend) {
        return this.service(i, extend);
    }

    /**
     * 业务方需要实现的具体业务逻辑
     *
     * @param i      int
     * @param extend extend
     * @return o out
     * @throws AgentServiceException agent exception
     * @since 1.0.0
     */
    @Override
    public abstract O service(I i, ApiExtend extend) throws AgentServiceException;
}
