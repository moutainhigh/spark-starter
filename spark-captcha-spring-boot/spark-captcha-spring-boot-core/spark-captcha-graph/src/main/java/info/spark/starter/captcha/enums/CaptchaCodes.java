package info.spark.starter.captcha.enums;

import info.spark.starter.basic.annotation.ModelSerial;
import info.spark.starter.basic.annotation.SystemLevel;
import info.spark.starter.captcha.CaptchaBundle;
import info.spark.starter.captcha.assertion.CaptchaExceptionAssert;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.20 15:15
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
@ModelSerial(modelName = "E")
public enum CaptchaCodes implements CaptchaExceptionAssert {

    /** Code not exist security codes */
    @SystemLevel
    CODE_NOT_EXIST(40009, CaptchaBundle.message("code.not.exist")),
    /** Code timeout security codes */
    @SystemLevel
    CODE_TIMEOUT(40010, CaptchaBundle.message("code.timeout")),
    /** Code error security codes */
    @SystemLevel
    CODE_ERROR(40011, CaptchaBundle.message("code.error")),
    /** Code render error security codes */
    @SystemLevel
    CODE_RENDER_ERROR(40012, CaptchaBundle.message("code.render.error")),
    /** Code build error security codes */
    @SystemLevel
    CODE_BUILD_ERROR(40072, CaptchaBundle.message("code.build.error"));

    /** 返回码 */
    private final Integer code;
    /** 返回消息 */
    private final String message;
}
