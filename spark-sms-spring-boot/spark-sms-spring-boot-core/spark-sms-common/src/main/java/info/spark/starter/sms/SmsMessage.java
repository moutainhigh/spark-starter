package info.spark.starter.sms;

import info.spark.starter.notify.AbstractMessage;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * <p>Description:  </p>
 *
 * @param <ID> parameter
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.03.01 17:59
 * @since 1.0.0
 */
@ToString
@SuperBuilder
public abstract class SmsMessage<ID extends Serializable> extends AbstractMessage<ID> {

    /** serialVersionUID */
    private static final long serialVersionUID = -8714307620584406903L;
    /** 手机号码 */
    @Getter
    @Setter
    private List<String> phone;
    /** 发送内容 */
    @Getter
    @Setter
    private String content;
}
