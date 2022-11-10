package info.spark.starter.dubbo;

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
 * @date 2021.10.30 18:52
 * @since 1.0.0
 */
public final class DubboBundle extends DynamicBundle {
    /** BUNDLE */
    @NonNls
    private static final String BUNDLE = "messages.DubboBundle";
    /** INSTANCE */
    private static final DubboBundle INSTANCE = new DubboBundle();

    /**
     * Plugin bundle
     *
     * @since 2.0.0
     */
    @Contract(pure = true)
    private DubboBundle() {
        super(BUNDLE);
    }

    /**
     * Message
     *
     * @param key    key
     * @param params params
     * @return the string
     * @since 2.0.0
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
     * @since 2.0.0
     */
    public static @NotNull Supplier<String> messagePointer(@NotNull String key, Object... params) {
        return INSTANCE.getLazyMessage(key, params);
    }
}
