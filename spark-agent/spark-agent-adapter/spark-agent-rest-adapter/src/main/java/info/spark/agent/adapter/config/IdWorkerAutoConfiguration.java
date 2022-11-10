package info.spark.agent.adapter.config;

import info.spark.starter.id.service.IdService;
import info.spark.starter.id.service.SnowflakeIdServiceImpl;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.concurrent.ThreadLocalRandom;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 分布式 id 生成器配置(v4 默认使用雪花算法) </p>
 *
 * @author dong4j
 * @version 1.6.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.08.31 22:36
 * @since 1.6.0
 */
@Slf4j
@Import(AgentRestProperties.class)
@Configuration
public class IdWorkerAutoConfiguration {

    /**
     * Snowflake id service
     *
     * @param properties properties
     * @return the id service
     * @since 1.6.0
     */
    @Bean
    public @NotNull IdService snowflakeIdService(@NotNull AgentRestProperties properties) {
        if (properties.getMachineId() == 0) {
            properties.setMachineId(ThreadLocalRandom.current().nextLong(1, 1024));
        }
        log.debug("装配分布式 id 生成器: [{}]", properties.getMachineId());
        // 这里需要转化一下, 雪花算法使用的机器 id 不能超过 32
        return new SnowflakeIdServiceImpl(properties.getMachineId() % 32, ThreadLocalRandom.current().nextInt(32));
    }
}
