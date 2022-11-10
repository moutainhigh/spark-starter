package info.spark.feign.adapter.annotation;

import info.spark.feign.adapter.registrar.FeignClientsRegistrar;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description: 扫描声明它们是外部客户机的接口 (通过@FeignClient**) .配置用于@Configuration**类的组件扫描指令 </p>
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
@Import(FeignClientsRegistrar.class)
public @interface EnableFeignClients {
    /** VALUE */
    String VALUE = "value";
    /** BASE_PACKAGES */
    String BASE_PACKAGES = "basePackages";
    /** BASE_PACKAGECLASSES */
    String BASE_PACKAGECLASSES = "basePackageClasses";

    /**
     * basePackages () 属性的别名.允许更简洁的注释声明,例如: @ComponentScan (“org.my.pkg”) 而不是@ComponentScan (basePackages=“org.my.pkg”) .
     *
     * @return the string [ ]
     * @since 1.0.0
     */
    String[] value() default {};

    /**
     * 扫描带注释组件的基本包. value () 是此属性的别名 (与互斥) . 使用 basePackageClasses () 作为基于字符串的包名称的类型安全替代方法.
     * 默认为 'info.spark.agent.adapter.feign', 目前所有的 client 都则此目录下, 如果业务端想本地实现 feign client, 添加到 basePackages 即可
     *
     * @return the array of 'basePackages'.
     * @since 1.0.0
     */
    String[] basePackages() default {"info.spark.agent.adapter.feign"};

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
     * 所有外部客户机的自定义@Configuration.可以包含组成客户端的块的override@Bean定义,例如feign.codec.Decoder、feign.codec.Encoder、feign.Contract
     *
     * @return the class [ ]
     * @since 1.0.0
     */
    Class<?>[] defaultConfiguration() default {};

    /**
     * 用 @FeignClient 注释的类列表.如果不为空,则禁用类路径扫描
     *
     * @return list of FeignClient classes
     * @since 1.0.0
     */
    Class<?>[] clients() default {};
}
