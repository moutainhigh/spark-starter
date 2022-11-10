package info.spark.feign.adapter.client;

import info.spark.feign.adapter.annotation.FeignClient;

/**
 * <p>Description: 直接使用 url  </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.28 23:25
 * @since 1.0.0
 */
@FeignClient(url = "${spark.feign.url.demo-service}")
public interface FeignClient3 {

}
