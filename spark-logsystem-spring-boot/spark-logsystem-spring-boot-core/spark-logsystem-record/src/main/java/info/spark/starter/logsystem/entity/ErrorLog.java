package info.spark.starter.logsystem.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>Description: 服务 异常 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 05:27
 * @since 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class ErrorLog extends AbstractLog {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    /** 堆栈信息 */
    private String stackTrace;
    /** 异常名 */
    private String exceptionName;
    /** 异常消息 */
    private String message;
    /** 文件名 */
    private String fileName;
    /** 代码行数 */
    private Integer lineNumber;

}
