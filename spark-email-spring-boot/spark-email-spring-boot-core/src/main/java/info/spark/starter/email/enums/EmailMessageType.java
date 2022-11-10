package info.spark.starter.email.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.07 21:31
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum EmailMessageType {
    /** Simple email email content enum */
    SIMPLE_EMAIL(0, "简单文本邮件"),
    /** Html comtent email email content enum */
    HTML_COMTENT_EMAIL(1, "HTML 内容文本邮件"),
    /** Attach email email content enum */
    ATTACH_EMAIL(2, "含附件邮件");

    /** Value */
    Integer value;
    /** Desc */
    String desc;
}
