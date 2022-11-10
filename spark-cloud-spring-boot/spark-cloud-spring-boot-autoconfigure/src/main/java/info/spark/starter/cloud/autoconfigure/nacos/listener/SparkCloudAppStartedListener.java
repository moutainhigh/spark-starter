package info.spark.starter.cloud.autoconfigure.nacos.listener;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.Service;
import info.spark.starter.basic.context.ComponentThreadLocal;
import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.basic.util.TimeoutUtils;
import info.spark.starter.cloud.autoconfigure.nacos.CustomNacosNamingMaintainService;
import info.spark.starter.cloud.autoconfigure.nacos.SparkNacosProperties;
import info.spark.starter.common.SparkApplicationListener;
import info.spark.starter.common.constant.App;
import info.spark.starter.common.util.ConfigKit;
import info.spark.starter.util.CollectionUtils;
import info.spark.starter.util.DateUtils;
import info.spark.starter.core.util.NetUtils;
import info.spark.starter.util.ThreadUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 启动完成后向 Nacos 写入实例的元数据 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 19:06
 * @since 1.0.0
 */
@Slf4j
public class SparkCloudAppStartedListener implements SparkApplicationListener {
    /** 分支 */
    private static final String INSTANCE_BUILD_BRANCH = "instance.build.branch";
    /** 当前版本 */
    private static final String INSTANCE_BUILD_VERSION = "instance.build.version";
    /** 构建时间 */
    private static final String INSTANCE_BUILD_TIME = "instance.build.time";
    /** 最后一次提交时间 */
    private static final String INSTANCE_COMMIT_TIME = "instance.commit.time";
    /** 构建者 */
    private static final String INSTANCE_BUILD_USER = "instance.build.user";
    /** 最后一次提交的人 */
    private static final String INSTANCE_COMMIT_USER = "instance.commit.user";
    /** 提交信息 */
    private static final String INSTANCE_COMMIT_MESSAGE = "instance.commit.message";

    /**
     * Gets order *
     *
     * @return the order
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1000;
    }

    /**
     * On context started event *
     *
     * @param event event
     * @since 1.0.0
     */
    @Override
    public void onApplicationStartedEvent(@NotNull ApplicationStartedEvent event) {
        Runner.executeAtLast(this.key(event, this.getClass()), () -> {
            log.info("SparkCloudAppStartedListener running.....");
            try {
                ConfigurableApplicationContext context = event.getApplicationContext();
                final SparkNacosProperties bean = context.getBean(SparkNacosProperties.class);
                if (bean.getEnableUpdateMetadata()) {
                    log.debug("应用启动完成, 开始向 Nacos 写入实例元数据");

                    NacosServiceManager nacosServiceManager = context.getBean(NacosServiceManager.class);
                    NacosConfigProperties nacosConfigProperties = context.getBean(NacosConfigProperties.class);

                    NamingMaintainService namingMaintainService =
                        new CustomNacosNamingMaintainService(nacosConfigProperties.assembleConfigServiceProperties());

                    NamingService namingService =
                        nacosServiceManager.getNamingService(nacosConfigProperties.assembleConfigServiceProperties());

                    NacosDiscoveryProperties nacosDiscoveryProperties = context.getBean(NacosDiscoveryProperties.class);
                    String group = nacosDiscoveryProperties.getGroup();

                    String serviceName = ConfigKit.getAppName();
                    ThreadUtils.submit(() -> this.updateMetadata(namingService, namingMaintainService, serviceName, group));
                    SparkNacosProperties sparkNacosProperties = context.getBean(SparkNacosProperties.class);

                    updateMemory(namingService, namingMaintainService, serviceName, group, sparkNacosProperties.getConfig());
                } else {
                    log.info("[spark.nacos.enable-update-metadata=false], 忽略更新元数据");
                }
            } catch (Throwable ignored) {
                // nothing to do
            }
        });
    }

    /**
     * 更新实例级元数据和服务级元数据
     *
     * @param namingService         naming service
     * @param namingMaintainService naming maintain service
     * @param serviceName           service name
     * @param group                 group
     * @throws Exception exception
     * @since 1.7.3
     */
    private void updateMetadata(NamingService namingService,
                                NamingMaintainService namingMaintainService,
                                String serviceName,
                                String group) throws Exception {

        // 休眠 5 秒以等待应用注册到 nacos
        ThreadUtils.sleep(5000);

        // 更新实例
        boolean usedNacos = TimeoutUtils.process(() -> {
            Instance currentInstance = null;
            try {
                currentInstance = this.getInstance(namingService, serviceName, group);
            } catch (Throwable ignored) {
            }
            if (currentInstance == null) {
                log.warn("获取实例失败: [{}@{}]", group, serviceName);
                return false;
            } else {
                // 写入元数据
                currentInstance.setMetadata(this.getMetadata(currentInstance));
            }

            try {
                namingMaintainService.updateInstance(serviceName,
                                                     group,
                                                     currentInstance);
                log.debug("更新实例级元数据成功: [{}]", currentInstance);
            } catch (Exception e) {
                log.warn("更新实例级元数据失败: [{}]", currentInstance);
            }

            return true;
        }, 5);

        String agentServiceString = getAgentService();

        if (usedNacos) {
            this.updateServiceMetadata(agentServiceString, namingMaintainService, serviceName, group);
        }
    }

    /**
     * Gets agent service *
     *
     * @return the agent service
     * @since 2.1.0
     */
    @NotNull
    private String getAgentService() {
        Map<String, Object> agentServices = ComponentThreadLocal.context().get();
        String agentServiceString = "";
        if (CollectionUtils.isNotEmpty(agentServices)) {
            @SuppressWarnings("unchecked") Map<String, Set<String>> agentServiceList =
                (Map<String, Set<String>>) agentServices.get(ComponentThreadLocal.AGENT_SERVICES);

            agentServiceString = JsonUtils.toJson(agentServiceList);
            agentServiceList.clear();
            agentServices.clear();
        }
        return agentServiceString;
    }

    /**
     * 更新服务级的元数据
     *
     * @param agentServiceString    agent service string
     * @param namingMaintainService naming maintain service
     * @param serviceName           service name
     * @param group                 group
     * @since 1.7.1
     */
    private void updateServiceMetadata(String agentServiceString,
                                       NamingMaintainService namingMaintainService,
                                       String serviceName,
                                       String group) throws Exception {

        TimeoutUtils.process(() -> {
            Service service = null;
            try {
                service = namingMaintainService.queryService(serviceName, group);
            } catch (Throwable ignored) {
            }
            if (service == null) {
                log.warn("获取实例失败: [{}@{}]", group, serviceName);
                return null;
            }

            Map<String, String> metadata = service.getMetadata();
            // todo-dong4j : (2021.01.20 16:24) [写入大量的 agent service 有可能造成 nacos 获取 service 414]
            // metadata.put("", agentServiceString);
            service.setMetadata(metadata);
            try {
                namingMaintainService.updateService(serviceName,
                                                    group,
                                                    0.0F,
                                                    metadata);
                log.trace("更新服务级元数据成功: [{}:{}@{}]", service, group, serviceName);
            } catch (Exception e) {
                log.warn("更新服务级元数据失败: [{}:{}@{}]", service, group, serviceName);
            }

            return null;
        }, 5);
    }

    /**
     * 获取当前应用的实例
     *
     * @param namingService naming service
     * @param serviceName   service name
     * @param group         group
     * @return the instance
     * @throws NacosException nacos exception
     * @since 1.7.1
     */
    @Nullable
    private Instance getInstance(@NotNull NamingService namingService, String serviceName, String group) throws NacosException {
        // 获取一个当前实例
        List<Instance> allInstances = namingService.selectInstances(serviceName,
                                                                    group,
                                                                    true);
        log.trace("{}", allInstances);
        String localHost = NetUtils.getLocalHost();
        Integer port = ConfigKit.getPort();
        // 获取当前应用的实例
        List<Instance> instances = allInstances.stream()
            .filter(i -> localHost.equals(i.getIp()) && i.getPort() == port)
            .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(instances)) {
            log.trace("获取当前实例错误, 未找到实例: [{}@{}@{}:{}]", group, serviceName, localHost, port);
            return null;
        }
        return instances.get(0);
    }

    /**
     * 将引用的 build-info 和 git 信息写入到 Nacos.
     *
     * @param instance instance
     * @return the map
     * @since 1.7.1
     */
    @Contract(pure = true)
    private @NotNull Map<String, String> getMetadata(@NotNull Instance instance) {
        Map<String, String> data = new HashMap<>(instance.getMetadata().size() + 32);
        this.addGitProperties(data);
        this.addBuildInfoProperties(data);
        this.addActuatorInfo(data);
        return data;
    }

    /**
     * Add actuator info
     *
     * @param data data
     * @since 1.7.1
     */
    private void addActuatorInfo(Map<String, String> data) {
        if (!ConfigKit.isProd()
            && WebApplicationType.SERVLET.name().equals(System.getProperty(App.APP_TYPE))
            && System.getProperty(App.START_URL) != null) {
            data.put("rest.actuator.url", System.getProperty(App.START_URL).replace("/info", ""));
            data.put("rest.swagger.url", System.getProperty(App.START_URL).replace("/actuator/info", "/doc.html"));

            if (Boolean.parseBoolean(System.getProperty(App.IS_AGENT_SERVICE))) {
                data.put("agent.ping.url", System.getProperty(App.START_URL).replace("/actuator/info", "/agent/ping"));
            }
        }
    }

    /**
     * 读取 build-info.properties 文件, 此文件会在 package 时自动生成, 且会放在 config 目录下.
     *
     * @param data data
     * @since 1.7.1
     */
    private void addBuildInfoProperties(@NotNull Map<String, String> data) {
        // 获取配置文件路径, 有多种情况 (本地运行, junit 运行, jar 运行)
        String configFilePath = ConfigKit.getConfigPath();
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(configFilePath + App.APP_BULID_INFO_FILE_NAME)) {
            properties.load(fileInputStream);
            data.put("original.artifact.name", String.valueOf(properties.get("build.artifact")));
        } catch (IOException ignored) {
            log.trace("配置目录下未找到: {}", App.APP_BULID_INFO_FILE_NAME);
        }
    }

    /**
     * Add git properties
     *
     * @param data data
     * @since 1.7.1
     */
    private void addGitProperties(Map<String, String> data) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(App.GIT_CONFIG_FILE_NAME);
        if (inputStream != null) {
            try {
                Properties properties = new Properties();
                properties.load(inputStream);
                data.put(INSTANCE_BUILD_BRANCH, String.valueOf(properties.get(App.GitInfo.BRANCH)));
                data.put(INSTANCE_BUILD_VERSION, String.valueOf(properties.get(App.GitInfo.BUILD_VERSION)));
                data.put(INSTANCE_BUILD_TIME, String.valueOf(properties.get(App.GitInfo.BUILD_TIME)));
                data.put(INSTANCE_COMMIT_TIME, String.valueOf(properties.get(App.GitInfo.COMMIT_TIME)));
                data.put(INSTANCE_BUILD_USER, String.valueOf(properties.get(App.GitInfo.BUILD_USER_NAME)));
                data.put(INSTANCE_COMMIT_USER, String.valueOf(properties.get(App.GitInfo.COMMIT_USER_NAME)));
                data.put(INSTANCE_COMMIT_MESSAGE, String.valueOf(properties.get(App.GitInfo.COMMIT_MESSAGE)));
            } catch (IOException ignored) {
                // nothing to do
            }
        } else {
            log.trace("classpath 下未找到: {}", App.GIT_CONFIG_FILE_NAME);
        }
    }

    /**
     * Update memory
     *
     * @param namingService         naming service
     * @param namingMaintainService naming maintain service
     * @param serviceName           service name
     * @param group                 group
     * @param config                config
     * @since 2.1.0
     */
    @SuppressWarnings("all")
    private void updateMemory(NamingService namingService,
                              NamingMaintainService namingMaintainService,
                              String serviceName,
                              String group,
                              SparkNacosProperties.@NotNull Config config) {

        Thread thread = new Thread(() -> {
            for (; ; ) {
                ThreadUtils.safeSleep(Math.max(config.getUpdateMemoryInterval(), 10 * 1000L));
                if (config.isEnableUpdateMemory()) {
                    updateMemory(namingService, namingMaintainService, serviceName, group);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Update memory
     *
     * @param namingService         naming service
     * @param namingMaintainService naming maintain service
     * @param serviceName           service name
     * @param group                 group
     * @since 2.1.0
     */
    private void updateMemory(NamingService namingService, NamingMaintainService namingMaintainService, String serviceName, String group) {
        try {
            Instance currentInstance = this.getInstance(namingService, serviceName, group);
            if (currentInstance != null) {
                Map<String, String> metadata = currentInstance.getMetadata();
                metadata.putAll(getMemoryInfo());
                // 写入元数据
                currentInstance.setMetadata(metadata);
                namingMaintainService.updateInstance(serviceName,
                                                     group,
                                                     currentInstance);
                log.trace("更新实例级元数据内存使用信息成功: [{}]", currentInstance);
            }
        } catch (Throwable ignored) {
            // nothing to do
        }
    }

    /**
     * Get memory info
     *
     * @return the map
     * @since 2.1.0
     */
    @SuppressWarnings("all")
    private @NotNull Map<String, String> getMemoryInfo() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        //椎内存使用情况
        MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
        //初始的总内存
        long totalMemorySize = memoryUsage.getInit();
        //最大可用内存
        long maxMemorySize = memoryUsage.getMax();
        //已使用的内存
        long usedMemorySize = memoryUsage.getUsed();

        String totalMemoryInfo = totalMemorySize / (1024 * 1024) + "M";
        String maxMemoryInfo = maxMemorySize / (1024 * 1024) + "M";
        String freeMemoryInfo = (totalMemorySize - usedMemorySize) / (1024 * 1024) + "M";
        String usedMemoryInfo = usedMemorySize / (1024 * 1024) + "M";

        long maxNonHeap = memoryMXBean.getNonHeapMemoryUsage().getMax();
        String maxMetadata = maxNonHeap == -1L ? "N" : maxNonHeap / (1024 * 1024) + "M";
        long usedNonHead = memoryMXBean.getNonHeapMemoryUsage().getUsed();
        String usedMetadata = usedNonHead / (1024 * 1024) + "M";
        String freeMetadata = maxNonHeap == -1L ? "N" : (maxNonHeap - usedNonHead) / (1024 * 1024) + "M";

        Map<String, String> map = new HashMap<>(2);
        map.put("heap", totalMemoryInfo + "/" + maxMemoryInfo + "/" + freeMemoryInfo + "/" + usedMemoryInfo);
        map.put("nonHeap", maxMetadata + "/" + freeMetadata + "/" + usedMetadata);
        map.put("memoryUpdateTime", DateUtils.format(new Date(), DateUtils.PATTERN_MS_DATETIME));
        return map;
    }
}
