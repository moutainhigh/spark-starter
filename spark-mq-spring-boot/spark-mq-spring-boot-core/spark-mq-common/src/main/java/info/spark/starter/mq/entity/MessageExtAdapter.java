package info.spark.starter.mq.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Description: 消息元数据 </p>
 *
 * @author wanghao
 * @version 1.8.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.05.25 10:12
 * @since 1.8.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageExtAdapter implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = -5857818828452063717L;
    /** Msg id */
    private String msgId;
    /** Queue id */
    private int queueId;
    /** Store size */
    private int storeSize;
    /** Queue offset */
    private long queueOffset;
    /** Reconsume times */
    private int reconsumeTimes;
    /** Max reconsume times */
    private int maxReconsumeTimes;
}
