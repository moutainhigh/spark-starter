package info.spark.starter.id.util;

import info.spark.starter.core.util.NetUtils;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.28 17:37
 * @since 1.5.0
 */
@Slf4j
class IpUtilsTest {

    @Test
    void test_1() {
        log.info("1 {}", NetUtils.getLocalHost());
        log.info("2 {}", NetUtils.getLocalIpAddr());
        log.info("3 {}", IpUtils.getHostIp());

        log.info("4 {}", IpUtils.getHostName());
        log.info("5 {}", NetUtils.getHostName());

    }

}
