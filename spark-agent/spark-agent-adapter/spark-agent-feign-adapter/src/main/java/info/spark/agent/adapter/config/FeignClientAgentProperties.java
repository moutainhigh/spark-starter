package info.spark.agent.adapter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.4
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.01.29 01:05
 * @since 1.0.0
 */
@Data
@Component
public class FeignClientAgentProperties {
    /** 启动时是否检查 client 连接是否可用 */
    @Value("${spark.feign.enable-check:false}")
    private boolean enableCheck;
}
