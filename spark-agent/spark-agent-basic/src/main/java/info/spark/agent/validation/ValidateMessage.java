package info.spark.agent.validation;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Description: 参数验证错误消息 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.30 06:03
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateMessage implements Serializable {
    /** serialVersionUID */
    private static final long serialVersionUID = 7839929546268632449L;
    /** Property */
    private String property;
    /** Message */
    private String message;
    /** Code */
    private Integer code;
}
