package info.spark.starter.es.support;

import java.util.HashMap;
import java.util.function.Supplier;

/**
 * <p>Description:  </p>
 *
 * @param <K> parameter
 * @param <V> parameter
 * @author wanghao
 * @version 1.8.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.25 17:52
 * @since 1.8.0
 */
public class ReactorHashMap<K, V> extends HashMap<K, V> {

    /** serialVersionUID */
    private static final long serialVersionUID = -2001775413699983338L;

    /**
     * Builder
     *
     * @param <K> parameter
     * @param <V> parameter
     * @return the lambda hash map
     * @since 1.8.0
     */
    public static <K, V> ReactorHashMap<K, V> builder() {
        return new ReactorHashMap<>();
    }

    /**
     * Put
     *
     * @param key      key
     * @param supplier supplier
     * @return the lambda hash map
     * @since 1.8.0
     */
    public ReactorHashMap<K, V> put(K key, Supplier<V> supplier) {
        super.put(key, supplier.get());
        //流式
        return this;
    }
}
