package info.spark.starter.captcha.autoconfigure;

import info.spark.starter.captcha.entity.CaptchaConfig;
import info.spark.starter.util.CollectionUtils;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.21 19:37
 * @since 1.0.0
 */
public abstract class CaptchaCondition extends SpringBootCondition {

    /** Cache types */
    private final CaptchaConfig.CheckType checkType;

    /**
     * Captcha condition
     *
     * @param checkType check type
     * @since 1.0.0
     */
    CaptchaCondition(CaptchaConfig.CheckType checkType) {
        Objects.requireNonNull(checkType, "验证码动态检查类型不能为空");
        this.checkType = checkType;
    }

    /**
     * Gets match outcome *
     *
     * @param conditionContext      condition context
     * @param annotatedTypeMetadata annotated type metadata
     * @return the match outcome
     * @since 1.0.0
     */
    @Override
    public ConditionOutcome getMatchOutcome(@NotNull ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment environment = conditionContext.getEnvironment();
        // 使用 binder 来解析配置
        Binder binder = Binder.get(environment);

        List<CaptchaConfig.CheckType> checkTypes;
        try {
            checkTypes = binder.bind("spark.captcha.checks", Bindable.listOf(CaptchaConfig.CheckType.class)).get();
        } catch (NoSuchElementException e) {
            checkTypes = Collections.emptyList();
        }

        if (CollectionUtils.isNotEmpty(checkTypes) && checkTypes.stream().anyMatch(m -> m.equals(this.checkType))) {
            return ConditionOutcome.match();
        }
        return ConditionOutcome.noMatch("未匹配到验证码动态检查类型: " + this.checkType);
    }
}
