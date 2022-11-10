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
public interface AccountService {

    /**
     * Logout *
     *
     * @param account account
     * @since 1.4.0
     */
    void logout(String account);

    /**
     * Login boolean
     *
     * @param account  account
     * @param password password
     * @return the boolean
     * @since 1.4.0
     */
    boolean login(String account, String password);

    /**
     * Login boolean
     *
     * @param account account
     * @param code    code
     * @return the boolean
     * @since 1.4.0
     */
    boolean login(String account, int code);

    /**
     * Update info *
     *
     * @param isBoy  is boy
     * @param number number
     * @since 1.4.0
     */
    void updateInfo(boolean isBoy, Integer number);

}
