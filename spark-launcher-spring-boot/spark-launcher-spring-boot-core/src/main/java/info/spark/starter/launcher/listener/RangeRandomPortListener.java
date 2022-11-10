package info.spark.starter.launcher.listener;

import info.spark.starter.common.SparkApplicationListener;
import info.spark.starter.launcher.env.RangeRandomValuePropertySource;

import info.spark.starter.processor.annotation.AutoListener;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.core.Ordered;


/**
 * <p>Description: range.random 配置监听器 </p>
 * 注意: 每次通过 key 去获取随机端口 ${range.random.int(1111, 2222)}
 *
 * @author zhubo
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.23 14:18
 * @see ConfigFileApplicationListener
 * @since 1.0.0
 */
@AutoListener
public class RangeRandomPortListener implements SparkApplicationListener {

    /** DEFAULT_ORDER */
    private static final int DEFAULT_ORDER = Ordered.HIGHEST_PRECEDENCE + 9;

    /**
     * Gets order *
     *
     * @return the order
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return DEFAULT_ORDER;
    }

    /**
     * 在应用读取完所有配置之后处理
     *
     * @param event event
     * @since 1.0.0
     */
    @Override
    public void onApplicationEnvironmentPreparedEvent(@NotNull ApplicationEnvironmentPreparedEvent event) {
        SparkApplicationListener.Runner.executeAtFirst(this.key(event, this.getClass()),
                                                       () -> RangeRandomValuePropertySource.addToEnvironment(event.getEnvironment()));
    }
}
