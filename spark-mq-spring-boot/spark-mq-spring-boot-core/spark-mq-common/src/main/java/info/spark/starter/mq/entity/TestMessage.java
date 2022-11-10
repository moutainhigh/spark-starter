package info.spark.starter.mq.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>Description: 消息中间件测试实体 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.14 15:52
 * @since 1.5.0
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TestMessage extends AbstractMessage {
    /** serialVersionUID */
    private static final long serialVersionUID = -7167352478597330591L;

    /** Id */
    private String id;
    /** Name */
    private String name;

}
