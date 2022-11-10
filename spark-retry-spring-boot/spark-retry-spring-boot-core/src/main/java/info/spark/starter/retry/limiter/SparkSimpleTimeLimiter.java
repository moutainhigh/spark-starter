package info.spark.starter.retry.limiter;

import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.common.util.concurrent.UncheckedTimeoutException;
import com.google.common.util.concurrent.Uninterruptibles;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <p>Description:  </p>
 *
 * @author liujintao
 * @version 1.0.0
 * @email "mailto:liujintao@gmail.com"
 * @date 2020.07.07 10:19
 * @see SimpleTimeLimiter
 * @since 1.5.0
 */
public class SparkSimpleTimeLimiter implements TimeLimiter {

    /** Executor */
    private final ExecutorService executor;

    /**
     * Spark simple time limiter
     *
     * @param executor executor
     * @since 1.5.0
     */
    public SparkSimpleTimeLimiter(ExecutorService executor) {
        this.executor = checkNotNull(executor);
    }

    /**
     * Creates a TimeLimiter instance using the given executor service to execute method calls.
     *
     * <p><b>Warning:</b> using a bounded executor may be counterproductive! If the thread pool fills
     * up, any time callers spend waiting for a thread may count toward their time limit, and in this
     * case the call may even time out before the target method is ever invoked.
     *
     * @param executor the ExecutorService that will execute the method calls on the target objects;     for example, a
     *                 {@link Executors#newCachedThreadPool()}.
     * @return the spark simple time limiter
     * @since 22.0
     */
    public static SparkSimpleTimeLimiter create(ExecutorService executor) {
        return new SparkSimpleTimeLimiter(executor);
    }

    /**
     * New proxy
     *
     * @param <T>             parameter
     * @param target          target
     * @param interfaceType   interface type
     * @param timeoutDuration timeout duration
     * @param timeoutUnit     timeout unit
     * @return the t
     * @since 1.5.0
     */
    @Override
    public <T> T newProxy(
        T target,
        Class<T> interfaceType,
        long timeoutDuration,
        TimeUnit timeoutUnit) {
        checkNotNull(target);
        checkNotNull(interfaceType);
        checkNotNull(timeoutUnit);
        checkPositiveTimeout(timeoutDuration);
        checkArgument(interfaceType.isInterface(), "interfaceType must be an interface type");

        Set<Method> interruptibleMethods = findInterruptibleMethods(interfaceType);

        InvocationHandler handler =
            (obj, method, args) -> {
                Callable<Object> callable =
                    () -> {
                        try {
                            return method.invoke(target, args);
                        } catch (InvocationTargetException e) {
                            throw throwCause(e, false /* combineStackTraces */);
                        }
                    };
                return SparkSimpleTimeLimiter.this.callWithTimeout(
                    callable, timeoutDuration, timeoutUnit, interruptibleMethods.contains(method));
            };
        return newProxy(interfaceType, handler);
    }

    /**
     * New proxy
     *
     * @param <T>           parameter
     * @param interfaceType interface type
     * @param handler       handler
     * @return the t
     * @since 1.5.0
     */
    private static <T> T newProxy(Class<T> interfaceType, InvocationHandler handler) {
        Object object =
            Proxy.newProxyInstance(
                interfaceType.getClassLoader(), new Class<?>[] {interfaceType}, handler);
        return interfaceType.cast(object);
    }

    /**
     * Call with timeout
     *
     * @param <T>             parameter
     * @param callable        callable
     * @param timeoutDuration timeout duration
     * @param timeoutUnit     timeout unit
     * @param amInterruptible am interruptible
     * @return the t
     * @throws Exception exception
     * @since 1.5.0
     */
    private <T> T callWithTimeout(
        Callable<T> callable, long timeoutDuration, TimeUnit timeoutUnit, boolean amInterruptible)
        throws Exception {
        checkNotNull(callable);
        checkNotNull(timeoutUnit);
        checkPositiveTimeout(timeoutDuration);

        Future<T> future = this.executor.submit(callable);

        try {
            if (amInterruptible) {
                try {
                    return future.get(timeoutDuration, timeoutUnit);
                } catch (InterruptedException e) {
                    future.cancel(true);
                    throw e;
                }
            } else {
                return Uninterruptibles.getUninterruptibly(future, timeoutDuration, timeoutUnit);
            }
        } catch (ExecutionException e) {
            throw throwCause(e, true);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new UncheckedTimeoutException(e);
        }
    }

    /**
     * Call with timeout
     *
     * @param <T>             parameter
     * @param callable        callable
     * @param timeoutDuration timeout duration
     * @param timeoutUnit     timeout unit
     * @return the t
     * @throws TimeoutException     timeout exception
     * @throws InterruptedException interrupted exception
     * @throws ExecutionException   execution exception
     * @since 1.5.0
     */
    @Override
    public <T> T callWithTimeout(Callable<T> callable, long timeoutDuration, TimeUnit timeoutUnit)
        throws TimeoutException, InterruptedException, ExecutionException {
        checkNotNull(callable);
        checkNotNull(timeoutUnit);
        checkPositiveTimeout(timeoutDuration);

        Future<T> future = this.executor.submit(callable);

        try {
            return future.get(timeoutDuration, timeoutUnit);
        } catch (InterruptedException | TimeoutException e) {
            future.cancel(true);
            throw e;
        } catch (ExecutionException e) {
            this.wrapAndThrowExecutionExceptionOrError(e.getCause());
            throw new AssertionError();
        }
    }

    /**
     * Call uninterruptibly with timeout
     *
     * @param <T>             parameter
     * @param callable        callable
     * @param timeoutDuration timeout duration
     * @param timeoutUnit     timeout unit
     * @return the t
     * @throws TimeoutException   timeout exception
     * @throws ExecutionException execution exception
     * @since 1.5.0
     */
    @Override
    public <T> T callUninterruptiblyWithTimeout(
        Callable<T> callable, long timeoutDuration, TimeUnit timeoutUnit)
        throws TimeoutException, ExecutionException {
        checkNotNull(callable);
        checkNotNull(timeoutUnit);
        checkPositiveTimeout(timeoutDuration);

        Future<T> future = this.executor.submit(callable);

        try {
            return Uninterruptibles.getUninterruptibly(future, timeoutDuration, timeoutUnit);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw e;
        } catch (ExecutionException e) {
            this.wrapAndThrowExecutionExceptionOrError(e.getCause());
            throw new AssertionError();
        }
    }

    /**
     * Run with timeout
     *
     * @param runnable        runnable
     * @param timeoutDuration timeout duration
     * @param timeoutUnit     timeout unit
     * @throws TimeoutException     timeout exception
     * @throws InterruptedException interrupted exception
     * @since 1.5.0
     */
    @Override
    public void runWithTimeout(Runnable runnable, long timeoutDuration, TimeUnit timeoutUnit)
        throws TimeoutException, InterruptedException {
        checkNotNull(runnable);
        checkNotNull(timeoutUnit);
        checkPositiveTimeout(timeoutDuration);

        Future<?> future = this.executor.submit(runnable);

        try {
            future.get(timeoutDuration, timeoutUnit);
        } catch (InterruptedException | TimeoutException e) {
            future.cancel(true);
            throw e;
        } catch (ExecutionException e) {
            this.wrapAndThrowRuntimeExecutionExceptionOrError(e.getCause());
            throw new AssertionError();
        }
    }

    /**
     * Run uninterruptibly with timeout
     *
     * @param runnable        runnable
     * @param timeoutDuration timeout duration
     * @param timeoutUnit     timeout unit
     * @throws TimeoutException timeout exception
     * @since 1.5.0
     */
    @Override
    public void runUninterruptiblyWithTimeout(
        Runnable runnable, long timeoutDuration, TimeUnit timeoutUnit) throws TimeoutException {
        checkNotNull(runnable);
        checkNotNull(timeoutUnit);
        checkPositiveTimeout(timeoutDuration);

        Future<?> future = this.executor.submit(runnable);

        try {
            Uninterruptibles.getUninterruptibly(future, timeoutDuration, timeoutUnit);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw e;
        } catch (ExecutionException e) {
            this.wrapAndThrowRuntimeExecutionExceptionOrError(e.getCause());
            throw new AssertionError();
        }
    }

    /**
     * Throw cause
     *
     * @param e                  e
     * @param combineStackTraces combine stack traces
     * @return the exception
     * @throws Exception exception
     * @since 1.5.0
     */
    private static Exception throwCause(Exception e, boolean combineStackTraces) throws Exception {
        Throwable cause = e.getCause();
        if (cause == null) {
            throw e;
        }
        if (combineStackTraces) {
            StackTraceElement[] combined =
                ObjectArrays.concat(cause.getStackTrace(), e.getStackTrace(), StackTraceElement.class);
            cause.setStackTrace(combined);
        }
        if (cause instanceof Exception) {
            throw (Exception) cause;
        }
        if (cause instanceof Error) {
            throw (Error) cause;
        }
        // The cause is a weird kind of Throwable, so throw the outer exception.
        throw e;
    }

    /**
     * Find interruptible methods
     *
     * @param interfaceType interface type
     * @return the set
     * @since 1.5.0
     */
    private static Set<Method> findInterruptibleMethods(Class<?> interfaceType) {
        Set<Method> set = Sets.newHashSet();
        for (Method m : interfaceType.getMethods()) {
            if (declaresInterruptedEx(m)) {
                set.add(m);
            }
        }
        return set;
    }

    /**
     * Declares interrupted ex
     *
     * @param method method
     * @return the boolean
     * @since 1.5.0
     */
    private static boolean declaresInterruptedEx(Method method) {
        for (Class<?> exType : method.getExceptionTypes()) {
            // debate: == or isAssignableFrom?
            if (exType == InterruptedException.class) {
                return true;
            }
        }
        return false;
    }

    /**
     * Wrap and throw execution exception or error
     *
     * @param cause cause
     * @throws ExecutionException execution exception
     * @since 1.5.0
     */
    private void wrapAndThrowExecutionExceptionOrError(Throwable cause) throws ExecutionException {
        if (cause instanceof Error) {
            throw new ExecutionError((Error) cause);
        } else if (cause instanceof RuntimeException) {
            throw new UncheckedExecutionException(cause);
        } else {
            throw new ExecutionException(cause);
        }
    }

    /**
     * Wrap and throw runtime execution exception or error
     *
     * @param cause cause
     * @since 1.5.0
     */
    private void wrapAndThrowRuntimeExecutionExceptionOrError(Throwable cause) {
        if (cause instanceof Error) {
            throw new ExecutionError((Error) cause);
        } else {
            throw new UncheckedExecutionException(cause);
        }
    }

    /**
     * Check positive timeout
     *
     * @param timeoutDuration timeout duration
     * @since 1.5.0
     */
    private static void checkPositiveTimeout(long timeoutDuration) {
        checkArgument(timeoutDuration > 0, "timeout must be positive: %s", timeoutDuration);
    }
}
