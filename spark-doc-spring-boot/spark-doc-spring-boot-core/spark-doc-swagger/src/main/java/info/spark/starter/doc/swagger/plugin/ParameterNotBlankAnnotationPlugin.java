package info.spark.starter.doc.swagger.plugin;

import org.jetbrains.annotations.Contract;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.Optional;

import javax.validation.constraints.NotBlank;

import lombok.extern.slf4j.Slf4j;
import springfox.bean.validators.plugins.Validators;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.15 21:31
 * @since 1.0.0
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ParameterNotBlankAnnotationPlugin implements ParameterBuilderPlugin {

    /**
     * Not blank annotation plugin
     *
     * @since 1.0.0
     */
    @Contract(pure = true)
    public ParameterNotBlankAnnotationPlugin() {
    }

    /**
     * Supports boolean
     *
     * @param delimiter delimiter
     * @return the boolean
     * @since 1.0.0
     */
    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }

    /**
     * Apply *
     *
     * @param context context
     * @since 1.0.0
     */
    @Override
    public void apply(ParameterContext context) {
        Optional<NotBlank> notBlank = Validators.annotationFromParameter(context, NotBlank.class);
        if (notBlank.isPresent()) {
            log.debug("@NotNull present: setting parameter as required");
            context.parameterBuilder().required(true);
        }

    }
}
