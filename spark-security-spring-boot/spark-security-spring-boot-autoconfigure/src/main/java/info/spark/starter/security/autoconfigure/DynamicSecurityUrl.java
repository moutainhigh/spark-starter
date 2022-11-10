package info.spark.starter.security.autoconfigure;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.exception.NacosException;
import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.basic.util.SecurityUtils;
import info.spark.starter.common.exception.PropertiesException;
import info.spark.starter.common.util.ConfigKit;
import info.spark.starter.common.util.GsonUtils;
import info.spark.starter.security.enums.SecurityCodes;
import info.spark.starter.security.matcher.SparkUrlMatcher;
import info.spark.starter.security.util.SkipRequestMatchers;
import info.spark.starter.util.CollectionUtils;
import info.spark.starter.util.StringUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 动态配置不需要认证的 url</p>
 * 监听 security-ignore-url.yml 配置, 当此配置发生变化时, 发送一个 RefreshEvent 事件,
 * 会在 {@link info.spark.starter.cloud.handler.RefreshEventHandler#handler(RefreshEvent)} 判断是否为当前配置发生变化, 如果是则保存一个标识,
 * 然后在 {@link info.spark.starter.security.handler.RefreshScopeRefreshedEventHandler#handler(RefreshScopeRefreshedEvent)} 中
 * 更新刷新完成的 DynamicIgnoreUrlConfig 配置.
 * fixme-dong4j : (2020年03月20日 19:17) [未使用 nacos 的情况下可能会报错, 待修复]
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:25
 * @since 1.0.0
 */
@Slf4j
@SuppressWarnings("all")
public class DynamicSecurityUrl {
    /**
     * formatter:off
     * 所有 service 都会忽略的 url, 对应 security-ignore-url.yml 中的 spark.security.ignore-url
     * {@code
     * spark:
     * security:
     * ignore-url:
     * global:
     *   - /login
     *   - /logout
     *   - /**
     * spark-mservice-system:
     *   - /user/**
     *   - /menu/list/**
     * spark-mservice-orization:
     *   - /hello/**
     * }
     *
     * @see SecurityProperties#ignoreUrl
     * formatter:on
     */
    public static final String GLOBAL_IGNORE_URL_KEY = "global";
    /** 发送配置刷新消息 */
    private final ApplicationEventPublisher applicationEventPublisher;
    /** Security properties */
    private final SecurityProperties securityProperties;
    /** Namespace */
    private final String namespace;
    /** Server addr */
    private final String serverAddr;
    /** Environment */
    private final ConfigurableEnvironment environment;
    /** 给 WebSecurityConfigurerAdapter 使用的用于设置不需要安全认证的所有 url */
    @Getter
    private final Set<String> allIgnoreUrlList = Sets.newHashSetWithExpectedSize(16);
    /** 存储处理后的 url, 以 service-name 区分不同服务的 url, 全局 url 以 GLOBAL_IGNORE_URL_KEY 为 key, 包含默认的 url */
    @Getter
    private final Map<String, Set<String>> allIgnoreUrlMap = Maps.newHashMapWithExpectedSize(16);
    /**
     * 存储处理后的 Matcher 处理器, 以 service-name 区分不同服务的 SparkUrlMatcher,
     * 全局 SparkUrlMatcher 以 GLOBAL_IGNORE_URL_KEY 为 key, 包含默认的 SparkUrlMatcher
     */
    @Getter
    private final Map<String, Set<SparkUrlMatcher>> allIgnoreUrlMatcherMap = Maps.newHashMapWithExpectedSize(16);

    /**
     * Dynamic security url
     *
     * @param applicationEventPublisher application event publisher
     * @param securityProperties        security properties
     * @param environment               environment
     * @since 1.0.0
     */
    @Contract(pure = true)
    DynamicSecurityUrl(ApplicationEventPublisher applicationEventPublisher,
                       SecurityProperties securityProperties,
                       @NotNull ConfigurableEnvironment environment) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.securityProperties = securityProperties;
        this.environment = environment;
        this.namespace = environment.getProperty(ConfigKey.NacosConfigKey.CONFIG_NAMESPACE,
                                                 environment.getProperty(ConfigKey.CloudConfigKey.CONFIG_NAMESPACE, ""));
        this.serverAddr = environment.getProperty(ConfigKey.NacosConfigKey.CONFIG_SERVER_ADDRESS,
                                                  environment.getProperty(ConfigKey.CloudConfigKey.CONFIG_SERVER_ADDRESS, ""));
    }

    /**
     * 应用启动时更新一下配置, 然后创建监听器, 监听 DEFAULT_DATA_ID 配置的变化, 并更新内存中的配置
     *
     * @since 1.0.0
     */
    @PostConstruct
    public void dynamicIgnoreUrlByNacosListener() {
        // 本地开发不走 naocs
        if (ConfigKit.isLocalLaunch() && this.securityProperties.isEnableLocalIgnoreUrl()) {
            log.warn("本地开发环境, 加载本地 security-ignore-url.yml, 如果本地环境需要读取 Nacos 配置, "
                     + "请删除 [spark.security.enable-local-ignore-url] 或配置 [spark.security.enable-local-ignore-url=false]");

            this.loadIgnoreUrlFromLocalConfigFile(this.processor());
            // todo-dong4j : (2020.12.21 21:56) [监听文件变化, 动态刷新配置]
            return;
        }

        try {
            if (StringUtils.isAnyBlank(this.namespace, this.serverAddr)) {
                throw SecurityCodes.IGNORE_URL_ERROR.newException("[spark.nacos.config.namespace] 和 [spark.nacos.config.server-addr] 必须配置");
            }

            Properties properties = new Properties();
            properties.put(PropertyKeyConst.NAMESPACE, this.namespace);
            properties.put(PropertyKeyConst.SERVER_ADDR, this.serverAddr);
            ConfigService configService = NacosFactory.createConfigService(properties);
            // 初始化一次数据
            this.updateIgnoreUrl();

            configService.addListener(this.securityProperties.getDataId(),
                                      this.securityProperties.getGroup(),
                                      new AbstractListener() {
                                          /**
                                           * 监听 nacos 事件, 如果 security-ignore-url.yml 被修改, 则发送一个刷新事件用于更新应用配置
                                           *
                                           * @param configInfo config info
                                           * @since 1.0.0
                                           */
                                          @Override
                                          public void receiveConfigInfo(String configInfo) {
                                              // 只发送一个事件, 在刷新完成后, RefreshScopeRefreshedEventHandler 更新配置
                                              DynamicSecurityUrl.this.applicationEventPublisher
                                                  .publishEvent(new RefreshEvent(this,
                                                                                 SecurityRefreshScopeRefreshedEventHandler.REFRESH_IGNORE_URL_CONFIG,
                                                                                 "刷新 security-ignore-url.yml (start)"));
                                          }
                                      });
        } catch (NacosException e) {
            throw SecurityCodes.IGNORE_URL_ERROR.newException(
                StringUtils.format("dataId = {}, group = {}, namespace = {}, serverAddr = {}",
                                   SecurityProperties.DEFAULT_DATA_ID,
                                   SecurityProperties.DEFAULT_GROUP,
                                   this.securityProperties.getGroup(),
                                   this.securityProperties.getDataId()));
        }
    }

    /**
     * 从 Nacos dataId = DEFAULT_DATA_ID 的配置文件中解析出 {@code List<String>} 类型的数据, 初始化 ignore-url
     *
     * @since 1.0.0
     */
    public void updateIgnoreUrl() {
        refreshIgnoreUrl(this.securityProperties.getIgnoreUrl());
    }

    /**
     * 使用本地的 security-ignore-url.yml
     *
     * @param localIgnoreUrl local ignore url
     * @since 1.7.0
     */
    private void loadIgnoreUrlFromLocalConfigFile(Map<String, Set<String>> localIgnoreUrl) {
        refreshIgnoreUrl(localIgnoreUrl);
    }

    /**
     * 读取本地的 security-ignore-url.yml
     *
     * @return the object
     * @since 1.7.0
     */
    private Map<String, Set<String>> processor() {
        try {
            final PropertySource<?> propertySource = ConfigKit.getPropertySource(SecurityProperties.DEFAULT_DATA_ID);
            environment.getPropertySources().addAfter(ConfigKit.SYSTEM_ENVIRONMENT_NAME, propertySource);

            final SecurityProperties securityProperties =
                Binder.get(environment).bind("spark.security", Bindable.of(SecurityProperties.class)).get();

            return securityProperties.getIgnoreUrl();
        } catch (PropertiesException e) {
            log.error("加载本地配置失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 开始刷新内存中的 ignore url
     *
     * @param ignoreUrlMap ignore url map
     * @since 1.7.0
     */
    private void refreshIgnoreUrl(Map<String, Set<String>> ignoreUrlMap) {
        this.allIgnoreUrlMatcherMap.clear();
        this.allIgnoreUrlMap.clear();
        this.allIgnoreUrlList.clear();

        Set<String> allServiceIgnoreUrl = Sets.newHashSet(SecurityUtils.DEFAULT_SKIP_URL);
        Map<String, Set<String>> otherServiceIgnoreUrlMap = Maps.newHashMapWithExpectedSize(8);
        log.info("加载 ignore-url: {}", ignoreUrlMap);
        if (CollectionUtils.isNotEmpty(ignoreUrlMap)) {
            // 将默认忽略的 url 与 all-service 合并
            allServiceIgnoreUrl.addAll(CollectionUtils.isNotEmpty(ignoreUrlMap.get(GLOBAL_IGNORE_URL_KEY))
                                       ? ignoreUrlMap.get(GLOBAL_IGNORE_URL_KEY)
                                       : Collections.emptySet());
            // 处理其他的 service 忽略 url
            otherServiceIgnoreUrlMap = ignoreUrlMap.entrySet().stream()
                .filter(m -> !GLOBAL_IGNORE_URL_KEY.equals(m.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        this.allIgnoreUrlMap.put(GLOBAL_IGNORE_URL_KEY, allServiceIgnoreUrl);

        this.allIgnoreUrlMap.putAll(otherServiceIgnoreUrlMap);
        // 转换成 <string, matcher>
        this.allIgnoreUrlMap.forEach((k, v) -> {
            this.allIgnoreUrlMatcherMap.put(k, SkipRequestMatchers.antMatchers(v.toArray(new String[0])));
            this.allIgnoreUrlList.addAll(v);
        });

        log.trace("更新 ignore url: \n{}",
                  GsonUtils.toJson(this.allIgnoreUrlMap, true));
    }

}
