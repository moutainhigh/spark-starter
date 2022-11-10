package info.spark.starter.mybatis.autoconfigure;

import com.alibaba.druid.wall.WallConfig;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import info.spark.starter.common.start.SparkAutoConfiguration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.06 22:27
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(MybatisPlusAutoConfiguration.class)
public class DruidAutoConfiguration implements SparkAutoConfiguration {

    /**
     * Wall config wall config
     *
     * @return the wall config
     * @since 1.0.0
     */
    @Bean
    public WallConfig wallConfig() {
        WallConfig wallConfig = new WallConfig();
        // 允许一次执行多条语句
        wallConfig.setMultiStatementAllow(true);
        // 允许一次执行多条语句
        wallConfig.setNoneBaseStatementAllow(true);
        return wallConfig;
    }

}
