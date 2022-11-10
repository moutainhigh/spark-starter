package info.spark.starter.launcher.banner;

import java.io.InputStream;

/**
 * <p>Description: 自定义打印 banner, 关闭了 SpringBoot 默认的 banner 输出</p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.26 21:45
 * @since 1.0.0
 */
public interface Banner {

    /**
     * Print banner.
     *
     * @param inputStream the input stream      输入流, 从文件中获取
     * @param profile     the profile           当前使用的环境
     * @param version     the version           当前应用版本号
     * @param appName     the app name          应用名
     * @since 1.0.0
     */
    void printBanner(InputStream inputStream, String profile, String version, String appName);
}
