package info.spark.starter.es.exception;

import info.spark.starter.basic.bundle.DynamicBundle;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.11.15 13:24
 * @since 1.0.0
 */
public final class EsBundle extends DynamicBundle {
    /**
     * BUNDLE
     */
    @NonNls
    private static final String BUNDLE = "messages.EsBundle";
    /**
     * INSTANCE
     */
    private static final EsBundle INSTANCE = new EsBundle();

    /**
     * Plugin bundle
     *
     * @since 1.0.0
     */
    @Contract(pure = true)
    private EsBundle() {
        super(BUNDLE);
    }

    /**
     * Message
     *
     * @param key    key
     * @param params params
     * @return the string
     * @since 1.0.0
     */
    @NotNull
    public static String message(@NotNull String key, Object... params) {
        return INSTANCE.getMessage(key, params);
    }

    /**
     * Message pointer
     *
     * @param key    key
     * @param params params
     * @return the supplier
     * @since 1.0.0
     */
    public static @NotNull Supplier<String> messagePointer(@NotNull String key, Object... params) {
        return INSTANCE.getLazyMessage(key, params);
    }
}
