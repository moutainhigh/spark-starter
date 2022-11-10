package info.spark.start.mqtt.core.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * <p>Description:  </p>
 *
 * @author zhonghaijun
 * @version 1.0.0
 * @email "mailto:zhonghaijun@gmail.com"
 * @date 2021.12.16 15:11
 * @since 2.1.0
 */
@Data
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MqttClientMessage implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1128847949412401730L;

    /** Topic */
    private String topic;

    /** Client id */
    private String clientId;

    /** Payload */
    private String payload;

    /** Qos */
    private Integer qos;
}
