package info.spark.feign.adapter.decode;

import info.spark.feign.adapter.exception.FeignAdapterException;
import info.spark.feign.adapter.exception.InternalException;
import info.spark.starter.basic.Result;
import info.spark.starter.basic.util.JsonUtils;

import org.springframework.context.annotation.Configuration;

import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: feign 异常封装, 只要返回的 code != 2000, 都抛出异常, 减少业务端判断;
 * 如果 http 异常, 则抛出 InternalException
 * </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:09
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class ExceptionErrorDecoder implements ErrorDecoder {

    /**
     * Decode exception
     *
     * @param s        s
     * @param response response
     * @return the exception
     * @since 1.0.0
     */
    @Override
    public Exception decode(String s, Response response) {
        try {
            if (response.body() != null) {
                String body = Util.toString(response.body().asReader());
                log.error(body);
                Result<?> result = JsonUtils.parse(body, Result.class);
                return new FeignAdapterException(result.getCode(), result.getMessage());
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return new InternalException("Feign client 远程调用网络错误");
    }
}
