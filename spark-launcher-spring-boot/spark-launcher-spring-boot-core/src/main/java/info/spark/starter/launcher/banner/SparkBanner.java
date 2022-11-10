package info.spark.starter.launcher.banner;

import info.spark.starter.basic.util.StringPool;
import info.spark.starter.common.util.ConfigKit;
import info.spark.starter.common.util.StartUtils;
import info.spark.starter.util.DateUtils;
import info.spark.starter.util.StringUtils;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.boot.SpringBootVersion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: Banner 实现, 输出自定义 banner</p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 00:29
 * @since 1.0.0
 */
@Slf4j
public class SparkBanner implements Banner {

    /**
     * Print banner *
     *
     * @param inputStream input stream
     * @param profile     profile
     * @param version     version
     * @param appName     app name
     * @since 1.0.0
     */
    @Override
    public void printBanner(InputStream inputStream, String profile, String version, String appName) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String blankCharacter = "   ";
            String info = blankCharacter + "::: Spring Boot (version:{}) :::        ::: {} :::        ::: spring.profiles.active = {} :::";
            String versionInfo = StringUtils.format(info, SpringBootVersion.getVersion(), version, profile);
            if (profile.startsWith(StringPool.LEFT_SQ_BRACKET)) {
                versionInfo = blankCharacter + profile;
            }
            int length = versionInfo.length();
            StringBuilder str = new StringBuilder(blankCharacter);
            for (int i = 0; i < length - blankCharacter.length(); i++) {
                str.append("-");
            }

            this.printLine(appName, br, versionInfo, str);

        } catch (IOException ignored) {
        }
    }

    /**
     * Print line.
     *
     * @param appName     the app name
     * @param br          the br                banner 内容
     * @param versionInfo the version info
     * @param str         the str               下划线长度
     * @throws IOException the io exception
     * @since 1.0.0
     */
    private void printLine(String appName,
                           @NotNull BufferedReader br,
                           String versionInfo,
                           @NotNull StringBuilder str) throws IOException {
        // 日志配置文件: MarkerPatternSelector.PatternMatch.key = banner
        Marker bannerMarker = MarkerFactory.getMarker("banner");

        String underline = str.toString();
        String devModel = "Local Development Model";
        if (ConfigKit.notLocalLaunch()) {
            devModel = "Started By Shell";
        }
        String startline = "::: "
                           + appName
                           + "("
                           + ConfigKit.getAppVersion()
                           + ")"
                           + " starting time: "
                           + DateUtils.formatDateTime(new Date())
                           + " start.model: " + devModel + " :::";

        startline = StringUtils.center(startline, underline.length(), StringPool.SPACE);
        int maxlength = 0;
        log.info(bannerMarker, "");
        log.info(bannerMarker, "");
        log.info(bannerMarker, startline);
        log.info(bannerMarker, "");

        // 循环输出 banner 内容
        String line;
        while ((line = br.readLine()) != null) {
            maxlength = Math.max(maxlength, line.length());
            log.info(bannerMarker, StringUtils.center(line, underline.length(), StringPool.SPACE));
        }

        log.info(bannerMarker, StartUtils.showInfo(maxlength, str.toString()));
        log.info(bannerMarker, StartUtils.showInfo(maxlength, versionInfo));
        log.info(bannerMarker, StartUtils.showInfo(maxlength, str.toString()));
        log.info(bannerMarker, "");
    }
}
