package info.spark.agent.sender.impl;

import com.alibaba.ttl.threadpool.TtlExecutors;
import info.spark.agent.core.AgentProperties;
import info.spark.agent.core.ApiServiceContext;
import info.spark.agent.entity.ApiExtend;
import info.spark.agent.entity.ApiServiceHeader;
import info.spark.agent.entity.ApiServiceRequest;
import info.spark.agent.enums.ModeType;
import info.spark.agent.invoker.ApiServiceInvoker;
import info.spark.agent.sender.AgentService;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;

import java.util.Optional;
import java.util.concurrent.ExecutorService;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 本地依赖进行请求分发(依赖 jar), 在 Spring 初始化此 bean 时, 实例化 {@link ApiServiceContext} </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.30 05:12
 * @since 1.0.0
 */
@Slf4j
public class EmbeddedAgentServiceImpl implements AgentService, InitializingBean {
    /** Api service context */
    private ApiServiceContext apiServiceContext;
    /** Application context */
    private final ApplicationContext applicationContext;
    /** Agent properties */
    private final AgentProperties agentProperties;
    /** Executor */
    private final ExecutorService executor;

    /**
     * Local agent service
     *
     * @param agentProperties      agent properties
     * @param applicationContext   application context
     * @param boostExecutorService boost executor service
     * @since 1.0.0
     */
    @Contract(pure = true)
    public EmbeddedAgentServiceImpl(AgentProperties agentProperties,
                                    ApplicationContext applicationContext,
                                    ExecutorService boostExecutorService) {
        this.agentProperties = agentProperties;
        this.applicationContext = applicationContext;
        this.executor = TtlExecutors.getTtlExecutorService(boostExecutorService);
    }

    /**
     * 初始化 bean 的时候进行处理
     *
     * @since 1.0.0
     */
    @Override
    public void afterPropertiesSet() {
        this.apiServiceContext = ApiServiceContext.getInstance(this.agentProperties);
        this.apiServiceContext.setApplicationContext(this.applicationContext);
        this.ready(this.apiServiceContext);
    }

    /**
     * Ready *
     *
     * @param apiServiceContext api service context
     * @since 1.0.0
     */
    @Override
    public void ready(@NotNull ApiServiceContext apiServiceContext) {
        apiServiceContext.setMode(ModeType.LOCAL);
        apiServiceContext.ready(null);
    }

    /**
     * Send byte [ ]
     *
     * @param apiServiceRequest api service request
     * @return the byte [ ]
     * @since 1.0.0
     */
    @Override
    public byte[] send(ApiServiceRequest apiServiceRequest) {
        return this.send(apiServiceRequest, new ApiServiceHeader());
    }

    /**
     * 通过 servicename 和 version 在缓存中查找具体的服务进行调用
     *
     * @param apiServiceRequest api service request
     * @param apiServiceHeader  api service header
     * @return the byte [ ]
     * @since 1.0.0
     */
    @SneakyThrows
    @Override
    public byte[] send(@NotNull ApiServiceRequest apiServiceRequest, @NotNull ApiServiceHeader apiServiceHeader) {
        ApiServiceInvoker apiServiceInvoker = this.getApiServiceInvoker(apiServiceRequest);

        apiServiceInvoker.check(apiServiceHeader, apiServiceRequest);
        ApiExtend apiExtend = this.buildApiExtend(apiServiceRequest, apiServiceHeader);
        if (log.isDebugEnabled()) {
            log.debug("tenantId:  [{}], clientId: [{}]",
                      apiExtend.getTenantId().orElse(null),
                      apiExtend.getClientId().orElse(""));
        }
        return apiServiceInvoker.invoke(this.executor,
                                        apiServiceRequest.getMessage(),
                                        apiExtend);
    }

    /**
     * 获取 ApiServiceInvoker
     *
     * @param apiServiceRequest api service request
     * @return the api service invoker
     * @since 1.8.0
     */
    private ApiServiceInvoker getApiServiceInvoker(@NotNull ApiServiceRequest apiServiceRequest) {
        // 优先通过 key 从 Code Api Services Map 中获取 Invoker
        ApiServiceInvoker apiServiceInvoker = this.apiServiceContext.findCodeServiceInvoker(apiServiceRequest.getApi(),
                                                                                            apiServiceRequest.getVersion());
        // 如果没有则从 API_SERVICE 中获取， API_SERVICE是最低的内部会 not null 断言
        return Optional
            .ofNullable(apiServiceInvoker)
            .orElseGet(() -> this.apiServiceContext.findApiServiceInvoker(apiServiceRequest.getApi(), apiServiceRequest.getVersion()));
    }

    /**
     * Shutdown
     *
     * @since 1.0.0
     */
    @Override
    public void shutdown() {
        this.apiServiceContext.shutdown();
    }
}
