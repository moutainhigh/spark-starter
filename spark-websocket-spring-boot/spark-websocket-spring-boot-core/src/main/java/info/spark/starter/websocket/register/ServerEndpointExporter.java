package info.spark.starter.websocket.register;

import info.spark.starter.basic.asserts.Assertions;
import info.spark.starter.websocket.annotation.ServerEndpoint;
import info.spark.starter.websocket.annotation.EnableWebSocket;

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
 * <p>Description: ??? ServerEndpoint ??????????????????????????? ioc</p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.09 20:08
 * @since 2022.1.1
 */
@Slf4j
@SuppressWarnings("all")
public class ServerEndpointExporter implements ResourceLoaderAware,
                                               ImportBeanDefinitionRegistrar,
                                               EnvironmentAware {

    /** Environment */
    private Environment environment;

    /** Resource loader */
    private ResourceLoader resourceLoader;

    /**
     * Register bean definitions
     *
     * @param metadata metadata
     * @param registry registry
     * @since 2022.1.1
     */
    @Override
    public void registerBeanDefinitions(@NotNull AnnotationMetadata metadata,
                                        @NotNull BeanDefinitionRegistry registry) {
        this.registerWebsocketServices(metadata, registry);
    }


    /**
     * Register websocket services
     *
     * @param metadata metadata
     * @param registry registry
     * @since 2022.1.1
     */
    private void registerWebsocketServices(@NotNull AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        log.debug("?????????????????????, ?????? IoC....");
        // ?????? EnableWebSocket ?????????????????????
        Set<String> basePackages = this.getScannerPackage(metadata);
        // ?????????????????????
        ClassPathScanningCandidateComponentProvider scanner = this.getScanner(metadata);

        // ??????????????? @EnableWebSocket ????????? class ????????? IoC
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
            if (CollectionUtils.isEmpty(candidateComponents)) {
                log.warn("[{}] ?????????????????? @EnableWebSocket ????????????", basePackage);
                continue;
            }

            for (BeanDefinition candidateComponent : candidateComponents) {
                if (candidateComponent instanceof AnnotatedBeanDefinition) {
                    // verify annotated class is an interface
                    AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                    AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                    Assertions.isTrue(!annotationMetadata.isInterface(), "@ServerEndpoint can't be specified on an interface");

                    this.registerWebsocketService(registry, annotationMetadata);
                }
            }
        }
    }

    /**
     * ?????? EnableWebSocket ????????????????????????
     *
     * @param metadata metadata
     * @return the scanner package
     * @since 2022.1.1
     */
    private @NotNull Set<String> getScannerPackage(@NotNull AnnotationMetadata metadata) {
        Map<String, Object> attrs = metadata.getAnnotationAttributes(EnableWebSocket.class.getName());
        Set<String> basePackages;
        Class<?>[] services = attrs == null ? null : (Class<?>[]) attrs.get(EnableWebSocket.SERVICES);
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
    private @NotNull ClassPathScanningCandidateComponentProvider getScanner(@NotNull AnnotationMetadata metadata) {
        Map<String, Object> attrs = metadata.getAnnotationAttributes(EnableWebSocket.class.getName());
        ClassPathScanningCandidateComponentProvider scanner = this.getScanner();
        scanner.setResourceLoader(this.resourceLoader);
        AnnotationTypeFilter serverEndpointFilter = new AnnotationTypeFilter(ServerEndpoint.class);
        Class<?>[] services = attrs == null ? null : (Class<?>[]) attrs.get(EnableWebSocket.SERVICES);

        // ?????????????????? annotation.info.spark.starter.websocket.EnableWebSocket.services ????????????????????? @ServerEndpoint
        if (services == null || services.length == 0) {
            scanner.addIncludeFilter(serverEndpointFilter);
        } else {
            // ??????????????? services ??????, ?????????????????? ServerEndpoint
            Set<String> clientClasses = new HashSet<>();
            for (Class<?> clazz : services) {
                clientClasses.add(clazz.getCanonicalName());
            }
            AbstractClassTestingTypeFilter filter = new AbstractClassTestingTypeFilter() {
                /**
                 * Match
                 *
                 * @param metadata metadata
                 * @return the boolean
                 * @since 2022.1.1
                 */
                @Override
                protected boolean match(@NotNull ClassMetadata metadata) {
                    String cleaned = metadata.getClassName().replaceAll("\\$", ".");
                    return clientClasses.contains(cleaned);
                }
            };
            scanner.addIncludeFilter(new AllTypeFilter(Arrays.asList(filter, serverEndpointFilter)));
        }
        return scanner;
    }

    /**
     * ???????????? @ApiService ????????? class ??????????????? bean, ????????? Spring ????????????????????????.
     *
     * @param registry           registry
     * @param annotationMetadata annotation metadata
     * @see BeanDefinitionParserDelegate#parseBeanDefinitionAttributes
     * @since 1.0.0
     */
    @SneakyThrows
    private void registerWebsocketService(BeanDefinitionRegistry registry,
                                          @NotNull AnnotationMetadata annotationMetadata) {
        String className = annotationMetadata.getClassName();

        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        Class<?> cls = Class.forName(className);
        beanDefinition.setBeanClass(cls);
        beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        beanDefinition.setPrimary(true);

        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, cls.getName());
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
        log.info("?????? Websocket Endpoint Server: [{}]", className);
    }

    /**
     * ?????? @EnableWebSocket ????????????????????????, ????????????, ??????????????????????????????
     *
     * @param importingClassMetadata importing class metadata
     * @return the base packages
     * @since 1.0.0
     */
    private @NotNull Set<String> getBasePackages(@NotNull AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata
            .getAnnotationAttributes(EnableWebSocket.class.getCanonicalName());

        Set<String> basePackages = new HashSet<>();
        for (String pkg : (String[]) Objects.requireNonNull(attributes).get(EnableWebSocket.VALUE)) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (String pkg : (String[]) attributes.get(EnableWebSocket.BASE_PACKAGES)) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (Class<?> clazz : (Class<?>[]) attributes.get(EnableWebSocket.BASE_PACKAGECLASSES)) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }

        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        return basePackages;
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
     * Sets resource loader *
     *
     * @param resourceLoader resource loader
     * @since 2022.1.1
     */
    @Override
    public void setResourceLoader(@NotNull ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Sets environment *
     *
     * @param environment environment
     * @since 2022.1.1
     */
    @Override
    public void setEnvironment(@NotNull Environment environment) {
        this.environment = environment;
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
