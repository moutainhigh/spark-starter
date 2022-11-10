package info.spark.starter.doc.common.schema;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import info.spark.starter.basic.Result;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.annotation.Annotation;

import javax.annotation.Resource;

import springfox.documentation.schema.Types;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationModelsProviderPlugin;
import springfox.documentation.spi.service.contexts.RequestMappingContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

/**
 * <p>Description: 包装返回值为 Result<?> </p>
 *
 * @author wanghao
 * @version 1.7.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.04 16:08
 * @since 1.7.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class SparkOperationModelsProviderPlugin implements OperationModelsProviderPlugin {
    /** Annotation */
    private final Class<? extends Annotation> filterAnnotationClass;
    /** Type resolver */
    @Resource
    private TypeResolver typeResolver;

    /**
     * Spark operation models provider plugin
     *
     * @param filterAnnotationClass filterAnnotationClass
     * @since 1.7.0
     */
    public SparkOperationModelsProviderPlugin(Class<? extends Annotation> filterAnnotationClass) {
        this.filterAnnotationClass = filterAnnotationClass;
    }

    /**
     * Apply
     *
     * @param context context
     * @since 1.7.0
     */
    @Override
    public void apply(RequestMappingContext context) {
        if (this.findAnnotationIfNotNull(context)) {
            this.addInputParams(context);
            this.addReturnType(context);
        }
    }

    /**
     * Find annotation if not null
     *
     * @param context context
     * @return the boolean
     * @since 1.7.0
     */
    public boolean findAnnotationIfNotNull(RequestMappingContext context) {
        if (null == this.filterAnnotationClass) {
            return true;
        }
        return context.findAnnotation(filterAnnotationClass).isPresent();
    }

    /**
     * Add return type
     *
     * @param context context
     * @since 1.7.0
     */
    private void addReturnType(RequestMappingContext context) {
        ResolvedType returnType = context.getReturnType();
        // 兼容，如果方法返回值本身写的 Result 就不包装了
        if (Result.class.equals(returnType.getErasedType())) {
            return;
        }
        if (Types.isVoid(returnType)) {
            returnType = this.typeResolver.resolve(Void.class);
        }
        ResolvedType packageType = context.alternateFor(this.typeResolver.resolve(Result.class, returnType));
        context.operationModelsBuilder().addReturn(packageType);
    }

    /**
     * Add input params
     *
     * @param context context
     * @since 1.7.0
     */
    private void addInputParams(RequestMappingContext context) {
        for (ResolvedMethodParameter parameter : context.getParameters()) {
            ResolvedType modelType = context.alternateFor(parameter.getParameterType());
            context.operationModelsBuilder().addInputParam(modelType);
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
    public boolean supports(DocumentationType delimiter) {
        return SwaggerPluginSupport.pluginDoesApply(delimiter);
    }
}
