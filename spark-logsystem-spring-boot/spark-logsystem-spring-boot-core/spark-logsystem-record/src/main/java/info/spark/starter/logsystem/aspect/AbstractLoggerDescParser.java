package info.spark.starter.logsystem.aspect;

import org.jetbrains.annotations.Contract;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 日志描述解析(支持SpEL表达式)</p>
 *
 * @author sujian
 * @version 1.0.0
 * @email "mailto:sujian@gmail.com"
 * @date 2020.05.26 11:31
 * @since 1.0.0
 */
@Slf4j
public abstract class AbstractLoggerDescParser {

    /** SpEL表达式解析器 */
    private static final ExpressionParser SPEL_EXPRESSION_PARSER = new SpelExpressionParser();

    /** 参数名发现器 */
    private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new LocalVariableTableParameterNameDiscoverer();

    /** evaluationContext */
    private static final StandardEvaluationContext EVALUATION_CONTEXT = new StandardEvaluationContext();

    static {
        try {
            EVALUATION_CONTEXT.registerFunction("dateFormat", AbstractLoggerDescParser.class.getDeclaredMethod("dateFormat", Date.class));
        } catch (NoSuchMethodException ignored) {
        }
    }

    /**
     * 解析描述(支持SpEL表达式)
     *
     * @param method method
     * @param args   args
     * @param spel   spel
     * @return the string
     * @since 1.5.0
     */
    protected String parseDescription(Method method, Object[] args, String spel) {
        // 从方法中获取参数名, 并把参数和值添加到计算上下文中
        String[] params = PARAMETER_NAME_DISCOVERER.getParameterNames(method);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                EVALUATION_CONTEXT.setVariable(params[i], args[i]);
            }
        }

        Expression expression = SPEL_EXPRESSION_PARSER.parseExpression(spel, new TemplateParserContext());
        return expression.getValue(EVALUATION_CONTEXT, String.class);
    }

    /**
     * 格式化为 yyyy-MM-dd HH:mm:ss
     *
     * @param date date
     * @return string string
     * @since 1.5.0
     */
    @Contract("null -> null")
    private static String dateFormat(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }
}
