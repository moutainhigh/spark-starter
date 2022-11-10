package info.spark.starter.common.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * <p>Description: </p>
 * 1. 新增记录时, 不需要以下 4 个公共字段, 将根据注解自动生成;
 * 2. 更新记录时, 也不需设置 updateClientId, 会自动更新
 * 注意: 子类不能使用 builder 模式! 子类不能使用 builder 模式! 子类不能使用 builder 模式!
 *
 * @param <T> parameter
 * @param <M> parameter
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.22 22:49
 * @since 1.0.0
 */
public abstract class BaseWithChannelPO<T extends Serializable, M extends Model<M>> extends BaseWithTimePO<T, M> implements Channel {

    /** serialVersionUID */
    private static final long serialVersionUID = 9002400483320996799L;

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
