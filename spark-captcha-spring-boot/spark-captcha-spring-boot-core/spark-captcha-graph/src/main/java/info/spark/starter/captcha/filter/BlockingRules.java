package info.spark.starter.captcha.filter;

import com.fasterxml.jackson.databind.JsonNode;
import info.spark.starter.basic.constant.BasicConstant;
import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.captcha.AbstractCaptcha;
import info.spark.starter.captcha.constant.CaptchaConstant;
import info.spark.starter.captcha.entity.FilterBean;
import info.spark.starter.util.core.exception.BaseException;
import info.spark.starter.util.CollectionUtils;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>Description: 验证码拦截规则 </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.21 09:34
 * @since 1.0.0
 */
public interface BlockingRules {

    /**
     * Filter *
     *
     * @param request request
     * @param captcha captcha
     * @param filters filters
     * @throws ServletRequestBindingException servlet request binding exception
     * @since 1.0.0
     */
    default void filter(@NotNull HttpServletRequest request,
                        AbstractCaptcha captcha,
                        List<FilterBean> filters) throws ServletRequestBindingException {
        String httpMethod = request.getMethod();
        String uri = request.getRequestURI();

        if (CollectionUtils.isNotEmpty(filters)) {
            // 拦截符合要求的 URI 和 http method
            boolean matched = filters.stream()
                .anyMatch(m -> m.getUri().matches(uri) && httpMethod.equalsIgnoreCase(m.getMethod().name()));

            if (matched && captcha.showCaptcha(request)) {
                this.validate(request, captcha);
            }
        }
    }

    /**
     * Validate
     *
     * @param request request
     * @param captcha captcha
     * @throws ServletRequestBindingException servlet request binding exception
     * @since 1.8.0
     */
    default void validate(@NotNull HttpServletRequest request, AbstractCaptcha captcha) throws ServletRequestBindingException {
        String code = null;
        String uuid = null;
        String contentType = request.getContentType();
        if (MediaType.valueOf(contentType).getSubtype().equalsIgnoreCase(BasicConstant.JSON)) {
            // application/json 需要从bodyString中获取
            ServletInputStream inputStream;
            try {
                inputStream = request.getInputStream();
            } catch (IOException e) {
                throw new BaseException(e);
            }
            JsonNode jsonNode = JsonUtils.readTree(inputStream);
            code = jsonNode.path(CaptchaConstant.VERIFICATION_CODE).asText();
            uuid = jsonNode.path(CaptchaConstant.VERIFICATION_UUID).asText();

        } else if (contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
            code = ServletRequestUtils.getStringParameter(request, CaptchaConstant.VERIFICATION_CODE);
            uuid = ServletRequestUtils.getStringParameter(request, CaptchaConstant.VERIFICATION_UUID);
        }
        captcha.validate(uuid, code);
    }
}
