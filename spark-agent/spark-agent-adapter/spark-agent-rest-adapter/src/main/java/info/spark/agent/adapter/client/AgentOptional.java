package info.spark.agent.adapter.client;

import info.spark.starter.basic.asserts.Assertions;
import info.spark.starter.basic.exception.BasicException;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @param <T> parameter
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.10.13 22:13
 * @since 1.6.0
 */
@Slf4j
public final class AgentOptional<T> {
    /** EMPTY */
    private static final AgentOptional<?> EMPTY = new AgentOptional<>();

    /** Value */
    private final T value;

    /**
     * Optional
     *
     * @since 1.6.0
     */
    @Contract(pure = true)
    private AgentOptional() {
        this.value = null;
    }

    /**
     * Empty
     *
     * @param <T> parameter
     * @return the optional
     * @since 1.6.0
     */
    @Contract(pure = true)
    public static <T> AgentOptional<T> empty() {
        @SuppressWarnings("unchecked")
        AgentOptional<T> t = (AgentOptional<T>) EMPTY;
        return t;
    }

    /**
     * Optional
     *
     * @param value value
     * @since 1.6.0
     */
    @Contract(pure = true)
    private AgentOptional(T value) {
        this.value = Objects.requireNonNull(value);
    }

    /**
     * Of
     *
     * @param <T>   parameter
     * @param value value
     * @return the optional
     * @since 1.6.0
     */
    @Contract(value = "_ -> new", pure = true)
    public static <T> @NotNull AgentOptional<T> of(T value) {
        return new AgentOptional<>(value);
    }

    /**
     * Of nullable
     *
     * @param <T>   parameter
     * @param value value
     * @return the optional
     * @since 1.6.0
     */
    @Contract("!null -> !null")
    public static <T> AgentOptional<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }

    /**
     * Get
     *
     * @return the t
     * @since 1.6.0
     */
    @Contract(pure = true)
    @SuppressWarnings("checkstyle:Indentation")
    public T get() {
        Assertions.notNull(this.value,
                           "数据异常(获取的数据不能为 null)",
                           () -> log.error("为避免 NPE, 请在使用 get() 之前用 isPresent(), 或使用 ofElesXxx() 操作"));
        return this.value;
    }

    /**
     * Is present
     *
     * @return the boolean
     * @since 1.6.0
     */
    @Contract(pure = true)
    @SuppressWarnings("all")
    public boolean isPresent() {
        return this.value != null;
    }

    /**
     * If present
     *
     * @param consumer consumer
     * @since 1.6.0
     */
    public void ifPresent(Consumer<? super T> consumer) {
        if (this.value != null) {
            consumer.accept(this.value);
        }
    }

    /**
     * Filter
     *
     * @param predicate predicate
     * @return the optional
     * @since 1.6.0
     */
    public AgentOptional<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (!this.isPresent()) {
            return this;
        } else {
            return predicate.test(this.value) ? this : empty();
        }
    }

    /**
     * Map
     *
     * @param <U>    parameter
     * @param mapper mapper
     * @return the optional
     * @since 1.6.0
     */
    public <U> AgentOptional<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (!this.isPresent()) {
            return empty();
        } else {
            return AgentOptional.ofNullable(mapper.apply(this.value));
        }
    }

    /**
     * Flat map
     *
     * @param <U>    parameter
     * @param mapper mapper
     * @return the optional
     * @since 1.6.0
     */
    public <U> AgentOptional<U> flatMap(Function<? super T, AgentOptional<U>> mapper) {
        Objects.requireNonNull(mapper);
        if (!this.isPresent()) {
            return empty();
        } else {
            return Objects.requireNonNull(mapper.apply(this.value));
        }
    }

    /**
     * Or else
     *
     * @param other other
     * @return the t
     * @since 1.6.0
     */
    @Contract(pure = true)
    public T orElse(T other) {
        return this.value != null ? this.value : other;
    }

    /**
     * Or else get
     *
     * @param other other
     * @return the t
     * @since 1.6.0
     */
    public T orElseGet(Supplier<? extends T> other) {
        return this.value != null ? this.value : other.get();
    }

    /**
     * Or else throw
     *
     * @param <X>               parameter
     * @param exceptionSupplier exception supplier
     * @return the t
     * @throws X x
     * @since 1.6.0
     */
    public <X extends BasicException> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (this.value != null) {
            return this.value;
        } else {
            throw exceptionSupplier.get();
        }
    }

    /**
     * Equals
     *
     * @param obj obj
     * @return the boolean
     * @since 1.6.0
     */
    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof AgentOptional)) {
            return false;
        }

        AgentOptional<?> other = (AgentOptional<?>) obj;
        return Objects.equals(this.value, other.value);
    }

    /**
     * Hash code
     *
     * @return the int
     * @since 1.6.0
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(this.value);
    }

    /**
     * To string
     *
     * @return the string
     * @since 1.6.0
     */
    @Override
    public String toString() {
        return this.value != null
               ? String.format("Optional[%s]", this.value)
               : "Optional.empty";
    }
}
