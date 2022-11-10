package info.spark.starter.mq.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Description: 分发实体 </p>
 *
 * @author wanghao
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.12.21 18:35
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Distribute implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = -2974936073919224518L;
    /** message */
    private String message;
    /** Receiver */
    private String receiver;
    /** Curr server */
    private String currServer;
    /** Message ext adapter */
    private MessageExtAdapter messageExtAdapter;
}
