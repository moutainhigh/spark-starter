package info.spark.agent.adapter;

import info.spark.starter.basic.bundle.DynamicBundle;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * <p>Description: 消息外置, 需要在 resources/messages 创建对应的 [AuthStarterCodeBundle.properties] 文件 </p>
 *
 * @author admin
 * @version 1.5.0
 * @email "mailto:admin@gmail.com"
 * @date 2020.06.30 17:51
 * @since 1.5.0
 */
public final class AgentBundle extends DynamicBundle {
    /** BUNDLE */
    @NonNls
    private static final String BUNDLE = "messages.AgentBundle";
    /** INSTANCE */
    private static final AgentBundle INSTANCE = new AgentBundle();

    /**
     * Plugin bundle
     *
     * @since 0.0.1
     */
    @Contract(pure = true)
    private AgentBundle() {
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
