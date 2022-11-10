package sample.doc.restdoc.repository;

/**
 * <p>Description: </p>
 *
 * @param <T> parameter
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.15 22:23
 * @since 1.0.0
 */
public interface Identifiable<T> {
    /**
     * 标识一个对象
     *
     * @return 对象的可读标识符 identifier
     * @since 1.0.0
     */
    T getIdentifier();
}
