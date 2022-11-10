package info.spark.agent.entity;

import info.spark.agent.validation.ValidateMessage;
import info.spark.starter.basic.context.ExpandIds;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Description: 扩展字段 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.30 06:02
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiExtend implements Serializable {
    /** serialVersionUID */
    private static final long serialVersionUID = 2241582346670499377L;
    /** 参数验证信息 */
    private List<ValidateMessage> messages;
    /** Request */
    @NotNull
    private ApiServiceRequest request;
    /** Header */
    @NotNull
    private ApiServiceHeader header;
    /** 复合优先继承 */
    @NotNull
    private ExpandIds expandId;

    /**
     * Get tenant id
     *
     * @return the long
     * @since 1.8.0
     */
    public Optional<Long> getTenantId() {
        return this.expandId.getTenantId();
    }

    /**
     * Get client id
     *
     * @return the string
     * @since 1.8.0
     */
    public Optional<String> getClientId() {
        return this.expandId.getClientId();
    }
}
