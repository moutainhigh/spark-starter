package info.spark.agent.annotation;

import info.spark.agent.register.AgentServiceRegistrar;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description: 用于指定被 {@link ApiService} 和 {@link ApiServiceMethod} 标识的类, 注入到 IoC
 * 1. 如果不配置任何属性, 将默认扫描被标注类的包下所有的类;
 * 2. 使用 value 或者 basePackages 指定扫描的包名;
 * 3. 使用 basePackageClasses 扫描指定的每个类的包;
 * 4. 使用 services 扫描指定的 class;
 * </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.19 03:26
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(AgentServiceRegistrar.class)
public @interface EnableAgentService {
    /** VALUE */
    String VALUE = "value";
    /** BASE_PACKAGES */
    String BASE_PACKAGES = "basePackages";
    /** BASE_PACKAGECLASSES */
    String BASE_PACKAGECLASSES = "basePackageClasses";
    /** SERVICES */
    String SERVICES = "services";

    /**
     * basePackages () 属性的别名.允许更简洁的注释声明,例如: @ComponentScan (“org.my.pkg”) 而不是@ComponentScan (basePackages=“org.my.pkg”) .
     *
     * @return the string [ ]
     * @since 1.0.0
     */
    String[] value() default {};

    /**
     * 扫描带注释组件的基本包. value () 是此属性的别名 (与互斥) . 使用 basePackageClasses () 作为基于字符串的包名称的类型安全替代方法.
     *
     * @return the string [ ]
     * @since 1.0.0
     */
    String[] basePackages() default {};

    /**
     * 键入basePackages () 的安全替代项,用于指定要扫描带注释组件的包.
     * 将扫描指定的每个类的包.
     * 考虑在每个包中创建一个特殊的no-op标记类或接口,该类或接口除了被此属性引用之外没有其他用途
     *
     * @return the array of 'basePackageClasses'.
     * @since 1.0.0
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * 用 @ApiService 注释的类列表.如果不为空,则禁用类路径扫描
     *
     * @return the class [ ]
     * @since 1.0.0
     */
    Class<?>[] services() default {};

}
