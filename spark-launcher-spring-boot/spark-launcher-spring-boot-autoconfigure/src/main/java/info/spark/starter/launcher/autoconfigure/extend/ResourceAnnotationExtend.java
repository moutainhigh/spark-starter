package info.spark.starter.launcher.autoconfigure.extend;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * spring 自动注入对象通常会用到 @Autowired (spring 自定义注解), @Resource (JSR-250 规范定义的注解) 两个注解,
 * Autowired 支持 required = false 的设置可以允许注入对象为 null, 而 Resource 不支持该特性, 这里手动扩展 spring 使其支持 null 对象注入.
 * 直接继承 CommonAnnotationBeanPostProcessor, 作为 BeanPostProcessor 组件加载
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.28 01:11
 * @see CommonAnnotationBeanPostProcessor#autowireResource CommonAnnotationBeanPostProcessor
 *     #autowireResourceCommonAnnotationBeanPostProcessor#autowireResource
 * @since 1.0.0
 */
@Slf4j
public class ResourceAnnotationExtend extends CommonAnnotationBeanPostProcessor {

    /** serialVersionUID */
    private static final long serialVersionUID = 3463920095157475670L;

    /**
     * 通过重写 CommonAnnotationBeanPostProcessor 在对象注入前, 重写该类中 autowireResource 方法,
     * 在 BeanFactory 中找不到该对象是默认返回 null 对象给到引用方, 从而不影响这个 spring 容器的加载.
     * 多数情况下用于单元测试阶段 (因为不想因为不相干的 bean 对象找不到从而影响整个 spring 容器的加载).
     * 有一个缺点就是不能像 @Autowired 那样细粒度控制到具体某个对象允许为 null, 这里相当于给 @Resource 加了个全局开关.
     *
     * @param factory            factory
     * @param element            element
     * @param requestingBeanName requesting bean name
     * @return the object
     * @throws BeansException beans exception
     * @since 1.0.0
     */
    @NotNull
    @Override
    protected Object autowireResource(BeanFactory factory, LookupElement element, String requestingBeanName) throws BeansException {
        Object obj = null;
        try {
            obj = super.autowireResource(factory, element, requestingBeanName);
        } catch (Throwable e) {
            log.warn("[{}] is null", requestingBeanName);
        }
        return obj;
    }

}
