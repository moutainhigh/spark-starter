package info.spark.feign.adapter.registrar;

import info.spark.feign.adapter.annotation.EnableFeignClients;
import info.spark.feign.adapter.annotation.FeignClient;
import info.spark.feign.adapter.util.FeignUtils;
import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.basic.util.StringPool;

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
import org.springframework.util.StringUtils;
import org.springframework.util.SystemPropertyUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:
 * Feign Client ?????????????????????, ?????? @EnableFeignClients ?????? Feign Client ????????????, ???????????????????????? @FeignClient ????????????, ????????????????????????,
 * ??????????????? {@link FeignClientsRegistrar#registerFeignClient} ????????????????????????????????? {@link FeignClientFactoryBean} ??????.
 * 1. ?????? name ?????? value ???, ?????????????????????, ??????????????? http://gateway/?????????/[agent] ??? url;
 * 2. agent ??????????????? @FeignClient ??? agent ??????????????????, ????????? false, ??????????????? true, ???????????????????????????[?????? spark-starter-agent];
 * 3. ?????? url ?????????????????????: http://gateway, agent ??????????????????, ???????????? url ?????? /agent;
 * 4. ????????????????????? name(value) ??? url, ??????????????? url, {@link FeignClientFactoryBean#getTarget()};
 * </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.26 16:40
 * @see FeignClientFactoryBean
 * @since 1.0.0
 */
@Slf4j
public class FeignClientsRegistrar implements PriorityOrdered, ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {
    /** Environment */
    private Environment environment;
    /** Resource loader */
    private ResourceLoader resourceLoader;

    /**
     * Feign clients registrar
     *
     * @since 1.0.0
     */
    @Contract(pure = true)
    public FeignClientsRegistrar() {
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
        this.registerFeignClients(metadata, registry);
    }

    /**
     * Register feign clients *
     *
     * @param metadata metadata
     * @param registry registry
     * @since 1.0.0
     */
    private void registerFeignClients(@NotNull AnnotationMetadata metadata,
                                      BeanDefinitionRegistry registry) {
        log.debug("??????????????????....");
        ClassPathScanningCandidateComponentProvider scanner = this.getScanner();
        scanner.setResourceLoader(this.resourceLoader);

        Set<String> basePackages;

        // ?????? @EnableFeignClients, ?????????????????????, ????????? @FeignClient
        Map<String, Object> attrs = metadata.getAnnotationAttributes(EnableFeignClients.class.getName());
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(FeignClient.class);
        Class<?>[] clients = attrs == null ? null : (Class<?>[]) attrs.get("clients");
        if (clients == null || clients.length == 0) {
            scanner.addIncludeFilter(annotationTypeFilter);
            basePackages = this.getBasePackages(metadata);
        } else {
            Set<String> clientClasses = new HashSet<>();
            basePackages = new HashSet<>();
            for (Class<?> clazz : clients) {
                basePackages.add(ClassUtils.getPackageName(clazz));
                clientClasses.add(clazz.getCanonicalName());
            }
            AbstractClassTestingTypeFilter filter = new AbstractClassTestingTypeFilter() {
                @Override
                protected boolean match(@NotNull ClassMetadata metadata) {
                    String cleaned = metadata.getClassName().replaceAll("\\$", ".");
                    return clientClasses.contains(cleaned);
                }
            };
            scanner.addIncludeFilter(
                new AllTypeFilter(Arrays.asList(filter, annotationTypeFilter)));
        }

        // ??????????????? @FeignClient ????????? class ????????? IoC
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition candidateComponent : candidateComponents) {
                if (candidateComponent instanceof AnnotatedBeanDefinition) {
                    // verify annotated class is an interface
                    AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                    AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                    Assert.isTrue(annotationMetadata.isInterface(), "@FeignClient can only be specified on an interface");

                    Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(FeignClient.class.getCanonicalName());
                    Objects.requireNonNull(attributes).put("currentFeignClientClassName", annotationMetadata.getClassName());
                    log.debug("Feign client name = [{}]", this.getClientName(attributes));
                    this.registerFeignClient(registry, annotationMetadata, attributes);
                }
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
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (!beanDefinition.getMetadata().isAnnotation()) {
                        isCandidate = true;
                    }
                }
                return isCandidate;
            }
        };
    }

    /**
     * ?????? @EnableFeignClients ????????????????????????, ????????????, ??????????????????????????????
     *
     * @param importingClassMetadata importing class metadata
     * @return the base packages
     * @since 1.0.0
     */
    private Set<String> getBasePackages(@NotNull AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata
            .getAnnotationAttributes(EnableFeignClients.class.getCanonicalName());

        Set<String> basePackages = new HashSet<>();
        for (String pkg : (String[]) Objects.requireNonNull(attributes).get(EnableFeignClients.VALUE)) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (String pkg : (String[]) attributes.get(EnableFeignClients.BASE_PACKAGES)) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (Class<?> clazz : (Class[]) attributes.get(EnableFeignClients.BASE_PACKAGECLASSES)) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }

        if (basePackages.isEmpty()) {
            basePackages.add(
                ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        return basePackages;
    }

    /**
     * FeignClient ??????????????????, ???????????? FeignClientFactoryBean ????????? bean
     *
     * @param registry           registry
     * @param annotationMetadata annotation metadata
     * @param attributes         attributes
     * @see FeignClientFactoryBean#getObject() FeignClientFactoryBean#getObject()FeignClientFactoryBean#getObject()
     * @see BeanDefinitionParserDelegate#parseBeanDefinitionAttributes BeanDefinitionParserDelegate#parseBeanDefinitionAttributes
     * @since 1.0.0
     */
    private void registerFeignClient(BeanDefinitionRegistry registry,
                                     @NotNull AnnotationMetadata annotationMetadata, Map<String, Object> attributes) {
        String className = annotationMetadata.getClassName();
        // ?????? factory, ?????? feign client bean ?????????????????????, ?????????
        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(FeignClientFactoryBean.class);
        this.validate(attributes);

        String contextId;
        try {
            definition.addPropertyValue(FeignClient.URL, this.getUrl(attributes));
            definition.addPropertyValue(FeignClient.AGENT, attributes.get(FeignClient.AGENT));
            definition.addPropertyValue(FeignClient.PATH, this.getPath(attributes));
            String name = this.getName(attributes);
            definition.addPropertyValue(FeignClient.NAME, name);
            contextId = this.getContextId(attributes);
            definition.addPropertyValue(FeignClient.CONTEXT_ID, contextId);
            definition.addPropertyValue(FeignClient.TYPE, className);
            definition.addPropertyValue(FeignClient.DECODE_404, attributes.get(FeignClient.DECODE_404));
            definition.addPropertyValue(FeignClient.FALLBACK, attributes.get(FeignClient.FALLBACK));
            definition.addPropertyValue(FeignClient.FALLBACK_FACTORY, attributes.get(FeignClient.FALLBACK_FACTORY));
            definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_NO);
        } catch (Exception e) {
            // ??????????????? feign client ???????????????, ???????????? client ??????????????????????????????????????????. ?????????????????? client ?????????????????? IoC, ???????????? @Resource ?????? client ?????????????????? bean ?????????
            log.error("[{}] ????????????: [{}]", className, e.getMessage());
            return;
        }

        String alias = className + "-FeignClient";
        String qualifier = this.getQualifier(attributes);
        if (StringUtils.hasText(qualifier)) {
            alias = qualifier;
        }

        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();

        // has a default, won't be null
        boolean primary = (Boolean) attributes.get(FeignClient.PRIMARY);

        beanDefinition.setPrimary(primary);

        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className, new String[] {alias});
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
        log.info("?????? Feign Client: [{}] [{}]", className, holder);

    }

    /**
     * ??????????????????
     *
     * @param attributes attributes
     * @since 1.0.0
     */
    private void validate(Map<String, Object> attributes) {
        AnnotationAttributes annotation = AnnotationAttributes.fromMap(attributes);
        FeignUtils.validateFallback(annotation.getClass("fallback"));
        FeignUtils.validateFallbackFactory(annotation.getClass("fallbackFactory"));
    }

    /**
     * Gets context id *
     *
     * @param attributes attributes
     * @return the context id
     * @since 1.0.0
     */
    private String getContextId(@NotNull Map<String, Object> attributes) {
        String contextId = (String) attributes.get(FeignClient.CONTEXT_ID);
        if (!StringUtils.hasText(contextId)) {
            return this.getName(attributes);
        }

        contextId = this.resolve(contextId);
        return FeignUtils.getName(contextId);
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
        String value = (String) attributes.get(FeignClient.CONTEXT_ID);
        if (!StringUtils.hasText(value)) {
            value = (String) attributes.get(FeignClient.VALUE);
        }
        if (!StringUtils.hasText(value)) {
            value = (String) attributes.get(FeignClient.NAME);
        }
        if (!StringUtils.hasText(value)) {
            value = this.getServiceNameFromUrl((String) attributes.get(FeignClient.URL));
        }
        if (StringUtils.hasText(value)) {
            return value;
        } else {
            // ?????????????????? value, name, url, ????????? type ??????  client name
            return String.valueOf(attributes.get("currentFeignClientClassName"));
        }
    }

    /**
     * Gets name *
     *
     * @param attributes attributes
     * @return the name
     * @since 1.0.0
     */
    private String getName(@NotNull Map<String, Object> attributes) {
        String name = (String) attributes.get(FeignClient.NAME);
        if (!StringUtils.hasText(name)) {
            name = (String) attributes.get(FeignClient.VALUE);
        }
        if (!StringUtils.hasText(name)) {
            name = this.getServiceNameFromUrl((String) attributes.get(FeignClient.URL));
        }
        name = this.resolve(name);
        return FeignUtils.getName(name);
    }

    /**
     * ??????????????? url ??????????????? service name
     * example:
     * ${spark.feign.url.demo-service=http://gateway/third-center} --> demo-service
     *
     * @param url url
     * @return the string
     * @since 1.0.0
     */
    @NotNull
    private String getServiceNameFromUrl(@NotNull String url) {
        return url.replace(SystemPropertyUtils.PLACEHOLDER_PREFIX, StringPool.EMPTY)
            .replace(SystemPropertyUtils.PLACEHOLDER_SUFFIX, StringPool.EMPTY)
            .replace(ConfigKey.FeignConfigKey.FEIGN_URL_PROFIX, StringPool.EMPTY);
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
     * Gets url *
     *
     * @param attributes attributes
     * @return the url
     * @since 1.0.0
     */
    private String getUrl(@NotNull Map<String, Object> attributes) {
        String url = this.resolve((String) attributes.get(FeignClient.URL));
        return FeignUtils.getUrl(url);
    }

    /**
     * Gets path *
     *
     * @param attributes attributes
     * @return the path
     * @since 1.0.0
     */
    private String getPath(@NotNull Map<String, Object> attributes) {
        String path = this.resolve((String) attributes.get(FeignClient.PATH));
        return FeignUtils.getPath(path);
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
     * ????????? bean ?????????????????????, ??????????????????????????????
     *
     * @return the order
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE;
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
