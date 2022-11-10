package info.spark.starter.mq.autoconfigure.common;

import info.spark.starter.util.StringUtils;
import info.spark.starter.mq.RoleType;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.21 19:37
 * @since 1.5.0
 */
@Slf4j
public abstract class ProviderPropCondition extends RoleTypeCondition {

    /** Condition prop */
    private final String[] conditionProp;

    /**
     * Captcha condition
     *
     * @param roleType      provider type
     * @param conditionProp condition prop
     * @since 1.5.0
     */
    protected ProviderPropCondition(RoleType roleType, String... conditionProp) {
        super(roleType);
        this.conditionProp = conditionProp;
    }

    /**
     * Gets match outcome *
     *
     * @param conditionContext      condition context
     * @param annotatedTypeMetadata annotated type metadata
     * @return the match outcome
     * @since 1.5.0
     */
    @Override
    public ConditionOutcome getMatchOutcome(@NotNull ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        // 调用父类校验 RoleType
        ConditionOutcome matchOutcome = super.getMatchOutcome(conditionContext, annotatedTypeMetadata);
        if (matchOutcome.isMatch()) {
            // 判断属性值
            Environment environment = conditionContext.getEnvironment();

            if (null != this.conditionProp) {
                for (String s : this.conditionProp) {
                    if (StringUtils.isBlank(environment.getProperty(s))) {
                        return ConditionOutcome.noMatch("");
                    }
                }
            }

        }
        return matchOutcome;
    }
}
