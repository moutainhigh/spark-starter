package info.spark.feign.adapter.registrar;

import feign.Feign;
import feign.Target;

/**
 * <p>Description: Feign Client 的代理接口 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.19 02:50
 * @since 1.0.0
 */
interface Targeter {
    /**
     * Target t
     *
     * @param <T>     parameter
     * @param factory factory
     * @param feign   feign
     * @param target  target
     * @return the t
     * @since 1.0.0
     */
    <T> T target(FeignClientFactoryBean factory, Feign.Builder feign, Target.HardCodedTarget<T> target);
}
