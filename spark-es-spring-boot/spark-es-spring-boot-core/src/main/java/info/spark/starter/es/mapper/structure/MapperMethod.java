package info.spark.starter.es.mapper.structure;

import java.io.Serializable;
import java.lang.reflect.Method;

import lombok.Builder;
import lombok.Data;

/**
 * <p>Description:  </p>
 *
 * @author wanghao
 * @version 1.7.1
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.22 12:02
 * @since 1.7.1
 */
@Data
@Builder
public class MapperMethod implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 5873383714232092033L;
    /** Method */
    private Method method;
    /** Name */
    private String name;
    /** Params */
    private Integer params;
    /** Params type */
    private Class<?>[] paramsType;
    /** Args */
    private Object[] args;
    /** Result type */
    private Class<?> resultType;

}
