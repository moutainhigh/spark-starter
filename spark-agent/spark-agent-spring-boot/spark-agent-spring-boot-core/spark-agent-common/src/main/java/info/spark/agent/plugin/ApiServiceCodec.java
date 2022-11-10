package info.spark.agent.plugin;

/**
 * <p>Description: 参数解码与编码插件 </p>
 *
 * @param <T> parameter
 * @param <K> parameter
 * @author dong4j
 * @version 1.0.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.31 02:53
 * @since 1.0.0
 */
public interface ApiServiceCodec<T, K> extends Plugin {
    /**
     * 将响应结果序列化为 byte[]
     *
     * @param t t
     * @return the byte [ ]
     * @since 1.0.0
     */
    byte[] encode(T t);

    /**
     * 将入参 byte[] 反序列化为对应实体
     *
     * @param in  in
     * @param cls cls
     * @return the k
     * @since 1.0.0
     */
    K decode(byte[] in, Class<?> cls);
}
