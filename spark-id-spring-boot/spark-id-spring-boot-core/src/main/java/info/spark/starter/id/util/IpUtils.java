package info.spark.starter.id.util;

import info.spark.starter.core.util.NetUtils;

import lombok.experimental.UtilityClass;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.24 16:25
 * @since 1.5.0
 */
@UtilityClass
public class IpUtils {

    /**
     * Gets host ip *
     *
     * @return the host ip
     * @since 1.5.0
     */
    public static String getHostIp() {
        return NetUtils.getLocalHost();
    }

    /**
     * Gets host name *
     *
     * @return the host name
     * @since 1.5.0
     */
    public static String getHostName() {
        return NetUtils.getHostName();
    }
}
