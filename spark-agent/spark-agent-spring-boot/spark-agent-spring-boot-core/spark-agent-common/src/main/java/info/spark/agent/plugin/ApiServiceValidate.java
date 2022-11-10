package info.spark.agent.plugin;

import info.spark.agent.validation.ValidateMessage;

import java.util.List;

/**
 * <p>Description: 参数验证接口 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.31 11:36
 * @since 1.0.0
 */
public interface ApiServiceValidate extends Plugin {
    /**
     * Validate list
     *
     * @param o      o
     * @param groups groups
     * @return the list
     * @since 1.0.0
     */
    List<ValidateMessage> validate(Object o, Class<?>... groups);
}
