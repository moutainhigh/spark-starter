package info.spark.starter.doc.swagger.parameter;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.Optional;

import javax.validation.constraints.NotBlank;

import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ExpandedParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.15 21:38
 * @since 1.0.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExpandedParameterNotBlankAnnotationPlugin implements ExpandedParameterBuilderPlugin {
    /** LOG */
    private static final Logger LOG = LoggerFactory.getLogger(ExpandedParameterNotBlankAnnotationPlugin.class);

    /**
     * Expanded parameter min max annotation plugin
     *
     * @since 1.0.0
     */
    @Contract(pure = true)
    public ExpandedParameterNotBlankAnnotationPlugin() {
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
    public void apply(@NotNull ParameterExpansionContext context) {
        Optional<NotBlank> notBlank = context.findAnnotation(NotBlank.class);
        if (notBlank.isPresent()) {
            LOG.debug("Setting parameter to required because of @NotBlank attribute");
            context.getParameterBuilder().required(true);
        }

    }
}
