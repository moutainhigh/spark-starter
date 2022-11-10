package info.spark.starter.mq.entity;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * <p>Description: 异步消息实体接口 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.14 15:48
 * @since 1.5.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
public abstract class AbstractMessage implements Serializable {
    /** serialVersionUID */
    private static final long serialVersionUID = -3330568032682623896L;
}
