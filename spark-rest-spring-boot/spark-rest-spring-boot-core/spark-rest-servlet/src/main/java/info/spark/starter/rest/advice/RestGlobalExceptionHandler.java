package info.spark.starter.rest.advice;

import info.spark.starter.rest.exception.ServletGlobalExceptionHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: servlet 全局异常处理器 </p>
 * {@link org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler}
 * {@link org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver}
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 10:23
 * @since 1.0.0
 */
@Slf4j
public class RestGlobalExceptionHandler extends ServletGlobalExceptionHandler {

    /**
     * Rest global exception handler
     *
     * @since 2022.1.1
     */
    public RestGlobalExceptionHandler() {
        log.info("加载全局异常处理器: [{}]", RestGlobalExceptionHandler.class);
    }
}
