package info.spark.starter.openness.service;

import info.spark.starter.openness.enums.AlgorithmEnum;

import org.jetbrains.annotations.NotNull;

/**
 * <p>Description:  </p>
 *
 * @author zhubo
 * @version 1.6.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.08.13 14:38
 * @since 1.6.0
 */
public abstract class AbstractOpennessClient {
    /**
     * 加解密算法名称 *
     *
     * @return the algorithm
     * @since 1.6.0
     */
    public abstract AlgorithmEnum getAlgorithm();

    /**
     * 获取签名, 按照指定加密算法
     *
     * @param params 动态参数: 默认实现中, 第一个参数是签名key, 第二个参数是请求参数
     * @return the string
     * @since 1.6.0
     */
    public abstract String generateSign(@NotNull String... params);

    /**
     * 加密字符串
     *
     * @param params 动态参数: 默认实现中, 第一个参数是待加密的内容, 第二个参数是加密的key
     * @return the string
     * @since 1.6.0
     */
    public abstract String encryptString(@NotNull String... params);
}
