package info.spark.starter.openness.handler;

/**
 * <p>Description: 黑、白名单过滤 </p>
 *
 * @author wanghao
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.08.19 11:25
 * @since 1.9.0
 */
public interface IBucketListHandler {

    /**
     * Adopt
     *
     * @param accessId access id
     * @return the boolean
     * @since 1.9.0
     */
    default boolean adopt(String accessId) {
        return true;
    }

}
