package sample.doc.restdoc.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <p>Description: </p>
 *
 * @param <K> parameter
 * @param <V> parameter
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.08 22:15
 * @since 1.0.0
 */
public class MapBackedRepository<K, V extends Identifiable<K>> {
    /** Service */
    private final Map<K, V> service = new HashMap<>();

    /**
     * Delete *
     *
     * @param key key
     * @since 1.0.0
     */
    public void delete(K key) {
        this.service.remove(key);
    }

    /**
     * Exists boolean
     *
     * @param key key
     * @return the boolean
     * @since 1.0.0
     */
    public boolean exists(K key) {
        return this.service.containsKey(key);
    }

    /**
     * Add *
     *
     * @param model model
     * @since 1.0.0
     */
    public void add(V model) {
        this.service.put(model.getIdentifier(), model);
    }

    /**
     * Get v
     *
     * @param key key
     * @return the v
     * @since 1.0.0
     */
    public V get(K key) {
        return this.service.get(key);
    }

    /**
     * First v
     *
     * @return the v
     * @since 1.0.0
     */
    public V first() {
        return this.service.values().stream().findFirst().orElse(null);
    }

    /**
     * Where list
     *
     * @param criteria criteria
     * @return the list
     * @since 1.0.0
     */
    public List<V> where(Predicate<V> criteria) {
        return this.service.values().stream().filter(criteria).collect(Collectors.toList());
    }
}
