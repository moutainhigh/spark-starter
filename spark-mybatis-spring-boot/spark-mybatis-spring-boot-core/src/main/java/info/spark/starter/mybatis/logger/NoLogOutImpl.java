package info.spark.starter.mybatis.logger;

import info.spark.starter.mybatis.plugins.PerformanceInterceptor;
import info.spark.starter.mybatis.spi.MybatisLauncherInitiation;

import org.apache.ibatis.logging.slf4j.Slf4jImpl;

/**
 * <p>Description: 不使用 mybatis 的 sql 日志格式</p>
 * 如果将 mapper 目录日志等级设置为 debug/trace, mybatis 将会输出日志, 但是一个 sql 将输出 3 行.
 * 为了减少日志输出, 这里重写 log 不输出日志, 使用 {@link PerformanceInterceptor} 代替, 需要配置 mybatis-plus.configuration.log-impl.
 * 如果想输出 mybatis 的原生 sql 日志, 可覆盖 mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.slf4j.Slf4jImpl
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.04.29 15:18
 * @see PerformanceInterceptor
 * @see MybatisLauncherInitiation
 * @since 1.8.0
 */
public class NoLogOutImpl extends Slf4jImpl {
    /**
     * Mybatis plus out
     *
     * @param clazz clazz
     * @since 1.8.0
     */
    public NoLogOutImpl(String clazz) {
        super(clazz);
    }

    /**
     * Debug
     *
     * @param s s
     * @since 1.8.0
     */
    @Override
    public void debug(String s) {
    }

    /**
     * Trace
     *
     * @param s s
     * @since 1.8.0
     */
    @Override
    public void trace(String s) {
    }

}
