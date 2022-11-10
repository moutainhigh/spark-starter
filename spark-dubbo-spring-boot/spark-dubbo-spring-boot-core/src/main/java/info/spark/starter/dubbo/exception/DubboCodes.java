package info.spark.starter.dubbo.exception;

import info.spark.starter.dubbo.DubboBundle;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.10.30 18:52
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum DubboCodes implements DubboExceptionAssert {

    /** Param error agent codes */
    PARAM_ERROR(40000, DubboBundle.message("dubbo.code.request.null")),
    /** Data error dubbo codes */
    DATA_ERROR(41000, DubboBundle.message("dubbo.code.data.error")),
    /** Save or update error agent codes */
    SAVE_OR_UPDATE_ERROR(40002, DubboBundle.message("dubbo.code.save.or.update.failure")),
    /** 操作错误 */
    OPERATION_ERROR(40002, DubboBundle.message("dubbo.code.operation.failure"));
    /** 返回码 */
    private final Integer code;
    /** 返回消息 */
    private final String message;
}
