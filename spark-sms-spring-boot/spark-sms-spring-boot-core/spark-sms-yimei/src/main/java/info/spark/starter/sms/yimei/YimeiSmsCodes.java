package info.spark.starter.sms.yimei;

import info.spark.starter.basic.annotation.ModelSerial;
import info.spark.starter.basic.annotation.ThirdLevel;
import info.spark.starter.sms.assertion.SmsExceptionAssert;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description: </p>
 *
 * @author zhubo
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.02 21:03
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
@ModelSerial(modelName = "Y")
public enum YimeiSmsCodes implements SmsExceptionAssert {
    /** Ok qcloud sms codes */
    OK(0, "SUCCESS"),
    /** SYSTEM_ERROR */
    @ThirdLevel
    SYSTEM_ERROR(-1, YimeiBundle.message("system.error")),
    /** CLIENT_ERROR */
    @ThirdLevel
    CLIENT_ERROR(-2, YimeiBundle.message("client.error")),
    /** COMMAND_NOT_SUPPORT */
    @ThirdLevel
    COMMAND_NOT_SUPPORT(-101, YimeiBundle.message("command.not.support")),
    /** DELETE_MSG_ERROR */
    @ThirdLevel
    DELETE_MSG_ERROR(-102, YimeiBundle.message("delete.msg.error")),
    /** UPDATE_MSG_ERROR */
    @ThirdLevel
    UPDATE_MSG_ERROR(-103, YimeiBundle.message("update.msg.error")),
    /** REQUEST_OVER_LIMIT */
    @ThirdLevel
    REQUEST_OVER_LIMIT(-104, YimeiBundle.message("request.over.limit")),
    /** REGISTRY_ERROR */
    @ThirdLevel
    REGISTRY_ERROR(-104, YimeiBundle.message("registry.error")),
    /** SEND_MSG_ERROR */
    @ThirdLevel
    SEND_MSG_ERROR(-117, YimeiBundle.message("send.msg.error")),
    /** REV_MO_ERROR */
    @ThirdLevel
    REV_MO_ERROR(-118, YimeiBundle.message("rev.mo.error")),
    /** REV_REPORT_ERROR */
    @ThirdLevel
    REV_REPORT_ERROR(-119, YimeiBundle.message("rev.report.error")),
    /** UPDATE_PWD_ERROR */
    @ThirdLevel
    UPDATE_PWD_ERROR(-120, YimeiBundle.message("update.pwd.error")),
    /** LOGOUT_ACTIVE_ERROR */
    @ThirdLevel
    LOGOUT_ACTIVE_ERROR(-122, YimeiBundle.message("logout.active.error")),
    /** REGISTRY_ACTIVE_ERROR */
    @ThirdLevel
    REGISTRY_ACTIVE_ERROR(-110, YimeiBundle.message("registry.active.error")),
    /** QUERY_AVG_BALANCE_ERROR */
    @ThirdLevel
    QUERY_AVG_BALANCE_ERROR(-123, YimeiBundle.message("query.avg.balance.error")),
    /** QUERY_BALANCE_ERROR */
    @ThirdLevel
    QUERY_BALANCE_ERROR(-124, YimeiBundle.message("query.balance.error")),
    /** SETTING_MO_ERROR */
    @ThirdLevel
    SETTING_MO_ERROR(-125, YimeiBundle.message("setting.mo.error")),
    /** ROUTE_INFO_ERROR */
    @ThirdLevel
    ROUTE_INFO_ERROR(-126, YimeiBundle.message("route.info.error")),
    /** ZERO_BALANCE_ERROR */
    @ThirdLevel
    ZERO_BALANCE_ERROR(-127, YimeiBundle.message("zero.balance.error")),
    /** NOT_ENOUGH_BALANCE_ERROR */
    @ThirdLevel
    NOT_ENOUGH_BALANCE_ERROR(-128, YimeiBundle.message("not.enough.balance.error")),
    /** SER_ERROR */
    @ThirdLevel
    SER_ERROR(-1100, YimeiBundle.message("ser.error")),
    /** SER_PWD_ERROR */
    @ThirdLevel
    SER_PWD_ERROR(-1102, YimeiBundle.message("ser.pwd.error")),
    /** SER_KEY_ERROR */
    @ThirdLevel
    SER_KEY_ERROR(-1103, YimeiBundle.message("ser.key.error")),
    /** ROUTE_ERROR */
    @ThirdLevel
    ROUTE_ERROR(-1104, YimeiBundle.message("route.error")),
    /** REGISTRY_STATUS_1_ERROR */
    @ThirdLevel
    REGISTRY_STATUS_1_ERROR(-1105, YimeiBundle.message("registry.status.1.error")),
    /** REGISTRY_STATUS_3_ERROR */
    @ThirdLevel
    REGISTRY_STATUS_3_ERROR(-1107, YimeiBundle.message("registry.status.3.error")),
    /** REGISTRY_STATUS_5_ERROR */
    @ThirdLevel
    REGISTRY_STATUS_5_ERROR(-1108, YimeiBundle.message("registry.status.5.error")),
    /** RECHARGE_ERROR */
    @ThirdLevel
    RECHARGE_ERROR(-113, YimeiBundle.message("recharge.error")),
    /** RECHARGE_CARD_INVALID */
    @ThirdLevel
    RECHARGE_CARD_INVALID(-1131, YimeiBundle.message("recharge.card.invalid")),
    /** RECHARGE_PWD_INVALID */
    @ThirdLevel
    RECHARGE_PWD_INVALID(-1132, YimeiBundle.message("recharge.pwd.invalid")),
    /** RECHARGE_BIND_ERROR */
    @ThirdLevel
    RECHARGE_BIND_ERROR(-1133, YimeiBundle.message("recharge.bind.error")),
    /** RECHARGE_STATUS_INVALID */
    @ThirdLevel
    RECHARGE_STATUS_INVALID(-1134, YimeiBundle.message("recharge.status.invalid")),
    /** RECHARGE_MOUNT_INVALID */
    @ThirdLevel
    RECHARGE_MOUNT_INVALID(-1135, YimeiBundle.message("recharge.mount.invalid")),
    /** DATA_OPR_ERROR */
    @ThirdLevel
    DATA_OPR_ERROR(-190, YimeiBundle.message("data.opr.error")),
    /** DATA_INSERT_ERROR */
    @ThirdLevel
    DATA_INSERT_ERROR(-1901, YimeiBundle.message("data.insert.error")),
    /** DATA_UPDATE_ERROR */
    @ThirdLevel
    DATA_UPDATE_ERROR(-1902, YimeiBundle.message("data.update.error")),
    /** DATA_DELETE_ERROR */
    @ThirdLevel
    DATA_DELETE_ERROR(-1903, YimeiBundle.message("data.delete.error")),

    /** DATA_FORMAT_ERROR */
    @ThirdLevel
    DATA_FORMAT_ERROR(-9000, YimeiBundle.message("data.format.error")),
    /** SEQ_PATTERN_ERROR */
    @ThirdLevel
    SEQ_PATTERN_ERROR(-9001, YimeiBundle.message("seq.pattern.error")),
    /** PWD_PATTERN_ERROR */
    @ThirdLevel
    PWD_PATTERN_ERROR(-9002, YimeiBundle.message("pwd.pattern.error")),
    /** KEY_PATTERN_ERROR */
    @ThirdLevel
    KEY_PATTERN_ERROR(-9003, YimeiBundle.message("key.pattern.error")),
    /** SETTING_TRANSFER_ERROR */
    @ThirdLevel
    SETTING_TRANSFER_ERROR(-9004, YimeiBundle.message("setting.transfer.error")),
    /** COMPANY_ADDR_ERROR */
    @ThirdLevel
    COMPANY_ADDR_ERROR(-9005, YimeiBundle.message("company.addr.error")),
    /** COMPANY_NAME_FORMAT_ERROR */
    @ThirdLevel
    COMPANY_NAME_FORMAT_ERROR(-9006, YimeiBundle.message("company.name.format.error")),
    /** COMPANY_NAME_ERROR */
    @ThirdLevel
    COMPANY_NAME_ERROR(-9007, YimeiBundle.message("company.name.error")),
    /** EMAIL_ERROR */
    @ThirdLevel
    EMAIL_ERROR(-9008, YimeiBundle.message("email.error")),
    /** COMPANY_ENG_NAME_FORMAT_ERROR */
    @ThirdLevel
    COMPANY_ENG_NAME_FORMAT_ERROR(-9009, YimeiBundle.message("company.eng.name.format.error")),
    /** COMPANY_ENG_NAME_ERROR */
    @ThirdLevel
    COMPANY_ENG_NAME_ERROR(-9010, YimeiBundle.message("company.eng.name.error")),
    /** FAX_ERROR */
    @ThirdLevel
    FAX_ERROR(-9011, YimeiBundle.message("fax.error")),
    /** CONTACT_ERROR */
    @ThirdLevel
    CONTACT_ERROR(-9012, YimeiBundle.message("contact.error")),
    /** PHONE_ERROR */
    @ThirdLevel
    PHONE_ERROR(-9013, YimeiBundle.message("phone.error")),
    /** POST_CODE_ERROR */
    @ThirdLevel
    POST_CODE_ERROR(-9014, YimeiBundle.message("post.code.error")),
    /** NEW_PWD_ERROR */
    @ThirdLevel
    NEW_PWD_ERROR(-9015, YimeiBundle.message("new.pwd.error")),
    /** SEND_MSG_PACKAGE_OVER_RANGE */
    @ThirdLevel
    SEND_MSG_PACKAGE_OVER_RANGE(-9016, YimeiBundle.message("send.msg.package.over.range")),
    /** SEND_MSG_CONTENT_PATTERN_ERROR */
    @ThirdLevel
    SEND_MSG_CONTENT_PATTERN_ERROR(-9017, YimeiBundle.message("send.msg.content.pattern.error")),
    /** MSG_EXT_PATTERN_ERROR */
    @ThirdLevel
    MSG_EXT_PATTERN_ERROR(-9018, YimeiBundle.message("msg.ext.pattern.error")),
    /** MSG_PRIORITY_PATTERN_ERROR */
    @ThirdLevel
    MSG_PRIORITY_PATTERN_ERROR(-9019, YimeiBundle.message("msg.priority.pattern.error")),
    /** PHONE_NUM_ERROR */
    @ThirdLevel
    PHONE_NUM_ERROR(-9020, YimeiBundle.message("phone.num.error")),
    /** MSG_TIMER_ERROR */
    @ThirdLevel
    MSG_TIMER_ERROR(-9021, YimeiBundle.message("msg.timer.error")),
    /** SEQ_ERROR */
    @ThirdLevel
    SEQ_ERROR(-9022, YimeiBundle.message("seq.error")),
    /** RECHARGE_CARD_NUM_ERROR */
    @ThirdLevel
    RECHARGE_CARD_NUM_ERROR(-9023, YimeiBundle.message("recharge.card.num.error")),
    /** RECHARGE_CARD_PWD_ERROR */
    @ThirdLevel
    RECHARGE_CARD_PWD_ERROR(-9024, YimeiBundle.message("recharge.card.pwd.error")),
    /** Unknown */
    @ThirdLevel
    UNKNOWN(-999, YimeiBundle.message("unknown"));
    /** 错误类型码 */
    private final Integer code;
    /** 错误类型描述信息 */
    private final String message;
}
