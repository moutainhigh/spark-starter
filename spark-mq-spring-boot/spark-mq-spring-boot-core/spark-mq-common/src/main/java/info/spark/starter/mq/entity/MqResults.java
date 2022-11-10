package info.spark.starter.mq.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Description: mq消息发送位置信息 </p>
 *
 * @author wanghao
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.12.01 18:19
 * @since 1.7.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MqResults implements Serializable {
    /** serialVersionUID */
    private static final long serialVersionUID = -1001474838978772753L;
    /** Send result info */
    private SendResultInfo sendResultInfo;
    /** result */
    private Boolean result;
}
