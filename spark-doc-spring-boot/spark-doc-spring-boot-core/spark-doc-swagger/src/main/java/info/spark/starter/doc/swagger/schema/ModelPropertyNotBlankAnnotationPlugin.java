package info.spark.starter.doc.swagger.schema;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

import org.jetbrains.annotations.Contract;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.Optional;

import javax.validation.constraints.NotBlank;

import springfox.bean.validators.plugins.Validators;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.15 21:33
 * @since 1.0.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ModelPropertyNotBlankAnnotationPlugin implements ModelPropertyBuilderPlugin {
    /**
     * Not blank annotation plugin
     *
     * @since 1.0.0
     */
    @Contract(pure = true)
    public ModelPropertyNotBlankAnnotationPlugin() {
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
    public void apply(ModelPropertyContext context) {
        Optional<NotBlank> notBlank = this.extractAnnotation(context);
        if (notBlank.isPresent()) {
            context.getBuilder().required(notBlank.isPresent());
        }

    }

    /**
     * Extract annotation optional
     *
     * @param context context
     * @return the optional
     * @since 1.0.0
     */
    @VisibleForTesting
    private Optional<NotBlank> extractAnnotation(ModelPropertyContext context) {
        // springfox 2.10.x 后, guava -> java.util.Optional，保持原 guava or方法逻辑，先检查 or(T t), 为空 NPE
        Preconditions.checkNotNull(Validators.annotationFromField(context, NotBlank.class));
        Optional<NotBlank> notBlank = Validators.annotationFromBean(context, NotBlank.class);
        return notBlank.isPresent() ? notBlank : Validators.annotationFromField(context, NotBlank.class);
    }
}
