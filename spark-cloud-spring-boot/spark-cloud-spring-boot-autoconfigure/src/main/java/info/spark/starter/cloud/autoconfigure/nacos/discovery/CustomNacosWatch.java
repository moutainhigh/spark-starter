/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package info.spark.starter.cloud.autoconfigure.nacos.discovery;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;

import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 官方不解决就自己修复: https://github.com/alibaba/spring-cloud-alibaba/issues/1723 </p>
 *
 * @author xiaojing
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.21 21:46
 * @since 2022.1.1
 */
@Slf4j
@SuppressWarnings("all")
public class CustomNacosWatch implements ApplicationEventPublisherAware, SmartLifecycle {

    /** Listener map */
    private final Map<String, EventListener> listenerMap = new ConcurrentHashMap<>(16);
    /** Running */
    private final AtomicBoolean running = new AtomicBoolean(false);
    /** Nacos watch index */
    private final AtomicLong nacosWatchIndex = new AtomicLong(0);
    /** Publisher */
    private ApplicationEventPublisher publisher;
    /** Watch future */
    private ScheduledFuture<?> watchFuture;
    /** Nacos service manager */
    private final NacosServiceManager nacosServiceManager;
    /** Naming service */
    private final NamingService namingService;
    /** Properties */
    private final NacosDiscoveryProperties properties;
    /** Task scheduler */
    private final ThreadPoolTaskScheduler taskScheduler;

    /**
     * Nacos watch
     *
     * @param nacosServiceManager nacos service manager
     * @param properties          properties
     * @since 2022.1.1
     */
    public CustomNacosWatch(@NotNull NacosServiceManager nacosServiceManager,
                            @NotNull NacosDiscoveryProperties properties) {
        this.nacosServiceManager = nacosServiceManager;
        this.properties = properties;
        this.taskScheduler = getTaskScheduler();
        this.namingService = nacosServiceManager.getNamingService(properties.getNacosProperties());
    }

    /**
     * Gets task scheduler *
     *
     * @return the task scheduler
     * @since 2022.1.1
     */
    private static @NotNull ThreadPoolTaskScheduler getTaskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setBeanName("Nacos-Watch-Task-Scheduler");
        taskScheduler.initialize();
        return taskScheduler;
    }

    /**
     * Sets application event publisher *
     *
     * @param publisher publisher
     * @since 2022.1.1
     */
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * Is auto startup
     *
     * @return the boolean
     * @since 2022.1.1
     */
    @Override
    public boolean isAutoStartup() {
        return true;
    }

    /**
     * Stop
     *
     * @param callback callback
     * @since 2022.1.1
     */
    @Override
    public void stop(Runnable callback) {
        this.stop();
        callback.run();
    }

    /**
     * Start
     *
     * @since 2022.1.1
     */
    @Override
    public void start() {
        if (this.running.compareAndSet(false, true)) {
            EventListener eventListener = listenerMap.computeIfAbsent(buildKey(),
                                                                      event -> (EventListener) event1 -> {
                                                                          if (event1 instanceof NamingEvent) {
                                                                              List<Instance> instances = ((NamingEvent) event1)
                                                                                  .getInstances();
                                                                              Optional<Instance> instanceOptional = selectCurrentInstance(
                                                                                  instances);
                                                                              instanceOptional.ifPresent(currentInstance -> {
                                                                                  resetIfNeeded(currentInstance);
                                                                              });
                                                                          }
                                                                      });

            try {
                namingService.subscribe(properties.getService(), properties.getGroup(),
                                        Collections.singletonList(properties.getClusterName()), eventListener);
            } catch (Exception e) {
                log.error("namingService subscribe failed, properties:{}", properties, e);
            }

            this.watchFuture = this.taskScheduler.scheduleWithFixedDelay(
                this::nacosServicesWatch, this.properties.getWatchDelay());
        }
    }

    /**
     * Build key
     *
     * @return the string
     * @since 2022.1.1
     */
    private String buildKey() {
        return String.join(":", properties.getService(), properties.getGroup());
    }

    /**
     * Reset if needed
     *
     * @param instance instance
     * @since 2022.1.1
     */
    private void resetIfNeeded(Instance instance) {
        if (!properties.getMetadata().equals(instance.getMetadata())) {
            properties.setMetadata(instance.getMetadata());
        }
    }

    /**
     * Select current instance
     *
     * @param instances instances
     * @return the optional
     * @since 2022.1.1
     */
    private Optional<Instance> selectCurrentInstance(List<Instance> instances) {
        return instances.stream()
            .filter(instance -> properties.getIp().equals(instance.getIp())
                                && properties.getPort() == instance.getPort())
            .findFirst();
    }

    /**
     * Stop
     *
     * @since 2022.1.1
     */
    @Override
    public void stop() {
        if (this.running.compareAndSet(true, false)) {
            if (this.watchFuture != null) {
                // shutdown current user-thread,
                // then the other daemon-threads will terminate automatic.
                this.taskScheduler.shutdown();
                this.watchFuture.cancel(true);
            }

            EventListener eventListener = listenerMap.get(buildKey());
            try {
                namingService.unsubscribe(properties.getService(), properties.getGroup(),
                                          Collections.singletonList(properties.getClusterName()), eventListener);
                nacosServiceManager.nacosServiceShutDown();
            } catch (Exception e) {
                log.error("namingService unsubscribe failed, properties:{}", properties,
                          e);
            }
        }
    }

    /**
     * Is running
     *
     * @return the boolean
     * @since 2022.1.1
     */
    @Override
    public boolean isRunning() {
        return this.running.get();
    }

    /**
     * Gets phase *
     *
     * @return the phase
     * @since 2022.1.1
     */
    @Override
    public int getPhase() {
        return 0;
    }

    /**
     * Nacos services watch
     *
     * @since 2022.1.1
     */
    public void nacosServicesWatch() {

        // nacos doesn't support watch now , publish an event every 30 seconds.
        this.publisher.publishEvent(
            new HeartbeatEvent(this, nacosWatchIndex.getAndIncrement()));

    }

}
