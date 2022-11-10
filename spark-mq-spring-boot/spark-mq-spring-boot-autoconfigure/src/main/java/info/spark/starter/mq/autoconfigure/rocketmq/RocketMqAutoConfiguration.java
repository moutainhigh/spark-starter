package info.spark.starter.mq.autoconfigure.rocketmq;

import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.basic.exception.ServiceInternalException;
import info.spark.starter.common.context.EarlySpringContext;
import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.util.CollectionUtils;
import info.spark.starter.util.StringUtils;
import info.spark.starter.mq.RoleType;
import info.spark.starter.mq.annotation.RocketMqConsumer;
import info.spark.starter.mq.autoconfigure.common.ProviderPropCondition;
import info.spark.starter.mq.autoconfigure.common.RoleTypeCondition;
import info.spark.starter.mq.consumer.AbstractConsumer;
import info.spark.starter.mq.consumer.AbstractRocketPushConsumer;
import info.spark.starter.mq.enums.ConsumeModeEnum;
import info.spark.starter.mq.exception.MessageException;
import info.spark.starter.mq.provider.ErrorRocketMqProvider;
import info.spark.starter.mq.provider.RocketMqProvider;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.14 16:15
 * @since 1.5.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RocketMqProvider.class)
@EnableConfigurationProperties(RocketMqProperties.class)
public class RocketMqAutoConfiguration implements SparkAutoConfiguration {

    /** 维护一份map用于检测是否用同样的consumerGroup订阅了不同的topic+tag */
    private Map<String, String> validConsumerMap;

    /**
     * Init mq consumer
     *
     * @param properties properties
     * @return the abstract consumer
     * @throws Exception exception
     * @since 1.7.0
     */
    @Bean
    @Conditional(value = ConsumerCondition.class)
    public AbstractConsumer initMqConsumer(RocketMqProperties properties) throws Exception {
        log.warn("init mq consumer...[{}]", properties);
        Map<String, Object> beans = EarlySpringContext.getApplicationContext()
            .getBeansWithAnnotation(RocketMqConsumer.class);
        if (CollectionUtils.isNotEmpty(beans)) {
            // 初始化
            this.validConsumerMap = new HashMap<>(beans.size());
            for (Map.Entry<String, Object> entry : beans.entrySet()) {
                this.publishConsumer(properties, entry.getKey(), entry.getValue());
            }
        } else {
            log.error("没有发现 @RocketMqConsumer 标记的MQ消费类, 未初始化 Consumer!");
        }
        // 清空map, 等待回收
        this.validConsumerMap = null;
        return null;
    }

    /**
     * Publish consumer
     *
     * @param properties properties
     * @param beanName   bean name
     * @param bean       bean
     * @throws Exception exception
     * @since 1.7.0
     */
    private void publishConsumer(RocketMqProperties properties, String beanName, Object bean) throws Exception {
        RocketMqConsumer mqConsumer = EarlySpringContext.getApplicationContext()
            .findAnnotationOnBean(beanName, RocketMqConsumer.class);
        if (mqConsumer == null) {
            log.error("在BeanName=[{}]上没有找到 RocketMqConsumer 注解", beanName);
            return;
        }
        if (StringUtils.isEmpty(properties.getNameServerAddress())) {
            log.error("name server address must be defined");
            return;
        }
        Assert.notNull(mqConsumer.consumerGroup(), "consumer's consumerGroup must be defined");
        Assert.notNull(mqConsumer.topic(), "consumer's topic must be defined");
        if (!AbstractRocketPushConsumer.class.isAssignableFrom(bean.getClass())) {
            throw new MessageException(bean.getClass().getName() + " - consumer未实现Consumer抽象类");
        }
        Environment environment = EarlySpringContext.getApplicationContext().getEnvironment();

        String consumerGroup = environment.resolvePlaceholders(mqConsumer.consumerGroup());
        String topic = environment.resolvePlaceholders(mqConsumer.topic());
        String tags = "*";
        if (mqConsumer.tag().length == 1) {
            tags = environment.resolvePlaceholders(mqConsumer.tag()[0]);
        } else if (mqConsumer.tag().length > 1) {
            tags = StringUtils.join(mqConsumer.tag(), "||");
        }

        // 检查consumerGroup
        if (!StringUtils.isEmpty(this.validConsumerMap.get(consumerGroup))) {
            String exist = this.validConsumerMap.get(consumerGroup);
            throw new ServiceInternalException("消费组重复订阅, 请新增消费组用于新的topic和tag组合: " + consumerGroup + "已经订阅了" + exist);
        } else {
            this.validConsumerMap.put(consumerGroup, topic + "-" + tags);
        }

        // 配置push consumer
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
        consumer.setNamesrvAddr(properties.getNameServerAddress());
        consumer.setMessageModel(MessageModel.valueOf(mqConsumer.messageMode().getValue()));
        consumer.subscribe(topic, tags);
        consumer.setInstanceName(UUID.randomUUID().toString());

        @SuppressWarnings("rawtypes") AbstractRocketPushConsumer rocketPushConsumer = (AbstractRocketPushConsumer) bean;
        if (ConsumeModeEnum.CONSUME_MODE_ORDERLY.equals(mqConsumer.consumeMode())) {
            // noinspection unchecked
            consumer.registerMessageListener((MessageListenerOrderly) rocketPushConsumer::processMessage);
        } else {
            // todo-by-zhuBo: 暂时只支持一种消费模式: 有序消费模式
            throw new ServiceInternalException("unknown consume mode ! only support ORDERLY");
        }
        rocketPushConsumer.setConsumer(consumer);
        consumer.start();

        log.warn(String.format("%s is ready to subscribe message", bean.getClass().getName()));
    }

    /**
     * Rocket mq provider
     *
     * @param mqProducer mq producer
     * @return the rocket mq provider
     * @throws MQClientException mq client exception
     * @since 1.7.0
     */
    @Bean
    @ConditionalOnClass(DefaultMQProducer.class)
    @Conditional(value = ProviderCondition.class)
    public RocketMqProvider rocketMqProvider(DefaultMQProducer mqProducer) {
        return new RocketMqProvider(mqProducer);
    }

    /**
     * 如果引用了mq starter，但是没有配置，注入一个运行时打印错误的提供者
     *
     * @return the rocket mq provider
     * @since 1.7.0
     */
    @Bean
    @ConditionalOnClass(DefaultMQProducer.class)
    @ConditionalOnMissingBean(RocketMqProvider.class)
    public RocketMqProvider errorMqProvider() {
        return new ErrorRocketMqProvider();
    }

    /**
     * Init default mq producer
     *
     * @param properties properties
     * @return the default mq producer
     * @throws MQClientException mq client exception
     * @since 1.7.0
     */
    @Bean
    @ConditionalOnClass(DefaultMQProducer.class)
    @Conditional(value = ProviderCondition.class)
    @SuppressWarnings("PMD.LowerCamelCaseVariableNamingRule")
    public DefaultMQProducer initDefaultMQProducer(RocketMqProperties properties) throws MQClientException {
        log.debug("init DefaultMQProducer...");
        DefaultMQProducer producer = new DefaultMQProducer(properties.getProducer().getGroup());
        producer.setNamesrvAddr(properties.getNameServerAddress());
        producer.setInstanceName(properties.getInstanceName());
        producer.setSendMsgTimeout(properties.getProducer().getSendMsgTimeout());
        producer.setRetryTimesWhenSendFailed(properties.getProducer().getRetryTimesWhenSendFailed());
        producer.start();
        log.debug("RocketMqProvider finish...[{}]", producer);
        return producer;
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.07.14 19:05
     * @since 1.5.0
     */
    static class ProviderCondition extends ProviderPropCondition {

        /**
         * Provider condition
         *
         * @since 1.5.0
         */
        ProviderCondition() {
            super(RoleType.PROVIDER, ConfigKey.MqConfigKey.NAME_SERVER_ADDRESS);
        }
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.07.14 19:05
     * @since 1.5.0
     */
    static class ConsumerCondition extends RoleTypeCondition {

        /**
         * Consumer condition
         *
         * @since 1.5.0
         */
        ConsumerCondition() {
            super(RoleType.CONSUMER);
        }
    }

}
