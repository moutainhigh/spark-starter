package info.spark.agent.adapter.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import info.spark.agent.adapter.feign.Client;
import info.spark.feign.adapter.annotation.FeignClient;
import info.spark.feign.adapter.config.FeignClientAdapterConfiguration;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 在启动时通过配置检查 feign client 连通性 </p>
 *
 * @author dong4j
 * @version 1.0.4
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.19 06:07
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ComponentScan("info.spark.agent.adapter")
@Import(FeignClientAdapterConfiguration.class)
public class FeignClientAgentConfiguration implements InitializingBean, ApplicationContextAware {
    /** Feign adapter properties */
    private FeignClientAgentProperties feignClientAgentProperties;
    /** Application context */
    private ApplicationContext applicationContext;
    /** AGENT_FEIGN_CLIENT_SUPER_INTERFACE_NAME */
    private static final String AGENT_FEIGN_CLIENT_SUPER_INTERFACE_NAME = "info.spark.agent.adapter.feign.Client";

    /**
     * feign client 检查接口
     *
     * @see Client#ping()
     */
    private static final String CHECK_METHOD_NAME = "ping";

    /**
     * Adapter configuration
     *
     * @since 1.0.0
     */
    public FeignClientAgentConfiguration() {
        log.warn("加载 Feign Client Agent 自动装配类: [{}]", FeignClientAgentConfiguration.class);
    }

    /**
     * Sets application context *
     *
     * @param context context
     * @throws BeansException beans exception
     * @since 1.0.0
     */
    @Override
    public void setApplicationContext(@NotNull ApplicationContext context) throws BeansException {
        this.applicationContext = context;
        this.feignClientAgentProperties = context.getBean(FeignClientAgentProperties.class);
    }

    /**
     * 通过配置判断是否在启动时检查所有已注册的 Feign Client 是否正常可用.
     * 会启动一个线程池加快检查速度, 所有 client 检查完成后再执行主线程,
     * 底层调用的 time() 接口进行连通性检查.
     *
     * @see Client#ping() Client#ping()Client#ping()
     * @since 1.0.0
     */
    @Override
    public void afterPropertiesSet() {
        if (this.feignClientAgentProperties.isEnableCheck()) {
            Map<String, Object> beansWithAnnotation = this.applicationContext.getBeansWithAnnotation(FeignClient.class);

            ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("check-%d").build();

            ExecutorService pool = new ThreadPoolExecutor(5,
                                                          10,
                                                          0L,
                                                          TimeUnit.MILLISECONDS,
                                                          new LinkedBlockingQueue<>(1024),
                                                          namedThreadFactory,
                                                          new ThreadPoolExecutor.CallerRunsPolicy());

            for (Map.Entry<String, Object> entry : beansWithAnnotation.entrySet()) {
                String clientName = entry.getKey();
                Object client = entry.getValue();
                pool.execute(() -> FeignClientAgentConfiguration.this.check(clientName, client));
            }
            // 关闭后不能再提交任务, 不影响任务执行
            pool.shutdown();

            try {
                boolean loop;
                do {
                    // 任务完成返回 true, 超时未完成返回 false, 中断抛出异常
                    loop = !pool.awaitTermination(5, TimeUnit.SECONDS);
                } while (loop);
            } catch (InterruptedException ignored) {
            } finally {
                pool.shutdownNow();
            }
        }
    }

    /**
     * 通过反射调用 client 的 ping 接口, 检查连接是否可用
     *
     * @param clientName client name
     * @param client     client
     * @see FeignClientAgentConfiguration#AGENT_FEIGN_CLIENT_SUPER_INTERFACE_NAME
     * @since 1.0.0
     */
    private void check(String clientName, Object client) {
        try {
            // 只有 Agent Client 的子类才检查
            Class<?>[] interfaces = Class.forName(clientName).getInterfaces();
            // feign client 接口最多只能继承一个接口
            if (interfaces.length == 1 && AGENT_FEIGN_CLIENT_SUPER_INTERFACE_NAME.equals(interfaces[0].getName())) {
                // 此 clazz 是 jdk 的代理类, 代理了 Feign Client 接口的实现类
                Class<?> clazz = client.getClass();
                Method method = clazz.getMethod(CHECK_METHOD_NAME);
                log.info("Check successed: [{}]: [{}]", clientName, method.invoke(client).toString());
            } else if (interfaces.length > 1) {
                throw new RuntimeException("Feign Client 不允许多重继承, 最多继承: " + AGENT_FEIGN_CLIENT_SUPER_INTERFACE_NAME);
            } else {
                log.warn("[{}] 未继承 [{}], 忽略检查", clientName, AGENT_FEIGN_CLIENT_SUPER_INTERFACE_NAME);
            }
        } catch (Exception e) {
            log.warn("Check error: [{}]: [{}]", clientName, e.getMessage());
        }
    }
}
