package info.spark.starter.doc.agent.schema;

import com.google.common.collect.Lists;

import com.fasterxml.classmate.TypeResolver;
import info.spark.agent.constant.AgentConstant;
import info.spark.starter.util.ReflectionUtils;
import info.spark.starter.doc.agent.constant.AgentDocConstant;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.List;
import java.util.Set;

import springfox.documentation.service.Parameter;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spring.web.readers.operation.AbstractOperationParameterRequestConditionReader;
import springfox.documentation.spring.wrapper.NameValueExpression;

/**
 * <p>Description:  </p>
 *
 * @author wanghao
 * @version 1.7.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.08 10:05
 * @since 1.7.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class AgentParameterHeadersConditionReader extends AbstractOperationParameterRequestConditionReader {

    /** Agent headers */
    private final List<String> agentHeaders = Lists.newArrayList(AgentConstant.X_AGENT_APPID,
                                                                 AgentConstant.X_AGENT_NONCE,
                                                                 AgentConstant.X_AGENT_SIGNATURE,
                                                                 AgentConstant.X_AGENT_TENANTID,
                                                                 AgentConstant.X_AGENT_SIGNATURE_HEADERS);

    /**
     * Agent parameter headers condition reader
     *
     * @param resolver resolver
     * @since 1.7.0
     */
    public AgentParameterHeadersConditionReader(TypeResolver resolver) {
        super(resolver);
    }

    /**
     * Apply
     *
     * @param context context
     * @since 1.7.0
     */
    @Override
    public void apply(OperationContext context) {
        Set<NameValueExpression<String>> headers = context.headers();
        List<Parameter> parameters = this.getParameters(headers, "header");
        for (Parameter parameter : parameters) {
            if (this.agentHeaders.contains(parameter.getName())) {
                ReflectionUtils.setFieldValue(parameter, AgentDocConstant.FIELD_REQUIRED, false);
            }
            ReflectionUtils.setFieldValue(parameter, AgentDocConstant.FIELD_DESCRIPTION, "agent请求头");
            ReflectionUtils.setFieldValue(parameter, AgentDocConstant.FIELD_ORDER, -1);
        }
        context.operationBuilder().parameters(parameters);
    }

}
