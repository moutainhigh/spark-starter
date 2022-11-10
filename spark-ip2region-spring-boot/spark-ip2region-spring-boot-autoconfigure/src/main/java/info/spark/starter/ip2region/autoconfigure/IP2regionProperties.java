package info.spark.starter.ip2region.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.12.05 16:26
 * @since 1.7.0
 */
@Data
@ConfigurationProperties(IP2regionProperties.PREFIX)
public class IP2regionProperties {

    /** Prefix */
    static final String PREFIX = "spark.ip2region";
    /** 是否使用外部的IP数据文件. */
    private boolean external = false;
    /** ip2region.db 文件路径, 默认: classpath:includes/ip2region.db */
    private String location = "classpath:includes/ip2region.db";
    /** total header data block size, must be times of 8; default 8192 */
    private int totalHeaderSize = 8192;
    /** max index data block size u should always choice the fastest read block, size;default 4 * 1024 = 4096 */
    private int indexBlockSize = 4096;
}
