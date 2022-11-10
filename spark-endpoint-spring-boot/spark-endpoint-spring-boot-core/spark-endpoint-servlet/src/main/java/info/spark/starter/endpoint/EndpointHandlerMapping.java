package info.spark.starter.endpoint;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 自动将被 @Endpoint 标识的类注册为一个 controller  </p>
 * http://monkeywie.cn/2020/06/22/custom-springmvc-requestmappinghandlermapping
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.30 03:15
 * @since 1.0.0
 */
@Slf4j
public abstract class EndpointHandlerMapping extends RequestMappingHandlerMapping {

    /** Mappings */
    private final Map<String, String> mappings = Maps.newHashMap();
    /** Paths */
    private final Set<String> paths = Sets.newHashSet();

    /**
     * Gets path *
     *
     * @param defaultPath default path
     * @return the mapping from default endpoint paths to custom ones (or the default if no customization is known)
     * @since 1.0.0
     */
    private String getPath(String defaultPath) {
        String result = defaultPath;
        if (this.mappings.containsKey(defaultPath)) {
            result = this.mappings.get(defaultPath);
        }
        return result;
    }

    /**
     * Agent client endpoint handler mapping
     *
     * @since 1.0.0
     */
    protected EndpointHandlerMapping() {
        // Make sure user-supplied mappings take precedence by default (except the resource mapping)
        this.setOrder(LOWEST_PRECEDENCE - 2);
    }

    /**
     * Detects &#64;FrameworkEndpoint annotations in handler beans.
     *
     * @param beanType bean type
     * @return the boolean
     * @see RequestMappingHandlerMapping#isHandler RequestMappingHandlerMapping#isHandler
     * @since 1.0.0
     */
    @Override
    protected boolean isHandler(@NotNull Class<?> beanType) {
        return AnnotationUtils.findAnnotation(beanType, Endpoint.class) != null;
    }

    /**
     * Gets mapping for method *
     *
     * @param method      method
     * @param handlerType handler type
     * @return the mapping for method
     * @since 1.0.0
     */
    @Override
    protected RequestMappingInfo getMappingForMethod(@NotNull Method method, @NotNull Class<?> handlerType) {

        RequestMappingInfo defaultMapping = super.getMappingForMethod(method, handlerType);
        if (defaultMapping == null) {
            return null;
        }
        log.trace("添加自定义 endpoint: [{}], handler: [{}]", defaultMapping, handlerType);

        Set<String> defaultPatterns = defaultMapping.getPatternsCondition().getPatterns();
        String[] patterns = new String[defaultPatterns.size()];

        int i = 0;
        for (String pattern : defaultPatterns) {
            patterns[i] = this.getPath(pattern);
            this.paths.add(pattern);
            i++;
        }
        PatternsRequestCondition patternsInfo = new PatternsRequestCondition(patterns);

        return new RequestMappingInfo(patternsInfo,
                                      defaultMapping.getMethodsCondition(),
                                      defaultMapping.getParamsCondition(),
                                      defaultMapping.getHeadersCondition(),
                                      defaultMapping.getConsumesCondition(),
                                      defaultMapping.getProducesCondition(),
                                      defaultMapping.getCustomCondition());

    }

}
