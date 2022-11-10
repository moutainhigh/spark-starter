package info.spark.starter.schedule.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.4.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.23 14:49
 * @since 1.4.0
 */
@Getter
@AllArgsConstructor
public enum GlueTypeEnum {

    /** Bean glue type enum */
    BEAN("BEAN", false, null, null),
    /** Glue groovy glue type enum */
    GLUE_GROOVY("GLUE(Java)", false, null, null),
    /** Glue shell glue type enum */
    GLUE_SHELL("GLUE(Shell)", true, "bash", ".sh"),
    /** Glue python glue type enum */
    GLUE_PYTHON("GLUE(Python)", true, "python", ".py"),
    /** Glue php glue type enum */
    GLUE_PHP("GLUE(PHP)", true, "php", ".php"),
    /** Glue nodejs glue type enum */
    GLUE_NODEJS("GLUE(Nodejs)", true, "node", ".js"),
    /** Glue powershell glue type enum */
    GLUE_POWERSHELL("GLUE(PowerShell)", true, "powershell", ".ps1");

    /** Desc */
    private final String desc;
    /** Is script */
    private final boolean isScript;
    /** Cmd */
    private final String cmd;
    /** Suffix */
    private final String suffix;

}
