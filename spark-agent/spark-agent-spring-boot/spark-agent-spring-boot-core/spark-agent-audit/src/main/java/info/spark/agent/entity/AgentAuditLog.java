package info.spark.agent.entity;

import info.spark.agent.enums.ActionType;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>Description: 实体类 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 05:28
 * @since 1.6.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class AgentAuditLog extends AbstractLog implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** 审计日志标题 */
    private String title;
    /** Action type */
    private ActionType actionType;
}
