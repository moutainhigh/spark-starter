package sample.doc.dubbo.api.service;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.15 19:31
 * @since 1.4.0
 */
public interface PushService {

    /**
     * Push string
     *
     * @param account account
     * @return the string
     * @since 1.4.0
     */
    String push(String account);
}
