package info.spark.starter.mongo.scanner;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.10.7 21:21
 * @since 1.4.0
 */
public class EntityScanPackages {

    /** BEAN */
    private static final String BEAN = EntityScanPackages.class.getName();
    /** NONE */
    private static final EntityScanPackages NONE = new EntityScanPackages();
    /** Package names */
    private final List<String> packageNames;

    /**
     * Entity scan packages
     *
     * @param packageNames package names
     * @since 1.0.0
     */
    public EntityScanPackages(@NotNull String... packageNames) {
        List<String> packages = new ArrayList<>();
        for (String name : packageNames) {
            if (StringUtils.hasText(name)) {
                packages.add(name);
            }
        }
        this.packageNames = Collections.unmodifiableList(packages);
    }

    /**
     * Return the package names specified from all {@link EntityScanner @EntityScanner}
     * annotations.
     *
     * @return the entity scan package names
     * @since 1.0.0
     */
    List<String> getPackageNames() {
        return this.packageNames;
    }

    /**
     * Return the {@link EntityScanPackages} for the given bean factory.
     *
     * @param beanFactory the source bean factory
     * @return the {@link EntityScanPackages} for the bean factory (never {@code null})
     * @since 1.0.0
     */
    static EntityScanPackages get(@NotNull BeanFactory beanFactory) {
        // Currently we only store a single base package, but we return a list to
        // allow this to change in the future if needed
        try {
            return beanFactory.getBean(BEAN, EntityScanPackages.class);
        } catch (NoSuchBeanDefinitionException ex) {
            return NONE;
        }
    }

    /**
     * Register the specified entity scan packages with the system.
     *
     * @param registry     the source registry
     * @param packageNames the package names to register
     * @since 1.0.0
     */
    public static void register(BeanDefinitionRegistry registry, String... packageNames) {
        Assert.notNull(registry, "Registry must not be null");
        Assert.notNull(packageNames, "PackageNames must not be null");
        register(registry, Arrays.asList(packageNames));
    }

    /**
     * Register the specified entity scan packages with the system.
     *
     * @param registry     the source registry
     * @param packageNames the package names to register
     * @since 1.0.0
     */
    static void register(BeanDefinitionRegistry registry, Collection<String> packageNames) {
        Assert.notNull(registry, "Registry must not be null");
        Assert.notNull(packageNames, "PackageNames must not be null");
        if (registry.containsBeanDefinition(BEAN)) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(BEAN);
            ConstructorArgumentValues constructorArguments = beanDefinition.getConstructorArgumentValues();
            constructorArguments.addIndexedArgumentValue(0, addPackageNames(constructorArguments, packageNames));
        } else {
            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
            beanDefinition.setBeanClass(EntityScanPackages.class);
            beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0,
                                                                                  StringUtils.toStringArray(packageNames));
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            registry.registerBeanDefinition(BEAN, beanDefinition);
        }
    }

    /**
     * Add package names string [ ]
     *
     * @param constructorArguments constructor arguments
     * @param packageNames         package names
     * @return the string [ ]
     * @since 1.0.0
     */
    @NotNull
    private static String[] addPackageNames(@NotNull ConstructorArgumentValues constructorArguments,
                                            Collection<String> packageNames) {
        String[] existing = (String[]) Objects.requireNonNull(constructorArguments.getIndexedArgumentValue(0, String[].class)).getValue();
        Set<String> merged = new LinkedHashSet<>();
        merged.addAll(Arrays.asList(Objects.requireNonNull(existing)));
        merged.addAll(packageNames);
        return StringUtils.toStringArray(merged);
    }
}
