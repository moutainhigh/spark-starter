package info.spark.starter.mq.provider;

import info.spark.starter.common.exception.PropertiesException;

/**
 * <p>Description:  </p>
 *
 * @author wanghao
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.12.08 10:41
 * @since 1.7.0
 */
public interface ErrorMqProvider {

    /** 错误信息 */
    String ERROR_MSG = "错误，mq 组件未进行配置";

    /**
     * Error msg
     *
     * @since 1.7.0
     */
    default void errorMsg() {
        throw new PropertiesException(ERROR_MSG);
    }
}
