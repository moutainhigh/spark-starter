package info.spark.starter.doc.agent.schema;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import info.spark.agent.annotation.ApiServiceMethod;

import org.springframework.core.annotation.Order;
import org.springframework.validation.annotation.Validated;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import springfox.bean.validators.plugins.Validators;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import static springfox.documentation.swagger.common.SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER;

/**
 * <p>Description: 扩展参数，JSR 303, ApiModelProperty是否必须填写字段, 其他 </p>
 *
 * @author wanghao
 * @version 1.7.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.04 18:43
 * @since 1.7.0
 */
@Order(SWAGGER_PLUGIN_ORDER + 10)
public class AgentParameterRequiredBuilderPlugin implements ParameterBuilderPlugin {

    /** Param annotations */
    private final List<Class<? extends Annotation>> paramAnnotations = Lists.newArrayList(NotNull.class,
                                                                                          NotBlank.class,
                                                                                          Valid.class,
                                                                                          Validated.class);

    /**
     * Apply
     *
     * @param context context
     * @since 1.7.0
     */
    @Override
    public void apply(ParameterContext context) {
        this.apiModelRequiredFromJsr303(context);
    }

    /**
     * Jsr form model
     *
     * @param context context
     * @since 1.7.0
     */
    private void apiModelRequiredFromJsr303(ParameterContext context) {
        Set<Boolean> optional = Sets.newHashSet();
        Optional<ApiServiceMethod> annotation = context.getOperationContext().findAnnotation(ApiServiceMethod.class);
        if (annotation.isPresent()) {
            for (Annotation param : context.resolvedMethodParameter().getAnnotations()) {
                if (this.paramAnnotations.contains(param.getClass())) {
                    context.parameterBuilder().required(Boolean.TRUE);
                    return;
                }
            }
        }
        Optional<NotNull> notNullOptional = Validators.annotationFromParameter(context, NotNull.class);
        if (notNullOptional.isPresent()) {
            optional.add(Boolean.TRUE);
        }
        Optional<NotBlank> notBlankOptional = Validators.annotationFromParameter(context, NotBlank.class);
        if (notBlankOptional.isPresent()) {
            optional.add(Boolean.TRUE);
        }
        Optional<Valid> validOptional = Validators.annotationFromParameter(context, Valid.class);
        if (validOptional.isPresent()) {
            optional.add(Boolean.TRUE);
        }
        Optional<Validated> validatedOptional = Validators.annotationFromParameter(context, Validated.class);
        if (validatedOptional.isPresent()) {
            optional.add(Boolean.TRUE);
        }
        if (optional.contains(Boolean.TRUE)) {
            context.parameterBuilder().required(Boolean.TRUE);
        }
    }

    /**
     * Supports
     *
     * @param delimiter delimiter
     * @return the boolean
     * @since 1.7.0
     */
    @Override
    public boolean supports(@org.jetbrains.annotations.NotNull DocumentationType delimiter) {
        return SwaggerPluginSupport.pluginDoesApply(delimiter);
    }

}
