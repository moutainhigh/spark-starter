package info.spark.starter.metrics.autoconfigure;

import info.spark.starter.basic.constant.BasicConstant;
import info.spark.starter.common.start.SparkAutoConfiguration;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;

import java.util.concurrent.ThreadPoolExecutor;

import cn.hippo4j.starter.core.DynamicThreadPool;
import cn.hippo4j.starter.enable.EnableDynamicThreadPool;
import cn.hippo4j.starter.toolkit.thread.ThreadPoolBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.04 22:32
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(MetricsProperties.class)
public class MetricsAutoConfiguration implements SparkAutoConfiguration {

    /**
         * <p>Description: https://github.com/acmenlt/dynamic-threadpool </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2021.12.28 17:27
     * @since 2.1.0
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(EnableDynamicThreadPool.class)
    static class DynamicThreadPoolExecutor implements SparkAutoConfiguration {
        /**
         * 动态线程池，可通过 web 修改线程池参数
         *
         * @param taskDecorator task decorator
         * @return the thread pool executor
         * @since 2.1.0
         */
        @DynamicThreadPool
        @Bean(name = BasicConstant.DYNAMIC_EXECUTOR)
        @ConditionalOnProperty(value = "spring.dynamic.thread-pool.enable", havingValue = "true")
        public ThreadPoolExecutor globalDynamicThreadPoolExecutor(@NotNull ObjectProvider<TaskDecorator> taskDecorator) {
            log.info("加载动态线程池: [{}]", BasicConstant.DYNAMIC_EXECUTOR);
            return ThreadPoolBuilder.builder()
                .threadFactory("global-dynamic")
                .dynamicPool()
                .waitForTasksToCompleteOnShutdown(true)
                .awaitTerminationMillis(5000)
                .taskDecorator(taskDecorator.getIfUnique())
                .build();
        }
    }
}
