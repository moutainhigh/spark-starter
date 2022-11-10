package info.spark.starter.rest.support;

import info.spark.starter.common.constant.App;
import info.spark.starter.common.start.SparkComponentBean;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.16 02:39
 * @since 1.7.1
 */
public class SparkRestComponent implements SparkComponentBean {
    /**
     * Component bean
     *
     * @return the string
     * @since 1.7.1
     */
    @Override
    public String componentName() {
        return App.Components.SPARK_REST_SPRING_BOOT;
    }
}
