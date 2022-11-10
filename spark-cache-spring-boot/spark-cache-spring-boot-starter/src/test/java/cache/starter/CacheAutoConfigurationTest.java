package cache.starter;

import info.spark.starter.cache.RedisMessageSubscriber;
import info.spark.starter.cache.autoconfigure.RedisMessageListenerContainerCustomizer;
import info.spark.starter.cache.service.CacheService;
import info.spark.starter.launcher.SparkStarter;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.12.11 13:38
 * @since 1.7.0
 */
@Slf4j
@SpringBootApplication
@PropertySource(value = "classpath:application.yml")
class CacheAutoConfigurationTest extends SparkStarter {
    @Resource
    private CacheService cacheService;

    /**
     * 自定义 container
     *
     * @return the redis message listener container customizer
     * @since 1.7.0
     */
    @Bean
    public RedisMessageListenerContainerCustomizer redisMessageListenerContainerCustomizer() {
        return (container) -> container.addMessageListener(new MessageListenerAdapter(), new PatternTopic("xxxx"));
    }

    /**
     * 自定义redis 消费者
     *
     * @return the redis message subscriber
     * @since 1.7.0
     */
    @Bean
    public RedisMessageSubscriber<?> redisMessageSubscriber() {

        return new RedisMessageSubscriber<Exception>("xxxx") {
            @Override
            public void handle(Exception o) {
                log.error(o.toString());
            }
        };
    }

    @Resource
    private RedisMessageListenerContainer container;
    /** Redis template */
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @since 1.7.0
     */
    @Override
    public void run(String... args) {
        log.info("{}", this.container);
        this.redisTemplate.convertAndSend("topic.xxxx", new Exception("222"));
        this.cacheService.publish("xxxx", new Exception("222"));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
