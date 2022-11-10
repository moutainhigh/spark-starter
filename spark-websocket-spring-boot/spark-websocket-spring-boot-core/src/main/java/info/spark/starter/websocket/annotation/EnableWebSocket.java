package info.spark.starter.websocket.annotation;

import info.spark.starter.websocket.register.ServerEndpointExporter;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.09 19:54
 * @since 2022.1.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(ServerEndpointExporter.class)
public @interface EnableWebSocket {

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
     * 用 @ServerEndpoint 注释的类列表.如果不为空,则禁用类路径扫描
     *
     * @return the class [ ]
     * @since 1.0.0
     */
    Class<?>[] services() default {};
}
