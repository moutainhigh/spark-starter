package info.spark.starter.cloud.autoconfigure.nacos;

import com.alibaba.cloud.nacos.diagnostics.analyzer.NacosConnectionFailureException;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;

import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 管理 ConfigService, 用于操作自定义配置 </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.20 18:01
 * @since 1.0.0
 */
@Slf4j
public class SparkConfigManager extends AbstractNacosManager<ConfigService> {
    /**
     * Nacos config manager
     *
     * @param sparkNacosProperties spark nacos properties
     * @since 1.0.0
     */
    public SparkConfigManager(SparkNacosProperties sparkNacosProperties) {
        super(sparkNacosProperties);
    }

    /**
     * Compatible with old design,It will be perfected in the future.
     *
     * @param sparkNacosProperties spark nacos properties
     * @return the config service
     * @since 1.0.0
     */
    @Override
    public ConfigService createService(SparkNacosProperties sparkNacosProperties) {
        if (Objects.isNull(this.service)) {
            synchronized (SparkConfigManager.class) {
                try {
                    if (Objects.isNull(this.service)) {
                        this.service = NacosFactory.createConfigService(sparkNacosProperties.assembleConfigServiceProperties());
                    }
                } catch (NacosException e) {
                    throw new NacosConnectionFailureException(sparkNacosProperties.getConfig().getServerAddr(), e.getMessage(), e);
                }
            }
        }
        return this.service;
    }
}
