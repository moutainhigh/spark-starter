package info.spark.feign.adapter.registrar;

import org.jetbrains.annotations.NotNull;

import feign.Feign;
import feign.Target;

/**
 * <p>Description: 生成 Feign client 的代理 </p>
 * todo-dong4j : (2020年01月19日 02:53) [实现 HystrixTargeter]
 *
 * @author Spencer Gibb
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.19 02:49
 * @since 1.0.0
 */
class DefaultTargeter implements Targeter {

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
    @Override
    public <T> T target(FeignClientFactoryBean factory,
                        @NotNull Feign.Builder feign,
                        Target.HardCodedTarget<T> target) {
        return feign.target(target);
    }
}
