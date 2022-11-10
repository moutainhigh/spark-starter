package info.spark.agent.adapter.constant;

import lombok.experimental.UtilityClass;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.19 05:16
 * @since 1.0.0
 */
@UtilityClass
public class AgentApapterConstants {
    /** DEMO_URL_CONFIG */
    public static final String DEMO_URL_CONFIG = "${spark.feign.url.demo}";
    /** THIRD_URL_CONFIG */
    public static final String THIRD_URL_CONFIG = "${spark.feign.url.third-center}";
    /** third 中台服务名 */
    public static final String THIRD_CENTER_NAME = "third-center";

}
