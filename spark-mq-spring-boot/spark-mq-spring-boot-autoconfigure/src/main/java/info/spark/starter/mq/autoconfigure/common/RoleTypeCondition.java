package info.spark.starter.mq.autoconfigure.common;

import info.spark.starter.common.util.JustOnceLogger;
import info.spark.starter.mq.RoleType;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.NoSuchElementException;
import java.util.Objects;

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
public abstract class RoleTypeCondition extends SpringBootCondition {

    /** Cache types */
    private final RoleType roleType;

    /**
     * Captcha condition
     *
     * @param roleType provider type
     * @since 1.5.0
     */
    protected RoleTypeCondition(RoleType roleType) {
        Objects.requireNonNull(roleType, "role 类型不能为空");
        this.roleType = roleType;
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
        Environment environment = conditionContext.getEnvironment();
        // 使用 binder 来解析配置
        Binder binder = Binder.get(environment);

        RoleType checkRoleType = null;
        try {
            checkRoleType = binder.bind("spark.mq.role", Bindable.of(RoleType.class)).get();
        } catch (NoSuchElementException e) {
            JustOnceLogger.warnOnce(RoleTypeCondition.class.getName(), "[spark.mq.role] 未配置");
        }
        if (null != checkRoleType) {
            if (checkRoleType.equals(this.roleType) || RoleType.BOTH.equals(checkRoleType)) {
                return ConditionOutcome.match();
            }
        }
        return ConditionOutcome.noMatch("");
    }
}
