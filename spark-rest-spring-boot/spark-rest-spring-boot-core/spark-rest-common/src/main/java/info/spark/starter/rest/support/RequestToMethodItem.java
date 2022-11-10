package info.spark.starter.rest.support;

import lombok.Builder;
import lombok.Data;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 21:47
 * @since 1.0.0
 */
@Data
@Builder
public class RequestToMethodItem {
    /** Request type */
    private String requestType;
    /** Request url */
    private String requestUrl;
    /** Controller name */
    private String controllerName;
    /** Request method name */
    private String requestMethodName;
    /** Method param types */
    private Class<?>[] methodParamTypes;
}
