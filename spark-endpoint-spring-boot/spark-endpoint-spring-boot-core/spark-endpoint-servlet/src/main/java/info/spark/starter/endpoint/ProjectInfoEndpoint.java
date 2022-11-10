package info.spark.starter.endpoint;

import com.google.common.collect.Lists;

import info.spark.starter.basic.Result;
import info.spark.starter.basic.util.SecurityUtils;
import info.spark.starter.util.core.api.R;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Builder;
import lombok.Data;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.10 11:12
 * @since 1.0.0
 */
@RestController
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Api(tags = "应用接口信息")
public class ProjectInfoEndpoint {
    /** Application context */
    @Resource
    private WebApplicationContext applicationContext;
    /** Path matcher */
    private final PathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 获取所有接口
     *
     * @return the result
     * @since 1.0.0
     */
    @ApiOperation("获取应用所有接口")
    @GetMapping(value = "/request-urls")
    public Result<List<RequestToMethodItem>> index() {
        List<RequestToMethodItem> list = Lists.newArrayList();
        RequestMappingHandlerMapping requestMappingHandlerMapping = this.applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> requestMappingInfoHandlerMethodEntry : handlerMethods.entrySet()) {
            RequestMappingInfo requestMappingInfo = requestMappingInfoHandlerMethodEntry.getKey();
            HandlerMethod mappingInfoValue = requestMappingInfoHandlerMethodEntry.getValue();
            RequestMethodsRequestCondition methodCondition = requestMappingInfo.getMethodsCondition();
            String requestType = methodCondition.getMethods().stream().map(String::valueOf).collect(Collectors.joining(","));
            PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();
            Class<?>[] methodParamTypes = mappingInfoValue.getMethod().getParameterTypes();
            String requestUrl = patternsCondition.getPatterns().stream().map(String::valueOf).collect(Collectors.joining(","));

            boolean allMatch = SecurityUtils.DEFAULT_SKIP_URL.stream().allMatch(pattern -> this.pathMatcher.matchStart(pattern,
                                                                                                                       requestUrl));
            if (allMatch) {
                continue;
            }

            String controllerName = mappingInfoValue.getBeanType().toString();
            String requestMethodName = mappingInfoValue.getMethod().getName();
            RequestToMethodItem item = RequestToMethodItem.builder()
                .requestType(requestType)
                .requestUrl(requestUrl)
                .controllerName(controllerName)
                .requestMethodName(requestMethodName)
                .methodParamTypes(methodParamTypes).build();
            list.add(item);
        }
        return R.succeed(list);
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.03.10 11:42
     * @since 1.0.0
     */
    @Data
    @Builder
    private static class RequestToMethodItem {
        /** Request type */
        private String requestType;
        /** Request url */
        private String requestUrl;
        /** Controller name */
        private String controllerName;
        /** Request method name */
        private String requestMethodName;
        /** Method param types */
        private Class<?>[] methodParamTypes;
    }
}
