package info.spark.starter.openness.handler;

import javax.servlet.http.HttpServletRequest;


/**
 * <p>Description: 资源 ACL 过滤 </p>
 *
 * @author wanghao
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.08.19 11:28
 * @since 1.9.0
 */
public interface IResourceAclHandler {

    /**
     * Allow
     *
     * @param accessId           access id
     * @param httpServletRequest http servlet request
     * @return the boolean
     * @since 1.9.0
     */
    default boolean allow(String accessId, HttpServletRequest httpServletRequest) {
        return true;
    }

}
