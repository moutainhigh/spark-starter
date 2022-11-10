package info.spark.starter.logsystem.enums;

/**
 * 操作动作
 *
 * @author sujian
 * @version 1.0.0
 * @email "mailto:sujian@gmail.com"
 * @date 2020.05.26 11:39
 * @since 1.0.0
 */
public enum OperationAction {

    /** Default operation action */
    DEFAULT("DEFAULT", "默认"),
    /** Register operation action */
    REGISTER("REGISTER", "注册"),

    /** Login operation action */
    LOGIN("LOGIN", "登录"),

    /** Logout operation action */
    LOGOUT("LOGOUT", "退出"),

    /** Add operation action */
    ADD("ADD", "新增"),

    /** Update operation action */
    UPDATE("UPDATE", "修改"),

    /** Delete operation action */
    DELETE("DELETE", "删除"),

    /** Upload file operation action */
    UPLOAD_FILE("UPLOAD_FILE", "上传文件"),

    /** Import operation action */
    IMPORT("IMPORT", "导入"),

    /** Send verify code operation action */
    SEND_VERIFY_CODE("SEND_VERIFY_CODE", "发送验证码");

    /** Code */
    private final String code;
    /** Name */
    private final String name;

    /**
     * Operation action
     *
     * @param code code
     * @param name name
     * @since 1.5.0
     */
    OperationAction(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * Gets code *
     *
     * @return the code
     * @since 1.5.0
     */
    public String getCode() {
        return this.code;
    }

    /**
     * Gets name *
     *
     * @return the name
     * @since 1.5.0
     */
    public String getName() {
        return this.name;
    }
}
