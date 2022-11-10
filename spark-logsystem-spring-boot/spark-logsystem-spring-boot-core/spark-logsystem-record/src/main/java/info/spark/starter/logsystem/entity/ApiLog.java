package info.spark.starter.logsystem.entity;

import info.spark.starter.auth.CurrentUser;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>Description: 实体类 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 05:26
 * @since 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class ApiLog extends AbstractLog {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** 日志类型 */
    private String type;
    /** 日志标题 */
    private String title;
    /** User */
    private CurrentUser user;

}
