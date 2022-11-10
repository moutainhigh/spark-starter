package info.spark.starter.retry.limiter;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.TimeLimiter;

import com.github.rholder.retry.AttemptTimeLimiter;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.Immutable;

/**
 * <p>Description:  </p>
 *
 * @author liujintao
 * @version 1.0.0
 * @email "mailto:liujintao@gmail.com"
 * @date 2020.07.07 10:24
 * @see com.github.rholder.retry.AttemptTimeLimiters
 * @since 1.5.0
 */
public final class SparkAttemptTimeLimiters {
    /**
     * Spark attempt time limiters
     *
     * @since 1.5.0
     */
    private SparkAttemptTimeLimiters() {
    }

    /**
     * No time limit
     *
     * @param <V> The type of the computation result.
     * @return an {@link AttemptTimeLimiter} impl which has no time limit
     * @since 1.5.0
     */
    public static <V> AttemptTimeLimiter<V> noTimeLimit() {
        return new SparkAttemptTimeLimiters.NoAttemptTimeLimit<>();
    }


    /**
     * Fixed time limit
     *
     * @param <V>             the type of the computation result
     * @param duration        that an attempt may persist before being circumvented
     * @param timeUnit        of the 'duration' arg
     * @param executorService used to enforce time limit
     * @return an {@link AttemptTimeLimiter} with a fixed time limit for each attempt
     * @since 1.5.0
     */
    public static <V> AttemptTimeLimiter<V> fixedTimeLimit(long duration, TimeUnit timeUnit,
                                                           ExecutorService executorService) {
        Preconditions.checkNotNull(timeUnit);
        return new FixedAttemptTimeLimit<>(duration, timeUnit, executorService);
    }

    /**
         * <p>Description: </p>
     *
     * @param <V> parameter
     * @author liujintao
     * @version 1.0.0
     * @email "mailto:liujintao@gmail.com"
     * @date 2020.07.07 10:28
     * @since 1.5.0
     */
    @Immutable
    private static final class NoAttemptTimeLimit<V> implements AttemptTimeLimiter<V> {
        /**
         * Call
         *
         * @param callable callable
         * @return the v
         * @throws Exception exception
         * @since 1.5.0
         */
        @Override
        public V call(Callable<V> callable) throws Exception {
            return callable.call();
        }
    }

    /**
         * <p>Description: </p>
     *
     * @param <V> parameter
     * @author liujintao
     * @version 1.0.0
     * @email "mailto:liujintao@gmail.com"
     * @date 2020.07.07 10:28
     * @since 1.5.0
     */
    @Immutable
    private static final class FixedAttemptTimeLimit<V> implements AttemptTimeLimiter<V> {

        /** Time limiter */
        private final TimeLimiter timeLimiter;
        /** Duration */
        private final long duration;
        /** Time unit */
        private final TimeUnit timeUnit;


        /**
         * Fixed attempt time limit
         *
         * @param duration        duration
         * @param timeUnit        time unit
         * @param executorService executor service
         * @since 1.5.0
         */
        FixedAttemptTimeLimit(long duration, TimeUnit timeUnit, ExecutorService executorService) {
            this(new SparkSimpleTimeLimiter(executorService), duration, timeUnit);
        }

        /**
         * Fixed attempt time limit
         *
         * @param timeLimiter time limiter
         * @param duration    duration
         * @param timeUnit    time unit
         * @since 1.5.0
         */
        private FixedAttemptTimeLimit(TimeLimiter timeLimiter, long duration, TimeUnit timeUnit) {
            Preconditions.checkNotNull(timeLimiter);
            Preconditions.checkNotNull(timeUnit);
            this.timeLimiter = timeLimiter;
            this.duration = duration;
            this.timeUnit = timeUnit;
        }

        /**
         * Call
         *
         * @param callable callable
         * @return the v
         * @throws Exception exception
         * @since 1.5.0
         */
        @Override
        public V call(Callable<V> callable) throws Exception {
            return this.timeLimiter.callWithTimeout(callable, this.duration, this.timeUnit);
        }
    }
}
