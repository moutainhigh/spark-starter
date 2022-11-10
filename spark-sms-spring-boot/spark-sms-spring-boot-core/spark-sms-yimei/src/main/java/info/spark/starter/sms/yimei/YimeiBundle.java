package info.spark.starter.sms.yimei;

import info.spark.starter.basic.bundle.DynamicBundle;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * <p>Description: 消息外置, 需要在 resources/messages 创建对应的 [YimeiBundle.properties] 文件 </p>
 *
 * @author dong4j
 * @version 1.4.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.19 09:26
 * @since 1.4.0
 */
public final class YimeiBundle extends DynamicBundle {
    /** BUNDLE */
    @NonNls
    private static final String BUNDLE = "messages.YimeiBundle";
    /** INSTANCE */
    private static final YimeiBundle INSTANCE = new YimeiBundle();

    /**
     * Plugin bundle
     *
     * @since 0.0.1
     */
    @Contract(pure = true)
    private YimeiBundle() {
        super(BUNDLE);
    }

    /**
     * Message
     *
     * @param key    key
     * @param params params
     * @return the string
     * @since 1.4.0
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
     * @since 1.4.0
     */
    public static @NotNull Supplier<String> messagePointer(@NotNull String key, Object... params) {
        return INSTANCE.getLazyMessage(key, params);
    }
}
