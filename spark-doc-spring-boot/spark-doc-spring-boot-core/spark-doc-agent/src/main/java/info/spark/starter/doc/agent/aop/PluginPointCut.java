package info.spark.starter.doc.agent.aop;

import org.jetbrains.annotations.NotNull;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.StaticMethodMatcherPointcut;

import java.lang.reflect.Method;

import springfox.documentation.spring.web.readers.operation.OperationParameterReader;

/**
 * <p>Description:  </p>
 *
 * @author wanghao
 * @version 1.7.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.11.27 15:22
 * @since 1.7.0
 */
public class PluginPointCut extends StaticMethodMatcherPointcut implements ClassFilter {

    /**
     * ProducerInterceptorMatches
     *
     * @param method      method
     * @param targetClass target class
     * @return the boolean
     * @since 1.7.0
     */
    @Override
    public boolean matches(@NotNull Method method, @NotNull Class<?> targetClass) {
        return OperationParameterReader.class.isAssignableFrom(targetClass);
    }

    /**
     * ProducerInterceptorMatches
     *
     * @param clazz clazz
     * @return the boolean
     * @since 1.7.0
     */
    @Override
    public boolean matches(Class<?> clazz) {
        return OperationParameterReader.class.isAssignableFrom(clazz);
    }
}
