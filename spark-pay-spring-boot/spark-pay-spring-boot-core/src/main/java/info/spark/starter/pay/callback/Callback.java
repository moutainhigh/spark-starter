package info.spark.starter.pay.callback;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * <p>Description: 标记回调方法 </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.04.01 20:40
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Callback {

    /**
     * 业务处理类
     *
     * @return the string
     * @since 1.0.0
     */
    Class<? extends CallbackService> value();

    /**
     * 分布式锁释放时间
     *
     * @return the int
     * @since 1.0.0
     */
    int timeout() default 5;

    /**
     * 超时释放锁的时间单位
     *
     * @return the time unit
     * @since 1.0.0
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 重试次数
     *
     * @return the int
     * @since 1.0.0
     */
    int retries() default 3;
}
