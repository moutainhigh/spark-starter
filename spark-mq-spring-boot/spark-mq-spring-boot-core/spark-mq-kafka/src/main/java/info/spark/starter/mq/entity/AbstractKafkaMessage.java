package info.spark.starter.mq.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;


/**
 * <p>Description: </p>
 *
 * @author wanghao
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.12.01 19:15
 * @since 1.0.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractKafkaMessage extends AbstractMessage {
    /** serialVersionUID */
    private static final long serialVersionUID = 6697518367777426587L;
    /** Topic */
    private String topic;
    /** Key: 当有多个broker时, 会根据该key指定消息存储到哪个broker上 */
    private String key;
}
