package info.spark.starter.common.base;

/**
 * <p>Description: 数据渠道标识 </p>
 *
 * @author wanghao
 * @version 1.8.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.03.08 14:47
 * @since 1.8.0
 */
public interface Channel {

    /** TENANT_ID */
    String TENANT_ID = "tenant_id";
    /** CLIENT_ID */
    String CLIENT_ID = "client_id";

    /**
     * Gets tenant id *
     *
     * @return the tenant id
     * @since 1.8.0
     */
    Long getTenantId();

    /**
     * Gets client id *
     *
     * @return the client id
     * @since 1.8.0
     */
    String getClientId();

}
