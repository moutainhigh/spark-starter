package info.spark.starter.rest.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.type.TypeFactory;
import info.spark.starter.common.enums.SerializeEnum;
import info.spark.starter.util.core.api.BaseCodes;
import info.spark.starter.util.core.exception.BaseException;
import info.spark.starter.util.ObjectUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJacksonInputMessage;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * <p>Description: rest 接口参数自定义处理与绑定 </p>
 *
 * @param <A> parameter
 * @author dong4j
 * @version 1.4.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.23 21:13
 * @since 1.4.0
 */
public abstract class AbstractMethodArgumentResolver<A extends Annotation> implements HandlerMethodArgumentResolver {

    /** Object mapper */
    protected final ObjectMapper objectMapper;
    /** Global enum converter factory */
    protected final ConverterFactory<String, SerializeEnum<?>> globalEnumConverterFactory;

    /**
     * Request single param handler method argument resolver
     *
     * @param objectMapper               object mapper
     * @param globalEnumConverterFactory global enum converter factory
     * @since 1.0.0
     */
    @Contract(pure = true)
    public AbstractMethodArgumentResolver(ObjectMapper objectMapper,
                                          ConverterFactory<String, SerializeEnum<?>> globalEnumConverterFactory) {
        this.objectMapper = objectMapper;
        this.globalEnumConverterFactory = globalEnumConverterFactory;
    }

    /**
     * Supports parameter boolean
     * HandlerMethodArgumentResolverComposite#getArgumentResolver
     *
     * @param parameter parameter
     * @return the boolean
     * @since 1.0.0
     */
    @Override
    public boolean supportsParameter(@NotNull MethodParameter parameter) {
        return parameter.hasParameterAnnotation(this.supportsAnnotation());
    }

    /**
     * Resolve argument object
     *
     * @param parameter     parameter
     * @param mavContainer  mav container
     * @param webRequest    web request
     * @param binderFactory binder factory
     * @return the object
     * @since 1.0.0
     */
    @Override
    public Object resolveArgument(@NotNull MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  @NotNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        A annotation = parameter.getParameterAnnotation(this.supportsAnnotation());
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        if (ObjectUtils.isNull(request)) {
            throw new BaseException("不是 servle 请求!");
        }

        ServletServerHttpRequest inputMessage = this.createInputMessage(request);

        this.check(annotation, request);

        try {
            if (request.getInputStream() == null) {
                throw new HttpMessageNotReadableException("", inputMessage);
            }
            Type type = parameter.getGenericParameterType();
            return this.read(parameter, type, inputMessage, annotation);
        } catch (Exception ex) {
            throw new BaseException("参数绑定失败: [{}]", ex.getMessage());
        }
    }

    /**
     * Check
     *
     * @param annotation request single param
     * @param request    request
     * @since 1.4.0
     */
    private void check(A annotation, HttpServletRequest request) {
        BaseCodes.PARAM_VERIFY_ERROR.notNull(request, "Request is null");
        BaseCodes.PARAM_VERIFY_ERROR.isTrue(HttpServletRequestWrapper.class.isAssignableFrom(request.getClass()),
                                            "为避免 Request 多次读取可能导致 NPE 问题, "
                                            + "Request 必须使用 CacheRequestEnhanceWrapper 进行包装处理");
        BaseCodes.PARAM_VERIFY_ERROR.isTrue(HttpMethod.POST.name().equalsIgnoreCase(Objects.requireNonNull(request).getMethod())
                                            || HttpMethod.PUT.name().equalsIgnoreCase(request.getMethod()),
                                            "@" + Objects.requireNonNull(annotation).getClass().getSimpleName()
                                            + " 只支持 POST/PUT 请求.");
    }

    /**
     * Create input message servlet server http request
     * AbstractMessageConverterMethodArgumentResolver#createInputMessage
     *
     * @param request request
     * @return the servlet server http request
     * @since 1.0.0
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    private ServletServerHttpRequest createInputMessage(HttpServletRequest request) {
        return new ServletServerHttpRequest(request);
    }

    /**
     * Read object
     *
     * @param parameter    parameter
     * @param type         type
     * @param inputMessage input message
     * @param annotation   request single param
     * @return the object
     * @throws IOException                     io exception
     * @throws HttpMessageNotReadableException http message not readable exception
     * @since 1.0.0
     */
    private Object read(MethodParameter parameter, Type type, HttpInputMessage inputMessage, A annotation)
        throws IOException, HttpMessageNotReadableException {

        TypeFactory typeFactory = this.objectMapper.getTypeFactory();
        JavaType javaType = typeFactory.constructType(GenericTypeResolver.resolveType(type, (Class<?>) null));
        return this.readJavaType(parameter, javaType, inputMessage, annotation);
    }

    /**
     * Read java type object
     * AbstractJackson2HttpMessageConverter#readJavaType
     *
     * @param parameter    parameter
     * @param javaType     java type
     * @param inputMessage input message
     * @param annotation   request single param
     * @return the object
     * @throws IOException io exception
     * @since 1.0.0
     */
    private Object readJavaType(MethodParameter parameter,
                                JavaType javaType,
                                HttpInputMessage inputMessage,
                                A annotation) throws IOException {
        try {
            if (inputMessage instanceof MappingJacksonInputMessage) {
                Class<?> deserializationView = ((MappingJacksonInputMessage) inputMessage).getDeserializationView();
                if (deserializationView != null) {
                    return this.objectMapper.readerWithView(deserializationView).forType(javaType).
                        readValue(inputMessage.getBody());
                }
            }

            return this.bundleArgument(parameter, javaType, inputMessage, annotation);
        } catch (InvalidDefinitionException ex) {
            throw new HttpMessageConversionException("Type definition error: " + ex.getType(), ex);
        } catch (JsonProcessingException ex) {
            throw new HttpMessageNotReadableException("JSON parse error: " + ex.getOriginalMessage(), ex, inputMessage);
        }
    }

    /**
     * Supports annotation
     *
     * @return the class
     * @since 1.4.0
     */
    protected abstract Class<A> supportsAnnotation();

    /**
     * Bundle argument
     *
     * @param parameter    parameter
     * @param javaType     java type
     * @param inputMessage input message
     * @param annotation   request single param
     * @return the object
     * @since 1.4.0
     */
    protected abstract Object bundleArgument(MethodParameter parameter,
                                             JavaType javaType,
                                             HttpInputMessage inputMessage,
                                             A annotation);

    /**
     * Convert
     *
     * @param <T>          parameter
     * @param clazz        clazz
     * @param targetObject target object
     * @return the t
     * @since 1.4.0
     */
    @SuppressWarnings("unchecked")
    protected <T> T convert(@NotNull Class<? extends SerializeEnum<?>> clazz, Object targetObject) {
        return (T) this.globalEnumConverterFactory.getConverter(clazz).convert(String.valueOf(targetObject));
    }
}
