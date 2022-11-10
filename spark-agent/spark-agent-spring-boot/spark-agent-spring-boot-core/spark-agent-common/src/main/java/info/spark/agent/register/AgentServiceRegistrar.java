package info.spark.agent.register;

import info.spark.agent.annotation.ApiService;
import info.spark.agent.annotation.EnableAgentService;
import info.spark.starter.util.CollectionUtils;
import info.spark.starter.util.StringUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.PriorityOrdered;
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

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:
 *
 * @author dong4j
 * @version 1.0.4
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.26 16:40
 * @since 1.0.0
 */
@Slf4j
public class AgentServiceRegistrar implements PriorityOrdered, ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {
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
    public AgentServiceRegistrar() {
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
     * Gets order *
     *
     * @return the order
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }

    /**
     * 将被 @ApiService 标识的类注册了为一个 bean 的入口
     *
     * @param metadata metadata
     * @param registry registry
     * @since 1.0.0
     */
    @Override
    public void registerBeanDefinitions(@NotNull AnnotationMetadata metadata,
                                        @NotNull BeanDefinitionRegistry registry) {
        this.registerAgentServices(metadata, registry);
    }

    /**
     * Register agent services *
     *
     * @param metadata metadata
     * @param registry registry
     * @since 1.0.0
     */
    private void registerAgentServices(@NotNull AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        log.debug("扫描自定义注解, 注入 IoC....");
        // 解析 EnableAgentService 配置的扫描路径
        Set<String> basePackages = this.getScannerPackage(metadata);
        // 生成注解扫描器
        ClassPathScanningCandidateComponentProvider scanner = this.getScanner(metadata);

        // 将扫描的被 @ApiService 标识的 class 注入到 IoC
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
            if (CollectionUtils.isEmpty(candidateComponents)) {
                log.warn("[{}] 内没有找到被 @ApiService 标识的类", basePackage);
                continue;
            }

            for (BeanDefinition candidateComponent : candidateComponents) {
                if (candidateComponent instanceof AnnotatedBeanDefinition) {
                    // verify annotated class is an interface
                    AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                    AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                    Assert.isTrue(!annotationMetadata.isInterface(), "@AipService can't be specified on an interface");

                    Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(ApiService.class.getCanonicalName());
                    Objects.requireNonNull(attributes).put("currentApiServiceClassName", annotationMetadata.getClassName());
                    this.registerApiService(registry, annotationMetadata, attributes);
                }
            }
        }
    }

    /**
     * Gets scanner package *
     *
     * @param metadata metadata
     * @return the scanner package
     * @since 1.0.0
     */
    private Set<String> getScannerPackage(@NotNull AnnotationMetadata metadata) {
        Map<String, Object> attrs = metadata.getAnnotationAttributes(EnableAgentService.class.getName());
        Set<String> basePackages;
        Class<?>[] services = attrs == null ? null : (Class<?>[]) attrs.get(EnableAgentService.SERVICES);
        if (services == null || services.length == 0) {
            basePackages = this.getBasePackages(metadata);
        } else {
            basePackages = new HashSet<>();
            for (Class<?> clazz : services) {
                basePackages.add(ClassUtils.getPackageName(clazz));
            }
        }
        return basePackages;
    }

    /**
     * Get scanner class path scanning candidate component provider
     *
     * @param metadata metadata
     * @return the class path scanning candidate component provider
     * @since 1.0.0
     */
    private ClassPathScanningCandidateComponentProvider getScanner(@NotNull AnnotationMetadata metadata) {
        Map<String, Object> attrs = metadata.getAnnotationAttributes(EnableAgentService.class.getName());
        ClassPathScanningCandidateComponentProvider scanner = this.getScanner();
        scanner.setResourceLoader(this.resourceLoader);
        AnnotationTypeFilter apiServiceTypeFilter = new AnnotationTypeFilter(ApiService.class);
        Class<?>[] services = attrs == null ? null : (Class<?>[]) attrs.get(EnableAgentService.SERVICES);

        if (services == null || services.length == 0) {
            scanner.addIncludeFilter(apiServiceTypeFilter);
        } else {
            // 如果指定了 services 属性, 则扫描指定的 ApiService
            Set<String> clientClasses = new HashSet<>();
            for (Class<?> clazz : services) {
                clientClasses.add(clazz.getCanonicalName());
            }
            AbstractClassTestingTypeFilter filter = new AbstractClassTestingTypeFilter() {
                @Override
                protected boolean match(@NotNull ClassMetadata metadata) {
                    String cleaned = metadata.getClassName().replaceAll("\\$", ".");
                    return clientClasses.contains(cleaned);
                }
            };
            scanner.addIncludeFilter(new AllTypeFilter(Arrays.asList(filter, apiServiceTypeFilter)));
        }
        return scanner;
    }

    /**
     * 直接将被 @ApiService 标识的 class 注册为一个 bean, 底层由 Spring 通过代理创建实例.
     * 没有使用 {@link AgentServiceFactoryBean} 的方式生成时为了解决自定义 bean 中不能注入其他 bean 的问题,
     * 这里会使用 spring 的代理生成 apiService 实例
     *
     * @param registry           registry
     * @param annotationMetadata annotation metadata
     * @param attributes         attributes
     * @see BeanDefinitionParserDelegate#parseBeanDefinitionAttributes
     * @since 1.0.0
     */
    @SneakyThrows
    private void registerApiService(BeanDefinitionRegistry registry,
                                    @NotNull AnnotationMetadata annotationMetadata,
                                    Map<String, Object> attributes) {
        String className = annotationMetadata.getClassName();
        String beanName = this.getName(attributes);

        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        Class<?> cls = Class.forName(className);
        beanDefinition.setBeanClass(cls);
        beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        String alias = this.getApiName(attributes);
        boolean primary = (Boolean) attributes.get(ApiService.PRIMARY);
        beanDefinition.setPrimary(primary);

        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, beanName, new String[] {alias});
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
        log.trace("create bean: [{}] bean name: [{}]", className, beanName);
    }

    /**
     * Spring 提供的工具,可以按自定义的类型,查找 classpath 下符合要求的 class 文件
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
     * 解析 @EnableAgentService 指定的包扫描路径, 如果为空, 则扫描此注解所在的包
     *
     * @param importingClassMetadata importing class metadata
     * @return the base packages
     * @since 1.0.0
     */
    private Set<String> getBasePackages(@NotNull AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata
            .getAnnotationAttributes(EnableAgentService.class.getCanonicalName());

        Set<String> basePackages = new HashSet<>();
        for (String pkg : (String[]) Objects.requireNonNull(attributes).get(EnableAgentService.VALUE)) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (String pkg : (String[]) attributes.get(EnableAgentService.BASE_PACKAGES)) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (Class<?> clazz : (Class<?>[]) attributes.get(EnableAgentService.BASE_PACKAGECLASSES)) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }

        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        return basePackages;
    }

    /**
     * 生成 api name, 没有指定则使用全类名
     *
     * @param attributes attributes
     * @return the client name
     * @since 1.0.0
     */
    @SneakyThrows
    private String getApiName(@NotNull Map<String, Object> attributes) {
        String apiName = (String) attributes.get(ApiService.API_NAME);
        if (!StringUtils.hasText(apiName)) {
            Class<?> clz = Class.forName(String.valueOf(attributes.get("currentApiServiceClassName")));
            apiName = clz.getName();
        }

        return apiName;
    }

    /**
     * 注册的 bean name, 没有指定则使用当前类的 class name, 首字母小写
     *
     * @param attributes attributes
     * @return the name
     * @since 1.0.0
     */
    @SneakyThrows
    private String getName(@NotNull Map<String, Object> attributes) {
        String name = (String) attributes.get(ApiService.NAME);
        if (!StringUtils.hasText(name)) {
            name = (String) attributes.get(ApiService.VALUE);
        }
        if (!StringUtils.hasText(name)) {
            Class<?> clz = Class.forName(String.valueOf(attributes.get("currentApiServiceClassName")));
            name = StringUtils.firstCharToLower(clz.getSimpleName());
        }

        return name;
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
