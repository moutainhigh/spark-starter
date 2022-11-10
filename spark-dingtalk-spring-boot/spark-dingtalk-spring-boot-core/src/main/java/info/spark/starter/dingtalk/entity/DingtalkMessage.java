package info.spark.starter.dingtalk.entity;

import info.spark.starter.dingtalk.enums.DingtalkMessageType;
import info.spark.starter.notify.AbstractMessage;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * <p>Description: </p>
 *
 * @param <T> parameter
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.12.05 00:27
 * @since 2.1.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class DingtalkMessage<T extends Serializable> extends AbstractMessage<T> {
    /** serialVersionUID */
    private static final long serialVersionUID = 6297518434607557350L;

    /**
     * Gets message type *
     *
     * @return the message type
     * @since 2.1.0
     */
    public abstract DingtalkMessageType getMessageType();
}
