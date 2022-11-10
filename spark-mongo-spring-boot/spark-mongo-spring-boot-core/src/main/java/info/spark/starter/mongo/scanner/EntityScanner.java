package info.spark.starter.mongo.scanner;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.10.7 21:21
 * @since 1.4.0
 */
@Slf4j
public class EntityScanner {

    /** Context */
    private final ApplicationContext context;
    /** Scanner packages */
    @Setter
    @Getter
    private List<String> scannerPackages;

    /**
     * Create a new {@link EntityScanner} instance.
     *
     * @param context the source application context
     * @since 1.0.0
     */
    @Contract(pure = true)
    public EntityScanner(ApplicationContext context) {
        Assert.notNull(context, "Context must not be null");
        this.context = context;
        this.scannerPackages = this.initPackages();
    }

    /**
     * Scan for entities with the specified annotations.
     *
     * @param annotationTypes the annotation types used on the entities
     * @return a set of entity classes
     * @throws ClassNotFoundException if an entity class cannot be loaded
     * @since 1.0.0
     */
    @SafeVarargs
    public final @NotNull
    Set<Class<?>> scan(Class<? extends Annotation>... annotationTypes) throws ClassNotFoundException {
        List<String> packages = this.getScannerPackages();
        if (packages.isEmpty()) {
            return Collections.emptySet();
        }
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.setEnvironment(this.context.getEnvironment());
        scanner.setResourceLoader(this.context);
        for (Class<? extends Annotation> annotationType : annotationTypes) {
            scanner.addIncludeFilter(new AnnotationTypeFilter(annotationType));
        }
        Set<Class<?>> entitySet = new HashSet<>();
        for (String basePackage : packages) {
            if (StringUtils.hasText(basePackage)) {
                for (BeanDefinition candidate : scanner.findCandidateComponents(basePackage)) {
                    entitySet.add(ClassUtils.forName(Objects.requireNonNull(candidate.getBeanClassName()), this.context.getClassLoader()));
                }
            }
        }
        return entitySet;
    }

    /**
     * 如果不存在 EntityScanPackages bean, 则使用启动类所在的包和子包
     *
     * @return the packages
     * @since 1.0.0
     */
    private List<String> initPackages() {
        List<String> packages = EntityScanPackages.get(this.context).getPackageNames();
        if (packages.isEmpty() && AutoConfigurationPackages.has(this.context)) {
            packages = AutoConfigurationPackages.get(this.context);
        }
        return packages;
    }

}
