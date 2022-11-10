package info.spark.starter.common.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * <p>Description:  </p>
 *
 * @param <T> parameter
 * @param <M> parameter
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.03.18 11:37
 * @since 1.8.0
 */
public abstract class BaseExtendChannelPO<T extends Serializable, M extends Model<M>> extends BaseExtendPO<T, M>
    implements Channel {

    /** serialVersionUID */
    private static final long serialVersionUID = -2716066043048753401L;
    /** Tenant id */
    @TableField(value = TENANT_ID, fill = FieldFill.INSERT)
    private Long tenantId;

    /** Client id */
    @TableField(value = CLIENT_ID, fill = FieldFill.INSERT)
    private String clientId;

    /**
     * Gets tenant id *
     *
     * @return the tenant id
     * @since 1.8.0
     */
    @Override
    public Long getTenantId() {
        return this.tenantId;
    }

    /**
     * Gets client id *
     *
     * @return the client id
     * @since 1.8.0
     */
    @Override
    public String getClientId() {
        return this.clientId;
    }

    /**
     * Sets tenant id *
     *
     * @param tenantId tenant id
     * @return the tenant id
     * @since 1.8.0
     */
    @SuppressWarnings("unchecked")
    public M setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return (M) this;
    }

    /**
     * Sets client id *
     *
     * @param clientId client id
     * @return the client id
     * @since 1.8.0
     */
    @SuppressWarnings("unchecked")
    public M setClientId(String clientId) {
        this.clientId = clientId;
        return (M) this;
    }
}
