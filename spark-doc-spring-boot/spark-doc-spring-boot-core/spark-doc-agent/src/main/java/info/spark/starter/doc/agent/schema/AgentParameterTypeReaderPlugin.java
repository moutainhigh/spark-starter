package info.spark.starter.doc.agent.schema;

import info.spark.agent.annotation.ApiServiceMethod;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;

import java.util.Optional;

import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;

/**
 * <p>Description: 重新为 agent 参数列表，设置 dataType，type影响渲染 </p>
 *
 * @author wanghao
 * @version 1.7.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.04 15:35
 * @since 1.7.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class AgentParameterTypeReaderPlugin implements ParameterBuilderPlugin {

    /**
     * Apply
     *
     * @param context context
     * @since 1.7.0
     */
    @Override
    public void apply(ParameterContext context) {
        context.parameterBuilder().parameterType(this.resetSetAgentParamType(context));
    }

    /**
     * Gets parameter *
     *
     * @param context context
     * @return the parameter
     * @since 1.7.0
     */
    public String resetSetAgentParamType(ParameterContext context) {
        Optional<ApiServiceMethod> annotation = context.getOperationContext().findAnnotation(ApiServiceMethod.class);
        if (annotation.isPresent()) {
            // 这里没有进行基础类型的判断，agent 请求类型都为body，方便doc调试，页面UI展示的数据类型字段为字段类型
            HttpMethod httpMethod = context.getOperationContext().httpMethod();
            if (HttpMethod.GET.equals(httpMethod)) {
                return "path";
            }
            return "body";
        }
        return null;
    }


    /**
     * Supports
     *
     * @param delimiter delimiter
     * @return the boolean
     * @since 1.7.0
     */
    @Override
    public boolean supports(@NotNull DocumentationType delimiter) {
        return delimiter == DocumentationType.SWAGGER_2;
    }

}
