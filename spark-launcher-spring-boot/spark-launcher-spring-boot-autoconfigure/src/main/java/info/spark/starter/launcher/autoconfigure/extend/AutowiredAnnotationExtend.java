package info.spark.starter.launcher.autoconfigure.extend;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 实现 @Autowired 的全局允许为 null 的扩展, @Autowired 和 @Inject 使用的是 AutowiredAnnotationBeanPostProcessor 处理器 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.28 01:21
 * @since 1.0.0
 */
@Slf4j
public class AutowiredAnnotationExtend extends AutowiredAnnotationBeanPostProcessor {

    /**
     * 同 {@link ResourceAnnotationExtend}, 在获取bean对象时捕获异常返回 null 对象
     *
     * @param pvs      pvs
     * @param bean     bean
     * @param beanName bean name
     * @return the property values
     * @throws BeanCreationException bean creation exception
     * @since 1.0.0
     */
    @Override
    public PropertyValues postProcessProperties(@NotNull PropertyValues pvs,
                                                @NotNull Object bean,
                                                @NotNull String beanName) throws BeanCreationException {
        try {
            pvs = super.postProcessProperties(pvs, bean, beanName);
        } catch (Throwable e) {
            log.warn("[{}] is null", beanName);
        }
        return pvs;
    }
}
