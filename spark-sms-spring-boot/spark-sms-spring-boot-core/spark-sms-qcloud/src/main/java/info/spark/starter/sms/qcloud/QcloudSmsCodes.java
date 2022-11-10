package info.spark.starter.sms.qcloud;

import info.spark.starter.basic.annotation.ModelSerial;
import info.spark.starter.basic.annotation.ThirdLevel;
import info.spark.starter.sms.assertion.SmsExceptionAssert;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description:  </p>
 * https://cloud.tencent.com/document/product/382/3771
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.03.01 19:14
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
@ModelSerial(modelName = "S")
public enum QcloudSmsCodes implements SmsExceptionAssert {
    /** Ok qcloud sms codes */
    OK(0, "SUCCESS"),
    /** Error 1001 qcloud sms codes */
    @ThirdLevel
    ERROR_1001(1001, QcloudBundle.message("error.1001")),
    /** Error 1002 */
    @ThirdLevel
    ERROR_1002(1002, QcloudBundle.message("error.1002")),
    /** Error 1003 qcloud sms codes */
    @ThirdLevel
    ERROR_1003(1003, QcloudBundle.message("error.1003")),
    /** Error 1004 qcloud sms codes */
    @ThirdLevel
    ERROR_1004(1004, QcloudBundle.message("error.1004")),
    /** Error 1006 */
    @ThirdLevel
    ERROR_1006(1006, QcloudBundle.message("error.1006")),
    /** Error 1007 */
    @ThirdLevel
    ERROR_1007(1007, QcloudBundle.message("error.1007")),
    /** Error 1008 qcloud sms codes */
    @ThirdLevel
    ERROR_1008(1008, QcloudBundle.message("error.1008")),
    /** Error 1009 */
    @ThirdLevel
    ERROR_1009(1009, QcloudBundle.message("error.1009")),
    /** Error 1011 */
    @ThirdLevel
    ERROR_1011(1011, QcloudBundle.message("error.1011")),
    /** Error 1012 qcloud sms codes */
    @ThirdLevel
    ERROR_1012(1012, QcloudBundle.message("error.1012")),
    /** Error 1013 */
    @ThirdLevel
    ERROR_1013(1013, QcloudBundle.message("error.1013")),
    /** Error 1014 qcloud sms codes */
    @ThirdLevel
    ERROR_1014(1014, QcloudBundle.message("error.1014")),
    /** Error 1015 */
    @ThirdLevel
    ERROR_1015(1015, QcloudBundle.message("error.1015")),
    /** Error 1016 qcloud sms codes */
    @ThirdLevel
    ERROR_1016(1016, QcloudBundle.message("error.1016")),
    /** Error 1017 qcloud sms codes */
    @ThirdLevel
    ERROR_1017(1017, QcloudBundle.message("error.1017")),
    /** Error 1019 qcloud sms codes */
    @ThirdLevel
    ERROR_1019(1019, QcloudBundle.message("error.1019")),
    /** Error 1020 */
    @ThirdLevel
    ERROR_1020(1020, QcloudBundle.message("error.1020")),
    /** Error 1021 qcloud sms codes */
    @ThirdLevel
    ERROR_1021(1021, QcloudBundle.message("error.1021")),
    /** Error 1022 qcloud sms codes */
    @ThirdLevel
    ERROR_1022(1022, QcloudBundle.message("error.1022")),
    /** Error 1023 qcloud sms codes */
    @ThirdLevel
    ERROR_1023(1023, QcloudBundle.message("error.1023")),
    /** Error 1024 qcloud sms codes */
    @ThirdLevel
    ERROR_1024(1024, QcloudBundle.message("error.1024")),
    /** Error 1025 qcloud sms codes */
    @ThirdLevel
    ERROR_1025(1025, QcloudBundle.message("error.1025")),
    /** Error 1026 qcloud sms codes */
    @ThirdLevel
    ERROR_1026(1026, QcloudBundle.message("error.1026")),
    /** Error 1029 qcloud sms codes */
    @ThirdLevel
    ERROR_1029(1029, QcloudBundle.message("error.1029")),
    /** Error 1030 qcloud sms codes */
    @ThirdLevel
    ERROR_1030(1030, QcloudBundle.message("error.1030")),
    /** Error 1031 qcloud sms codes */
    @ThirdLevel
    ERROR_1031(1031, QcloudBundle.message("error.1031")),
    /** Error 1032 qcloud sms codes */
    @ThirdLevel
    ERROR_1032(1032, QcloudBundle.message("error.1032")),
    /** Error 1033 qcloud sms codes */
    @ThirdLevel
    ERROR_1033(1033, QcloudBundle.message("error.1033")),
    /** Error 1034 qcloud sms codes */
    @ThirdLevel
    ERROR_1034(1034, QcloudBundle.message("error.1034")),
    /** Error 1036 qcloud sms codes */
    @ThirdLevel
    ERROR_1036(1036, QcloudBundle.message("error.1036")),
    /** Error 1038 qcloud sms codes */
    @ThirdLevel
    ERROR_1038(1038, QcloudBundle.message("error.1038")),
    /** Error 1045 qcloud sms codes */
    @ThirdLevel
    ERROR_1045(1045, QcloudBundle.message("error.1045")),
    /** Error 1046 qcloud sms codes */
    @ThirdLevel
    ERROR_1046(1046, QcloudBundle.message("error.1046")),
    /** Error 1047 */
    @ThirdLevel
    ERROR_1047(1047, QcloudBundle.message("error.1047")),
    /** Error 1048 qcloud sms codes */
    @ThirdLevel
    ERROR_1048(1048, QcloudBundle.message("error.1048")),
    /** Error 60008 qcloud sms codes */
    @ThirdLevel
    ERROR_60008(60008, QcloudBundle.message("error.60008")),
    /** Unknown */
    @ThirdLevel
    UNKNOWN(-999, QcloudBundle.message("error.-999"));

    /** 错误类型码 */
    private final Integer code;
    /** 错误类型描述信息 */
    private final String message;

}
