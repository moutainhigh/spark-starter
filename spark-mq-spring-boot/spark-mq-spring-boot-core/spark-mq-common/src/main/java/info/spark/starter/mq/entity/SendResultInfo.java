package info.spark.starter.mq.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Description: 消息发送的一些信息 </p>
 *
 * @author wanghao
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.12.11 13:52
 * @since 1.7.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendResultInfo {

    /** Queue id */
    private Integer queueId;
    /** Offset */
    private Long offset;
}
