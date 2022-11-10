package info.spark.starter.launcher.runner;

import info.spark.starter.basic.util.StringPool;
import info.spark.starter.common.constant.App;
import info.spark.starter.common.context.SpringContext;
import info.spark.starter.common.util.ConfigKit;
import info.spark.starter.common.util.StartUtils;
import info.spark.starter.util.StringUtils;

import org.slf4j.MDC;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 启动完成后输出信息 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:09
 * @since 1.0.0
 */
@Slf4j
@Order
@Component
public class StarterInfoRunner implements ApplicationRunner {

    /**
     * Run *
     *
     * @param args args
     * @since 1.0.0
     */
    @Override
    public void run(ApplicationArguments args) {
        try {
            ConfigKit.showDebugInfo();
            SpringContext.showDebugInfo();
        } catch (IllegalStateException e) {
            log.warn("{}", e.getMessage());
        }

        if (App.START_JUNIT.equals(ConfigKit.getProperty(App.START_TYPE))) {
            return;
        }

        String str = MDC.get(App.LIBRARY_NAME);
        if (StringUtils.isNotBlank(str)) {
            String[] components = str.split(StringPool.AT);
            StartUtils.printStartedInfo(components);
        } else {
            StartUtils.printSimpleInfo();
        }
        MDC.remove(App.LIBRARY_NAME);
    }
}
