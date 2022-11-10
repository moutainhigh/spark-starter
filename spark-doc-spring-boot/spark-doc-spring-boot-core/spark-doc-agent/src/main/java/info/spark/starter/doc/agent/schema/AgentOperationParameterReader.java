package info.spark.starter.doc.agent.schema;

import com.fasterxml.classmate.ResolvedType;
import info.spark.agent.annotation.ApiServiceMethod;
import info.spark.agent.entity.ApiExtend;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;
import springfox.documentation.spring.web.readers.operation.OperationParameterReader;
import springfox.documentation.spring.web.readers.parameter.ExpansionContext;
import springfox.documentation.spring.web.readers.parameter.ModelAttributeParameterExpander;

import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.schema.Collections.isContainerType;
import static springfox.documentation.schema.Maps.isMapType;
import static springfox.documentation.schema.Types.isBaseType;
import static springfox.documentation.schema.Types.typeNameFor;

/**
 * <p>Description: 用于 aop 拦截 springfox中的 {@link OperationParameterReader} plugin,
 * 因为没有找到好的扩展点，因此aop直接替换为本类 </p>
 *
 * @author wanghao
 * @version 1.7.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.05 11:47
 * @since 1.7.0
 */
public class AgentOperationParameterReader {
    /** Expander */
    @Resource
    private ModelAttributeParameterExpander expander;
    /** Enum type determiner */
    @Resource
    private EnumTypeDeterminer enumTypeDeterminer;
    /** Plugins manager */
    @Resource
    private DocumentationPluginsManager pluginsManager;

    /**
     * Apply
     *
     * @param context context
     * @since 1.7.0
     */
    public void apply(OperationContext context) {
        context.operationBuilder().parameters(context.getGlobalOperationParameters());
        context.operationBuilder().parameters(this.readParameters(context));
    }

    /**
     * Read parameters
     *
     * @param context context
     * @return the list
     * @since 1.7.0
     */
    private List<Parameter> readParameters(OperationContext context) {
        List<ResolvedMethodParameter> methodParameters = context.getParameters();
        List<Parameter> parameters = newArrayList();

        for (ResolvedMethodParameter methodParameter : methodParameters) {
            ResolvedType alternate = context.alternateFor(methodParameter.getParameterType());
            if (!this.shouldIgnore(methodParameter, alternate, context.getIgnorableParameterTypes())) {

                ParameterContext parameterContext = new ParameterContext(methodParameter,
                                                                         new ParameterBuilder(),
                                                                         context.getDocumentationContext(),
                                                                         context.getGenericsNamingStrategy(),
                                                                         context);

                if (this.shouldExpand(methodParameter, alternate) && !context.findAnnotation(ApiServiceMethod.class).isPresent()) {
                    parameters.addAll(
                        this.expander.expand(
                            new ExpansionContext("", alternate, context)));
                } else {
                    parameters.add(this.pluginsManager.parameter(parameterContext));
                }
            }
        }
        return parameters.stream().filter(((Predicate<Parameter>) Parameter::isHidden).negate()).collect(Collectors.toList());
    }

    /**
     * Should ignore
     *
     * @param parameter             parameter
     * @param resolvedParameterType resolved parameter type
     * @param ignorableParamTypes   ignorable param types
     * @return the boolean
     * @since 1.7.0
     */
    private boolean shouldIgnore(
        ResolvedMethodParameter parameter,
        ResolvedType resolvedParameterType,
        @SuppressWarnings("rawtypes") Set<Class> ignorableParamTypes) {

        // 扩展较小，不必弄成接口实现的方式，多了可以重狗
        // 扩展: apiExtend 参数如果有，直接忽略，不生成到doc
        if (parameter.getParameterType().isInstanceOf(ApiExtend.class)) {
            return true;
        }
        // 扩展: 如果 agent 的body参数，和文档相差很大，可以忽略，自己用swagger注解写 @ApiImplicitParams
        if (parameter.hasParameterAnnotation(ApiIgnore.class)) {
            return true;
        }
        if (ignorableParamTypes.contains(resolvedParameterType.getErasedType())) {
            return true;
        }
        return (int) ignorableParamTypes.stream()
            .filter(Annotation.class::isAssignableFrom)
            .filter(parameter::hasParameterAnnotation).count() > 0;

    }

    /**
     * Should expand
     *
     * @param parameter         parameter
     * @param resolvedParamType resolved param type
     * @return the boolean
     * @since 1.7.0
     */
    private boolean shouldExpand(ResolvedMethodParameter parameter, ResolvedType resolvedParamType) {
        return !parameter.hasParameterAnnotation(RequestBody.class)
               && !parameter.hasParameterAnnotation(RequestPart.class)
               && !parameter.hasParameterAnnotation(RequestParam.class)
               && !parameter.hasParameterAnnotation(PathVariable.class)
               && !isBaseType(typeNameFor(resolvedParamType.getErasedType()))
               && !this.enumTypeDeterminer.isEnum(resolvedParamType.getErasedType())
               && !isContainerType(resolvedParamType)
               && !isMapType(resolvedParamType);

    }

}
