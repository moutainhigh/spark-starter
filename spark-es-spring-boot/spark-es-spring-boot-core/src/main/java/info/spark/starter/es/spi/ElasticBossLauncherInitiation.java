package info.spark.starter.es.spi;

import com.google.common.collect.Lists;

import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.basic.util.StringPool;
import info.spark.starter.common.start.LauncherInitiation;
import info.spark.starter.util.core.support.ChainMap;
import info.spark.starter.util.StringUtils;
import info.spark.starter.es.entity.constant.ElasticBossKeyConstant;
import info.spark.starter.processor.annotation.AutoService;

import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.List;
import java.util.Map;

/**
 * <p>Description: 通过 SPI 加载 bboss es 默认配置 </p>
 *
 * @author wanghao
 * @version 1.8.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.27 19:07
 * @since 1.8.0
 */
@AutoService(LauncherInitiation.class)
public class ElasticBossLauncherInitiation implements LauncherInitiation {

    /** ELASTIC_AUTO_CONFIG */
    private static final String ELASTIC_AUTO_CONFIG = "org.springframework.boot.autoconfigure.data.elasticsearch"
                                                      + ".ElasticsearchAutoConfiguration";
    /** ELASTIC_DATA_AUTO_CONFIG */
    private static final String ELASTIC_DATA_AUTO_CONFIG = "org.springframework.boot.autoconfigure.data.elasticsearch"
                                                           + ".ElasticsearchDataAutoConfiguration";
    /** ELASTIC_REPO_AUTO_CONFIG */
    private static final String ELASTIC_REPO_AUTO_CONFIG = "org.springframework.boot.autoconfigure.data.elasticsearch"
                                                           + ".ElasticsearchRepositoriesAutoConfiguration";
    /** ELASTIC_HEALTH_AUTO_CONFIG */
    private static final String ELASTIC_HEALTH_AUTO_CONFIG = "org.springframework.boot.autoconfigure.elasticsearch.rest"
                                                             + ".RestClientAutoConfiguration";

    /**
     * Advance
     *
     * @param appName app name
     * @since 1.8.0
     */
    @Override
    public void advance(String appName) {
        List<String> list = Lists.newArrayList(ELASTIC_AUTO_CONFIG, ELASTIC_DATA_AUTO_CONFIG, ELASTIC_REPO_AUTO_CONFIG,
                                               ELASTIC_HEALTH_AUTO_CONFIG);
        LOG.warn("禁用 [{}] 和 [{}] 和 [{}] 和 [{}]", list.toArray());

        String property = System.getProperty(ConfigKey.SpringConfigKey.AUTOCONFIGURE_EXCLUDE);

        String value = String.join(StringPool.COMMA, list);
        if (StringUtils.isNotBlank(property)) {
            list.add(0, property);
        }
        System.setProperty(ConfigKey.SpringConfigKey.AUTOCONFIGURE_EXCLUDE, value);

    }

    /**
     * Launcher
     *
     * @param env           env
     * @param appName       app name
     * @param isLocalLaunch is local launch
     * @return the map
     * @since 1.8.0
     */
    @Override
    public Map<String, Object> launcher(ConfigurableEnvironment env,
                                        String appName,
                                        boolean isLocalLaunch) {

        return ChainMap.build(2)
            .put(ElasticBossKeyConstant.TIME_ZONE, "Asia/Shanghai");

    }

    /**
     * Gets order *
     *
     * @return the order
     * @since 1.8.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 200;
    }

    /**
     * Gets name *
     *
     * @return the name
     * @since 1.8.0
     */
    @Override
    public String getName() {
        return "spark-starter-es";
    }
}
