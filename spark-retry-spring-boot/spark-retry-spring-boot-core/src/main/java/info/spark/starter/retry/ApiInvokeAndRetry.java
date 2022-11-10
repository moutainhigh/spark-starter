package info.spark.starter.retry;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;

import info.spark.starter.core.function.CheckedCallable;
import info.spark.starter.retry.exception.SparkRetryException;
import info.spark.starter.retry.limiter.SparkAttemptTimeLimiters;

import org.jetbrains.annotations.Contract;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 重试工具类 </p>
 *
 * @author liujintao
 * @version 1.0.0
 * @email "mailto:liujintao@gmail.com"
 * @date 2020.07.06 17:08
 * @since 1.5.0
 */
@Slf4j
public final class ApiInvokeAndRetry {
    /**
     * Api invoke and retry
     *
     * @since 1.5.0
     */
    @Contract(pure = true)
    private ApiInvokeAndRetry() {

    }

    /**
     * 重试 callable 逻辑, 默认重试次数 1 次, 间隔 3 秒
     *
     * @param <T>      业务逻辑返回值
     * @param callable 业务逻辑
     * @return the t
     * @since 1.5.0
     */
    public static <T> T retry(CheckedCallable<T> callable) {
        return retry(callable, 3000L);
    }

    /**
     * 重试 callable 逻辑, 默认重试次数 1 次, 间隔时间由调用方指定, 默认不设置超时
     *
     * @param <T>      业务逻辑返回值
     * @param callable 业务逻辑
     * @param waitTime 重试间隔时间
     * @return the t
     * @since 1.5.0
     */
    public static <T> T retry(CheckedCallable<T> callable, Long waitTime) {
        return retry(callable, waitTime, 0);
    }

    /**
     * 重试 callable 逻辑, 重试次数由调用方指定, 间隔时间由调用方指定, 默认不设置超时
     *
     * @param <T>        业务逻辑返回值
     * @param callable   业务逻辑
     * @param waitTime   重试间隔时间
     * @param retryTimes 重试次数
     * @return the t
     * @since 1.5.0
     */
    public static <T> T retry(CheckedCallable<T> callable, Long waitTime, Integer retryTimes) {
        return retry(callable, waitTime, retryTimes, 0L);
    }

    /**
     * 重试 callable 逻辑, 重试次数由调用方指定, 间隔时间由调用方指定, 超时时间由调用方指定, 单位默认为毫秒
     *
     * @param <T>         业务逻辑返回值
     * @param callable    业务逻辑
     * @param waitTime    重试间隔时间
     * @param retryTimes  重试次数
     * @param timeOutTime 重试超时时间
     * @return the t
     * @since 1.5.0
     */
    public static <T> T retry(CheckedCallable<T> callable, Long waitTime, Integer retryTimes, Long timeOutTime) {
        return retry(callable, waitTime, retryTimes, timeOutTime, TimeUnit.MILLISECONDS);
    }

    /**
     * 重试 callable 逻辑, 重试次数由调用方指定, 间隔时间由调用方指定, 超时时间由调用方指定, 单位由调用方指定, 默认使用 {@link Exception} 作为重试条件
     *
     * @param <T>         业务逻辑返回值
     * @param callable    业务逻辑
     * @param waitTime    重试间隔时间
     * @param retryTimes  重试次数
     * @param timeOutTime 时间单位
     * @param timeUnit    时间单位
     * @return the t
     * @since 1.5.0
     */
    public static <T> T retry(CheckedCallable<T> callable, Long waitTime, Integer retryTimes, Long timeOutTime, TimeUnit timeUnit) {
        return retry(callable, waitTime, retryTimes, timeOutTime, timeUnit, Exception.class);
    }

    /**
     * 重试 callable 逻辑, 重试次数由调用方指定, 间隔时间由调用方指定, 超时时间由调用方指定, 单位由调用方指定, 重试条件由调用方指定.
     *
     * @param <T>            业务逻辑返回值
     * @param callable       业务逻辑
     * @param waitTime       重试间隔时间
     * @param retryTimes     重试次数
     * @param timeOutTime    时间单位
     * @param timeUnit       时间单位
     * @param exceptionClass 重试条件
     * @return the t
     * @since 1.5.0
     */
    @SuppressWarnings("checkstyle:ParameterNumber")
    public static <T> T retry(CheckedCallable<T> callable,
                              Long waitTime,
                              Integer retryTimes,
                              Long timeOutTime,
                              TimeUnit timeUnit,
                              Class<? extends Exception> exceptionClass) {

        Objects.requireNonNull(callable);

        return new Builder<T>()
            .waitTime(waitTime)
            .retryTimes(retryTimes)
            .timeOutTime(timeOutTime)
            .timeUnit(timeUnit)
            .exceptionClass(exceptionClass)
            .builder()
            .call(callable);
    }

    /**
         * <p>Description: </p>
     *
     * @param <T> parameter
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.07.07 16:28
     * @since 1.5.0
     */
    private static class Builder<T> {
        /** Wait time */
        private Long waitTime = 3000L;
        /** Retry times */
        private Integer retryTimes = 0;
        /** Time out time */
        private Long timeOutTime = 0L;
        /** Time unit */
        private TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        /** Exception class */
        private Class<? extends Exception> exceptionClass = Exception.class;
        /** Retryer */
        private Retryer<T> retryer;

        /**
         * Wait time
         *
         * @param waitTime wait time
         * @return the builder
         * @since 1.5.0
         */
        public Builder<T> waitTime(Long waitTime) {
            if (waitTime != null && waitTime > 0) {
                this.waitTime = waitTime;
            }
            return this;
        }

        /**
         * Retry times
         *
         * @param retryTimes retry times
         * @return the builder
         * @since 1.5.0
         */
        public Builder<T> retryTimes(Integer retryTimes) {
            if (retryTimes != null && retryTimes > 0) {
                this.retryTimes = retryTimes;
            }
            return this;
        }

        /**
         * Time out time
         *
         * @param timeOutTime time out time
         * @return the builder
         * @since 1.5.0
         */
        public Builder<T> timeOutTime(Long timeOutTime) {
            if (timeOutTime != null && timeOutTime > 0) {
                this.timeOutTime = timeOutTime;
            }
            return this;
        }

        /**
         * Time unit
         *
         * @param timeUnit time unit
         * @return the builder
         * @since 1.5.0
         */
        public Builder<T> timeUnit(TimeUnit timeUnit) {
            if (timeUnit != null) {
                this.timeUnit = timeUnit;
            }
            return this;
        }

        /**
         * Exception class
         *
         * @param exceptionClass exception class
         * @return the builder
         * @since 1.5.0
         */
        public Builder<T> exceptionClass(Class<? extends Exception> exceptionClass) {
            if (this.timeUnit != null) {
                this.exceptionClass = exceptionClass;
            }
            return this;
        }

        /**
         * Builder
         *
         * @return the builder
         * @since 1.5.0
         */
        public Builder<T> builder() {
            RetryerBuilder<T> retryerBuilder = RetryerBuilder.<T>newBuilder()
                //retryIf 重试条件
                .retryIfException()
                .retryIfRuntimeException()
                .retryIfExceptionOfType(this.exceptionClass)
                //等待策略: 每次请求间隔时间
                .withWaitStrategy(WaitStrategies.fixedWait(this.waitTime, this.timeUnit))
                //停止策略 : 尝试请求次数
                .withStopStrategy(StopStrategies.stopAfterAttempt(this.retryTimes + 1));

            if (this.timeOutTime <= 0) {
                retryerBuilder = retryerBuilder
                    .withAttemptTimeLimiter(SparkAttemptTimeLimiters.noTimeLimit());
            } else {
                ExecutorService executorService = new ThreadPoolExecutor(1,
                                                                         1,
                                                                         0L,
                                                                         TimeUnit.MILLISECONDS,
                                                                         new LinkedBlockingQueue<>(),
                                                                         new ThreadFactoryBuilder().setNameFormat("retry-pool")
                                                                             .build());
                retryerBuilder = retryerBuilder
                    //时间限制 : 某次请求不得超过指定时间 , 类似: TimeLimiter timeLimiter = new SimpleTimeLimiter();
                    .withAttemptTimeLimiter(SparkAttemptTimeLimiters.fixedTimeLimit(this.timeOutTime, this.timeUnit, executorService));
            }
            this.retryer = retryerBuilder.build();

            return this;
        }

        /**
         * Call
         *
         * @param callable callable
         * @return the t
         * @since 1.5.0
         */
        public T call(CheckedCallable<T> callable) {
            try {
                return this.retryer.call(() -> {
                    try {
                        return callable.call();
                    } catch (Throwable throwable) {
                        throw new SparkRetryException(throwable);
                    }
                });
            } catch (ExecutionException | RetryException e) {
                throw new SparkRetryException(e);
            }
        }
    }
}
