package info.spark.starter.doc.common.schema;

import com.google.common.collect.Sets;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import info.spark.starter.basic.Result;
import info.spark.starter.basic.util.StringPool;

import org.springframework.core.annotation.Order;

import java.lang.annotation.Annotation;
import java.util.Optional;

import javax.annotation.Resource;

import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.JacksonEnumTypeDeterminer;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.schema.ResolvedTypes;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.schema.Types;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spring.web.readers.operation.ResponseMessagesReader;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

/**
 * <p>Description: 最后一位执行，修改200状态码显示的 schema </p>
 *
 * @author wanghao
 * @version 1.7.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.05 22:21
 * @since 1.7.0
 */
@Order
public class SparkOperationResponseMessagePlugin implements OperationBuilderPlugin {
    /** Annotation */
    private final Class<? extends Annotation> filterAnnotationClass;
    /** Name extractor */
    @Resource
    private TypeNameExtractor nameExtractor;
    /** Type resolver */
    @Resource
    private TypeResolver typeResolver;

    /**
     * Spark operation models provider plugin
     *
     * @param filterAnnotationClass filterAnnotationClass
     * @since 1.7.0
     */
    public SparkOperationResponseMessagePlugin(Class<? extends Annotation> filterAnnotationClass) {
        this.filterAnnotationClass = filterAnnotationClass;
    }

    /**
     * Apply
     *
     * @param operationContext operation context
     * @since 1.7.0
     */
    @Override
    public void apply(OperationContext operationContext) {
        if (this.findAnnotationIfNotNull(operationContext)) {
            ResolvedType returnType = operationContext.getReturnType();
            // 兼容，如果方法返回值本身写的 Result 就不包装了
            if (Result.class.equals(returnType.getErasedType())) {
                return;
            }
            if (Types.isVoid(returnType)) {
                returnType = this.typeResolver.resolve(Void.class);
            }
            ResolvedType packageType = operationContext.alternateFor(this.typeResolver.resolve(Result.class, returnType));
            ModelContext modelContext = ModelContext.returnValue(StringPool.EMPTY,
                                                                 operationContext.getGroupName(),
                                                                 packageType,
                                                                 Optional.empty(),
                                                                 operationContext.getDocumentationType(),
                                                                 operationContext.getAlternateTypeProvider(),
                                                                 operationContext.getGenericsNamingStrategy(),
                                                                 operationContext.getIgnorableParameterTypes());

            int httpStatusCode = ResponseMessagesReader.httpStatusCode(operationContext);
            String message = ResponseMessagesReader.message(operationContext);
            ModelReference modelRef =
                ResolvedTypes.modelRefFactory(modelContext, new JacksonEnumTypeDeterminer(), this.nameExtractor).apply(packageType);
            ResponseMessage built = new ResponseMessageBuilder()
                .code(httpStatusCode)
                .message(message)
                .responseModel(modelRef)
                .build();

            operationContext.operationBuilder().responseMessages(Sets.newHashSet(built));
        }
    }

    /**
     * Find annotation if not null
     *
     * @param context context
     * @return the boolean
     * @since 1.7.0
     */
    public boolean findAnnotationIfNotNull(OperationContext context) {
        if (null == this.filterAnnotationClass) {
            return true;
        }
        return context.findAnnotation(this.filterAnnotationClass).isPresent();
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
