package info.spark.feign.adapter.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>Description: feign 服务异常</p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:26
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FeignAdapterException extends RuntimeException {

    /** serialVersionUID */
    private static final long serialVersionUID = 521487192664577374L;
    /** Code */
    private String code;
    /** Message */
    private String message;

    /**
     * Instantiates a new Feign exception.
     *
     * @since 1.0.0
     */
    public FeignAdapterException() {
    }

    /**
     * Instantiates a new Feign exception.
     *
     * @param message message
     * @since 1.0.0
     */
    public FeignAdapterException(String message) {
        super(message);
    }


    /**
     * Instantiates a new Feign exception.
     *
     * @param code   the code
     * @param errmsg the errmsg
     * @since 1.0.0
     */
    public FeignAdapterException(String code, String errmsg) {
        super(errmsg);
        this.code = code;
        this.message = errmsg;
    }

    /**
     * To string string
     *
     * @return the string
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return "{"
               + "\"code\":"
               + this.code
               + ", \"message\":\""
               + this.message
               + "\""
               + "}";
    }
}
