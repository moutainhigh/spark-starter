package info.spark.starter.dingtalk.entity;

import info.spark.starter.dingtalk.enums.DingtalkMessageType;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.12.05 00:27
 * @since 2.1.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ActionCardMessage extends DingtalkMessage<String> {

    /** Content */
    private String content;
    /** Title */
    private String title;
    /** Buttons */
    private List<Button> buttons;

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2021.12.05 00:27
     * @since 2.1.0
     */
    @Data
    public static class Button {
        /** Content */
        private String content;
        /** Title */
        private String title;
    }

    /**
     * Get message type
     *
     * @return the dingtalk message type
     * @since 2.1.0
     */
    @Override
    public DingtalkMessageType getMessageType() {
        return DingtalkMessageType.ACTION_CARD;
    }
}
