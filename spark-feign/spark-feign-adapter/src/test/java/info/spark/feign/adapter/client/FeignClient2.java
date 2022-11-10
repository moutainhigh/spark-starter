package info.spark.feign.adapter.client;

import info.spark.feign.adapter.annotation.FeignClient;

/**
 * <p>Description: 通过 name 生成 url  </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.28 23:25
 * @since 1.0.0
 */
@FeignClient(name = "demo-service")
public interface FeignClient2 {

}
