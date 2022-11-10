package info.spark.starter.mq.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * <p>Description: RocketMQ 发送或者是消费都必须通过实体封装消息, 且必须继承该实体 </p>
 *
 * @author zhubo
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.11.12 14:11
 * @since 1.7.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractRocketMessage extends AbstractMessage {
    private static final long serialVersionUID = -4141723799837442568L;
    /** Topic */
    private String topic;
    /** Tag: 不指定时会默认设置为 * */
    private String tag;
    /** Key: 当有多个broker时, 会根据该key指定消息存储到哪个broker上 */
    private String key;
}
