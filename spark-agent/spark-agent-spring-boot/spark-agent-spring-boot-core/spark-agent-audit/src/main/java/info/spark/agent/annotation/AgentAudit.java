package info.spark.agent.annotation;

import info.spark.agent.enums.ActionType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description: agent service 项目审计日志注解 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 04:54
 * @since 1.6.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AgentAudit {

    /**
     * 审计日志描述
     *
     * @return {String}
     * @since 1.6.0
     */
    String value();

    /**
     * Action
     *
     * @return the action type
     * @since 1.6.0
     */
    ActionType action();
}
