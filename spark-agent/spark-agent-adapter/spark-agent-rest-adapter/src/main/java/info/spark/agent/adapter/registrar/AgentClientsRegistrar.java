package info.spark.agent.adapter.registrar;

import info.spark.agent.adapter.annotation.Client;
import info.spark.agent.adapter.annotation.EnableAgentClient;
import info.spark.agent.adapter.annotation.ServiceName;
import info.spark.agent.adapter.client.AgentClient;
import info.spark.agent.adapter.util.AgentUtils;
import info.spark.starter.basic.asserts.Assertions;
import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.basic.support.StrFormatter;
import info.spark.starter.basic.util.BasicUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AbstractClassTestingTypeFilter;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.SystemPropertyUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: ?????? {@link Client}, ?????????????????????????????????????????? IoC </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.09.22 08:58
 * @see AgentClientFactoryBean
 * @since 1.6.0
 */
@Slf4j
public class AgentClientsRegistrar implements PriorityOrdered, ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {
    /** Environment */
    private Environment environment;
    /** Resource loader */
    private ResourceLoader resourceLoader;

    /**
     * Agent clients registrar formatter:on
     *
     * @since 1.0.0
     */
    @Contract(pure = true)
    public AgentClientsRegistrar() {
        log.info("init {}", AgentClientsRegistrar.class);
    }

    /**
     * Sets resource loader *
     *
     * @param resourceLoader resource loader
     * @since 1.0.0
     */
    @Override
    public void setResourceLoader(@NotNull ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Sets environment *
     *
     * @param environment environment
     * @since 1.0.0
     */
    @Override
    public void setEnvironment(@NotNull Environment environment) {
        this.environment = environment;
    }

    /**
     * Register bean definitions *
     *
     * @param metadata metadata
     * @param registry registry
     * @since 1.0.0
     */
    @Override
    public void registerBeanDefinitions(@NotNull AnnotationMetadata metadata,
                                        @NotNull BeanDefinitionRegistry registry) {
        this.registerAgentClients(metadata, registry);
    }

    /**
     * Register Agent clients *
     *
     * @param metadata metadata
     * @param registry registry
     * @since 1.0.0
     */
    @SneakyThrows
    private void registerAgentClients(@NotNull AnnotationMetadata metadata,
                                      BeanDefinitionRegistry registry) {
        log.debug("?????? AgentClient ????????????....");
        ClassPathScanningCandidateComponentProvider scanner = this.getScanner();
        scanner.setResourceLoader(this.resourceLoader);

        Set<String> basePackages = getBasePackages(metadata, scanner);

        // ??????????????? @Client ????????? class ????????? IoC
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
            boolean useClientAnnotation = true;
            String annotationName = Client.class.getCanonicalName();
            if (CollectionUtils.isEmpty(candidateComponents)) {
                log.warn("???????????? @Client ????????? Agent SDK, ?????????????????? @ServiceName");
                // ?????? @ServiceName ??????
                scanner.addIncludeFilter(new AnnotationTypeFilter(ServiceName.class));
                candidateComponents = scanner.findCandidateComponents(basePackage);
                if (CollectionUtils.isEmpty(candidateComponents)) {
                    log.error("????????? @ServiceName ????????? Agent SDK, ?????? Agent SDK ????????????");
                    return;
                }
                useClientAnnotation = false;
                annotationName = ServiceName.class.getCanonicalName();
            }
            register(registry, candidateComponents, useClientAnnotation, annotationName);
        }
    }

    /**
     * Register
     *
     * @param registry            registry
     * @param candidateComponents candidate components
     * @param useClientAnnotation use client annotation
     * @param annotationName      annotation name
     * @throws ClassNotFoundException class not found exception
     * @since 2.1.0
     */
    private void register(BeanDefinitionRegistry registry,
                          @NotNull Set<BeanDefinition> candidateComponents,
                          boolean useClientAnnotation,
                          String annotationName) throws ClassNotFoundException {
        for (BeanDefinition candidateComponent : candidateComponents) {
            if (candidateComponent instanceof AnnotatedBeanDefinition) {
                // verify annotated class is an interface
                AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();

                Assertions.isTrue(annotationMetadata.isInterface(), "@Client/@ServiceName ?????????????????????");
                Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(annotationName);
                Assertions.notNull(attributes, "?????? [" + annotationMetadata.getClassName() + "] ????????????");

                if (useClientAnnotation) {
                    registerByClientAnnotation(annotationMetadata, Objects.requireNonNull(attributes));
                } else {
                    registerByServiceNameAnnotation(annotationMetadata, Objects.requireNonNull(attributes));
                }
                Objects.requireNonNull(attributes).put("currentAgentClientClassName", annotationMetadata.getClassName());
                log.debug("Agent client name = [{}]", this.getClientName(attributes));
                this.registerAgentClient(registry, annotationMetadata, attributes);
            }
        }

    }

    /**
     * Register by service name annotation
     *
     * @param annotationMetadata annotation metadata
     * @param attributes         attributes
     * @throws ClassNotFoundException class not found exception
     * @since 2.1.0
     */
    private void registerByServiceNameAnnotation(@NotNull AnnotationMetadata annotationMetadata,
                                                 @NotNull Map<String, Object> attributes) throws ClassNotFoundException {

        final Class<?> agentClientClass = ClassUtils.forName(annotationMetadata.getClassName(),
                                                             Thread.currentThread().getContextClassLoader());

        ServiceName serviceNameAnnotation = agentClientClass.getAnnotation(ServiceName.class);
        attributes.put(ServiceName.SERVICE_NAME, serviceNameAnnotation.value());


    }

    /**
     * ?????? @Client ?????? agentClient
     *
     * @param annotationMetadata annotation metadata
     * @param attributes         attributes
     * @throws ClassNotFoundException class not found exception
     * @since 2.1.0
     */
    private void registerByClientAnnotation(@NotNull AnnotationMetadata annotationMetadata,
                                            @NotNull Map<String, Object> attributes) throws ClassNotFoundException {
        this.processServiceName(annotationMetadata, attributes);
    }

    /**
     * Gets base packages *
     *
     * @param metadata metadata
     * @param scanner  scanner
     * @return the base packages
     * @since 2.1.0
     */
    @NotNull
    private Set<String> getBasePackages(@NotNull AnnotationMetadata metadata,
                                        ClassPathScanningCandidateComponentProvider scanner) {
        Set<String> basePackages;

        // ?????? @EnableAgentClient, ?????????????????????, ????????? @Client
        Map<String, Object> attrs = metadata.getAnnotationAttributes(EnableAgentClient.class.getName());

        Class<?>[] clients = attrs == null ? null : (Class<?>[]) attrs.get(EnableAgentClient.CLIENTS);
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(Client.class);

        // ?????? @EnableAgentClient ????????? clients ???????????????????????? EnableAgentClient ??????????????????
        if (clients == null || clients.length == 0) {
            scanner.addIncludeFilter(annotationTypeFilter);
            basePackages = this.getBasePackages(metadata);
        } else {
            // ??????????????? client ????????????
            Set<String> clientClasses = new HashSet<>();
            basePackages = new HashSet<>();
            for (Class<?> clazz : clients) {
                basePackages.add(ClassUtils.getPackageName(clazz));
                clientClasses.add(clazz.getCanonicalName());
            }

            AbstractClassTestingTypeFilter filter = new AbstractClassTestingTypeFilter() {
                /**
                 * ???????????????
                 *
                 * @param metadata metadata
                 * @return the boolean
                 * @since 1.6.0
                 */
                @Override
                @SuppressWarnings("java:S5361")
                protected boolean match(@NotNull ClassMetadata metadata) {
                    String cleaned = metadata.getClassName().replaceAll("\\$", ".");
                    return clientClasses.contains(cleaned);
                }
            };
            scanner.addIncludeFilter(new AllTypeFilter(Arrays.asList(filter, annotationTypeFilter)));
        }
        return basePackages;
    }

    /**
     * ????????????, ??????????????? Client.serviceName, ??????????????? ServiceName.serviceName
     *
     * @param annotationMetadata annotation metadata
     * @param attributes         attributes
     * @throws ClassNotFoundException class not found exception
     * @since 1.7.1
     */
    @SuppressWarnings("java:S1872")
    private void processServiceName(@NotNull AnnotationMetadata annotationMetadata,
                                    Map<String, Object> attributes) throws ClassNotFoundException {
        // ????????? @Serivce ???????????????????????????
        Class<?>[] interfaces = ClassUtils.forName(annotationMetadata.getClassName(),
                                                   Thread.currentThread().getContextClassLoader()).getInterfaces();
        List<Class<?>> collect =
            Arrays.stream(interfaces).filter(AgentClient.class::isAssignableFrom).collect(Collectors.toList());
        Assertions.isTrue(collect.size() == 1, "[" + annotationMetadata.getClassName() + "] ?????????????????????");
        Class<?> agentClientClass = collect.get(0);

        if (agentClientClass.getName().equals(AgentClient.class.getName())) {
            // ????????????????????? AgentClient, ????????? Client.value
            attributes.put(ServiceName.SERVICE_NAME, attributes.get(Client.VALUE));
        } else {
            // ??????????????? SDK ????????????
            ServiceName serviceNameAnnotation = agentClientClass.getAnnotation(ServiceName.class);
            Object clientValue = attributes.get(Client.VALUE);

            Assertions.isFalse((serviceNameAnnotation == null || !StringUtils.hasText(serviceNameAnnotation.value()))
                               && clientValue == null,
                               "????????? @Client.value ??? @ServiceName.value ?????? serviceName");

            if (serviceNameAnnotation == null || !StringUtils.hasText(serviceNameAnnotation.value())) {
                log.warn("[{}.serviceName] ?????????, ????????? @ServiceName ?????? @Client.value ?????? serviceName", agentClientClass.getName());
                attributes.put(ServiceName.SERVICE_NAME, clientValue);
            } else if (clientValue != null && StringUtils.hasText(String.valueOf(clientValue))) {
                // ???????????? client.value ?????? serivceName, ???????????????
                log.warn("serviceName ?????????: [{}] -> [{}]", serviceNameAnnotation.value(), clientValue);
                attributes.put(ServiceName.SERVICE_NAME, clientValue);
            } else {
                attributes.put(ServiceName.SERVICE_NAME, serviceNameAnnotation.value());
            }
        }
    }

    /**
     * Spring ???????????????,???????????????????????????,?????? classpath ?????????????????? class ??????
     *
     * @return the scanner
     * @since 1.0.0
     */
    @NotNull
    @Contract(" -> new")
    private ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            @Override
            protected boolean isCandidateComponent(@NotNull AnnotatedBeanDefinition beanDefinition) {
                return beanDefinition.getMetadata().isIndependent() && !beanDefinition.getMetadata().isAnnotation();
            }
        };
    }

    /**
     * ?????? @EnableAgentClient ????????????????????????, ????????????, ??????????????????????????????
     *
     * @param importingClassMetadata importing class metadata
     * @return the base packages
     * @since 1.0.0
     */
    private @NotNull Set<String> getBasePackages(@NotNull AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata
            .getAnnotationAttributes(EnableAgentClient.class.getCanonicalName());

        Set<String> basePackages = new HashSet<>();
        for (String pkg : (String[]) Objects.requireNonNull(attributes).get(EnableAgentClient.VALUE)) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (String pkg : (String[]) attributes.get(EnableAgentClient.BASE_PACKAGES)) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (Class<?> clazz : (Class<?>[]) attributes.get(EnableAgentClient.BASE_PACKAGECLASSES)) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }

        if (basePackages.isEmpty()) {
            basePackages.add(
                ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        return basePackages;
    }

    /**
     * AgentClient ??????????????????, ???????????? AgentClientFactoryBean ????????? bean
     *
     * @param registry           registry
     * @param annotationMetadata annotation metadata
     * @param attributes         attributes
     * @see AgentClientFactoryBean#getObject() AgentClientFactoryBean#getObject()
     * @see BeanDefinitionParserDelegate#parseBeanDefinitionAttributes BeanDefinitionParserDelegate#parseBeanDefinitionAttributes
     * @see AgentClientFactoryBean#agentTemplate AgentClientFactoryBean#agentTemplate
     * @see AgentClientFactoryBean#agentRestProperties AgentClientFactoryBean#agentRestProperties
     * @see AgentClientFactoryBean#validater AgentClientFactoryBean#validater
     * @since 1.0.0
     */
    @SuppressWarnings("JavadocReference")
    @SneakyThrows
    private void registerAgentClient(BeanDefinitionRegistry registry,
                                     @NotNull AnnotationMetadata annotationMetadata, Map<String, Object> attributes) {
        String className = annotationMetadata.getClassName();
        // ?????? factory, ?????? Agent client bean ?????????????????????, ?????????
        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(AgentClientFactoryBean.class);
        this.validate(attributes);

        try {
            // ??? AgentClientFactoryBean ??????????????????
            definition.addPropertyValue(Client.ENDPOINT, this.getEndpoint(attributes));
            // todo-dong4j : (2021.01.14 16:20) [????????????????????? ServiceName]
            definition.addPropertyValue(ServiceName.SERVICE_NAME, attributes.get(ServiceName.SERVICE_NAME));
            definition.addPropertyValue(Client.TYPE, className);
            definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_NO);
        } catch (Exception e) {
            // ??????????????? Agent client ???????????????, ???????????? client ??????????????????????????????????????????. ?????????????????? client ?????????????????? IoC,
            // ???????????? @Resource ?????? client ?????????????????? bean ?????????
            log.error(StrFormatter.format("[{}] ????????????:", className), e);
            return;
        }

        String alias = className + "-AgentClient";
        String qualifier = this.getQualifier(attributes);
        if (StringUtils.hasText(qualifier)) {
            alias = qualifier;
        }

        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();

        beanDefinition.setPrimary(true);

        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className, new String[] {alias});
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
        log.info("?????? Agent Client: [{}] [{}]", className, holder);
    }

    /**
     * ??????????????????
     *
     * @param attributes attributes
     * @since 1.0.0
     */
    @SuppressWarnings("all")
    private void validate(Map<String, Object> attributes) {
        // todo-dong4j : (2021.05.19 16:28) [?????????]
        AnnotationAttributes annotation = AnnotationAttributes.fromMap(attributes);
    }

    /**
     * Gets client name *
     *
     * @param attributes attributes
     * @return the client name
     * @since 1.0.0
     */
    @Contract("null -> null")
    private String getClientName(Map<String, Object> attributes) {
        if (attributes == null) {
            return null;
        }
        String value = (String) attributes.get(Client.VALUE);
        if (!StringUtils.hasText(value)) {
            value = (String) attributes.get(ServiceName.SERVICE_NAME);
        }

        if (StringUtils.hasText(value)) {
            return value;
        } else {
            // ?????????????????? value, name, url, ????????? type ??????  client name
            return String.valueOf(attributes.get("currentAgentClientClassName"));
        }
    }

    /**
     * Gets qualifier *
     *
     * @param client client
     * @return the qualifier
     * @since 1.0.0
     */
    @Contract("null -> null")
    private String getQualifier(Map<String, Object> client) {
        if (client == null) {
            return null;
        }
        String qualifier = (String) client.get("qualifier");
        if (StringUtils.hasText(qualifier)) {
            return qualifier;
        }
        return null;
    }

    /**
     * Gets url *
     *
     * @param attributes attributes
     * @return the url
     * @since 1.0.0
     */
    private String getEndpoint(@NotNull Map<String, Object> attributes) {
        String endpoint = this.resolve((String) attributes.get(Client.ENDPOINT));
        return getEndpoint(endpoint, this.environment);
    }

    /**
     * ???????????????????????????
     *
     * @param value value
     * @return the string
     * @since 1.0.0
     */
    private String resolve(String value) {
        if (StringUtils.hasText(value)) {
            return this.environment.resolvePlaceholders(value);
        }
        return value;
    }

    /**
     * ?????? endpoint, ?????? [??????:port] ?????? [??????] ?????? [ip:port] ??? [ip] ????????????, ?????????????????????.
     *
     * @param endpoint    endpoint
     * @param environment ????????????
     * @return the endpoint
     * @since 1.0.0
     */
    @SuppressWarnings("java:S1874")
    private static String getEndpoint(String endpoint, Environment environment) {
        // ???????????????????????? true
        String endpointProperties = environment.getProperty(ConfigKey.AgentConfigKey.GATEWAY_REST_ENABLE_ENDPOINT, "true");

        boolean enableEndpoint = Boolean.parseBoolean(endpointProperties);

        if (BasicUtils.isLocalLaunch()
            && enableEndpoint
            && StringUtils.hasText(endpoint)) {
            boolean resolved = !(endpoint.startsWith(SystemPropertyUtils.PLACEHOLDER_PREFIX)
                                 && endpoint.endsWith(SystemPropertyUtils.PLACEHOLDER_SUFFIX));
            Assertions.isTrue(resolved, "???????????????????????? [" + endpoint + "], ?????????????????????");
            endpoint = AgentUtils.checkEndpointPattern(endpoint);
        }
        return endpoint;
    }

    /**
     * ????????? bean ?????????????????????, ??????????????????????????????
     *
     * @return the order
     * @see PriorityOrdered
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    /**
     * Helper class to create a {@link TypeFilter} that matches if all the delegates
     * match.
     *
     * @author dong4j
     * @version 1.2.3
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.01.19 03:28
     * @since 1.0.0
     */
    private static class AllTypeFilter implements TypeFilter {

        /** Delegates */
        private final List<TypeFilter> delegates;

        /**
         * Creates a new {@link AllTypeFilter} to match if all the given delegates match.
         *
         * @param delegates must not be {@literal null}.
         * @since 1.0.0
         */
        @Contract(pure = true)
        AllTypeFilter(List<TypeFilter> delegates) {
            Assert.notNull(delegates, "This argument is required, it must not be null");
            this.delegates = delegates;
        }

        /**
         * Match boolean
         *
         * @param metadataReader        metadata reader
         * @param metadataReaderFactory metadata reader factory
         * @return the boolean
         * @throws IOException io exception
         * @since 1.0.0
         */
        @Override
        public boolean match(@NotNull MetadataReader metadataReader,
                             @NotNull MetadataReaderFactory metadataReaderFactory) throws IOException {

            for (TypeFilter filter : this.delegates) {
                if (!filter.match(metadataReader, metadataReaderFactory)) {
                    return false;
                }
            }

            return true;
        }
    }
}
