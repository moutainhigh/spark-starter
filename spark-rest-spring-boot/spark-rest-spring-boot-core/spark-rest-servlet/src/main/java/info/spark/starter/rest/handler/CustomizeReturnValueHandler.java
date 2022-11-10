package info.spark.starter.rest.handler;

import info.spark.starter.basic.Result;
import info.spark.starter.basic.asserts.Assertions;
import info.spark.starter.basic.constant.BasicConstant;
import info.spark.starter.basic.util.StringUtils;
import info.spark.starter.common.base.BasePage;
import info.spark.starter.core.util.DataTypeUtils;
import info.spark.starter.rest.advice.ResponseWrapperAdvice;
import info.spark.starter.rest.annotation.OriginalResponse;
import info.spark.starter.rest.utis.RestUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 基础数据类型自动包装为 Map，返回内容是 BasePage 时需要取出 paging 并返回 </p>
 *
 * @author zhubo
 * @version 1.6.1
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.12.14 10:39
 * @since 1.6.1
 */
@Slf4j
public class CustomizeReturnValueHandler implements HandlerMethodReturnValueHandler {
    /** Target */
    private final RequestResponseBodyMethodProcessor target;

    /**
     * Page return value handler
     *
     * @param target target
     * @since 1.6.1
     */
    public CustomizeReturnValueHandler(RequestResponseBodyMethodProcessor target) {
        this.target = target;
    }

    /**
     * Supports return type
     *
     * @param returnType return type
     * @return the boolean
     * @since 1.6.1
     */
    @Override
    public boolean supportsReturnType(@NotNull MethodParameter returnType) {
        return this.target.supportsReturnType(returnType);
    }

    /**
     * 处理返回结果, 优先级在 {@link ResponseWrapperAdvice} 之前.
     * 用于处理服务端返回为基础类型/String/String[]/List 等类型的数据时, 服务端自动包装为 Map, 统一 Web 和 client 端的数据模型 (data 字段全部为 Object 类型).
     * 存在以下情况才有可能包装:
     * 1. 不存在 OriginalResponse 标识
     * 2. 未使用 Response 设置 Content-Type 或显式使用 response.setContentType("application/json")
     * 2. 没有显式设置 produces 或 produces 为 application/json
     *
     * @param returnValue  return value
     * @param returnType   return type
     * @param mavContainer mav container
     * @param webRequest   web request
     * @throws Exception exception
     * @see ResponseWrapperAdvice#beforeBodyWrite
     * @since 1.6.1
     */
    @Override
    @SuppressWarnings("all")
    public void handleReturnValue(Object returnValue,
                                  @NotNull MethodParameter returnType,
                                  @NotNull ModelAndViewContainer mavContainer,
                                  @NotNull NativeWebRequest webRequest) throws Exception {

        Object finalReturnValue = returnValue;
        // 如果是 string 则删除首尾空格
        if (finalReturnValue instanceof String) {
            finalReturnValue = StringUtils.trimWhitespace((String) finalReturnValue);
        }

        if (RestUtils.sparkClass(returnType) && RestUtils.supportsAdvice(returnType)) {
            Method method = returnType.getMethod();
            Assertions.notNull(method);
            @SuppressWarnings("all") OriginalResponse originalResponse = method.getAnnotation(OriginalResponse.class);
            // 如果不存在此注解, 则需要再次判断
            if (originalResponse == null) {
                String contentType = Objects.requireNonNull(webRequest.getNativeResponse(HttpServletResponse.class)).getContentType();

                // 没有使用 response 写数据 或者显式设置了 Content-Type 为 application/json
                boolean contentTypeIsJson = StringUtils.isNotBlank(contentType)
                                            && MediaType.parseMediaType(contentType).equalsTypeAndSubtype(MediaType.APPLICATION_JSON);

                // 显式使用 response.setContentType("application/json") 就优先使用
                if (contentTypeIsJson) {
                    finalReturnValue = this.returnValueWrapper(returnValue, returnType);
                } else if (StringUtils.isBlank(contentType)) {
                    // 如果没有使用 response.setContentType, 则判断 rest api 是否添加了 RequestMapping.produces, 且非 json 的不包装
                    RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
                    if (requestMapping != null) {
                        // 如果 rest api 上使用了 produces 属性, 则需要判断是否为 json
                        String[] serverProduces = requestMapping.produces();
                        String produceValue = serverProduces.length == 0 ? null
                                                                         : emptyToNull(serverProduces[0]);
                        // 没有显式设置 produces 或 produces 为 json 则包装
                        if (produceValue == null
                            || MediaType.parseMediaType(produceValue).equalsTypeAndSubtype(MediaType.APPLICATION_JSON)) {
                            finalReturnValue = this.returnValueWrapper(returnValue, returnType);
                        }
                    }

                } else {
                    // 只要使用了 response.setContentType 且不为 application/json 的, 就不再处理 produces, 优先使用显式设置的 Content-Type
                    log.trace("显式设置 Content-Type: {}", contentType);
                }
            }
        }

        this.target.handleReturnValue(finalReturnValue, returnType, mavContainer, webRequest);
    }

    /**
     * 包装返回值, 如果 returnValue 为 null 则直接返回.
     *
     * @param returnValue return value
     * @param returnType  return type
     * @return the object
     * @since 1.8.0
     */
    @Contract("null, _ -> null")
    private Object returnValueWrapper(Object returnValue, @NotNull MethodParameter returnType) {

        // 如果返回值为 null , 则交由 ResponseWrapperAdvice#beforeBodyWrite 处理
        if (returnValue == null) {
            return null;
        } else if (returnType.getMethod() != null && DataTypeUtils.isExtendPrimitive(returnType.getMethod())) {
            // 基础类型则包装为 map
            Map<String, Object> map = new HashMap<>(2);
            if (returnValue instanceof Result) {
                // 如果返回值是 Result，则需要取出 data 并封装为 Map
                map.put(BasicConstant.RESULT_WRAPPER_VALUE_KEY, ((Result<?>) returnValue).getData());
            } else {
                // 非 Result 的基础类型
                map.put(BasicConstant.RESULT_WRAPPER_VALUE_KEY, returnValue);
            }
            return map;
        } else {
            // 处理返回内容中包括 IPage 时返回实体格式
            return this.processBody(returnValue);
        }
    }

    /**
     * Empty to null
     *
     * @param string string
     * @return the string
     * @since 1.8.0
     */
    @Contract(value = "null -> null", pure = true)
    private static String emptyToNull(String string) {
        return string == null || string.isEmpty() ? null : string;
    }

    /**
     * 处理返回内容中包括 IPage 时返回实体格式
     *
     * @param body body
     * @return the object
     * @see ResponseWrapperAdvice#beforeBodyWrite
     * @since 1.6.1
     */
    private Object processBody(Object body) {
        Object result;
        Object data;
        if (body instanceof Result) {
            data = ((Result<?>) body).getData();
        } else {
            data = body;
        }
        // 判断 data 类型是否为 BasePage, 如果是，则返回 basePage.getPagination
        if (data instanceof BasePage) {
            result = ((BasePage<?>) data).getPagination();
        } else {
            // 如果解包后的数据或元素数据不是 BasePage, 则返回原始数据
            result = body;
        }
        return result;
    }

}
