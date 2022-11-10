package info.spark.starter.rest.runner;

import info.spark.starter.common.constant.App;
import info.spark.starter.common.util.ConfigKit;
import info.spark.starter.util.StringUtils;
import info.spark.starter.util.SystemUtils;

import org.jetbrains.annotations.Contract;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.06 19:08
 * @since 1.0.0
 */
@Slf4j
@Order
public class OpenBrowserRunner implements CommandLineRunner {
    /** Executor */
    @Resource
    private ThreadPoolTaskExecutor executor;

    /** Is enable browser */
    private final boolean isEnableBrowser;

    /**
     * Open browser runner
     *
     * @param isEnableBrowser is enable browser
     * @since 1.0.0
     */
    @Contract(pure = true)
    public OpenBrowserRunner(boolean isEnableBrowser) {
        this.isEnableBrowser = isEnableBrowser;
    }

    /**
     * Run *
     *
     * @param gs gs
     * @since 1.0.0
     */
    @Override
    public void run(String... gs) {
        this.executor.execute(() -> {
            Thread.currentThread().setName("browse");
            String url = System.getProperty(App.START_URL);
            // 配置了 ms.rest.enable-browser=true 且是 local 环境时才打开
            boolean isLocalLaunch = ConfigKit.isLocalLaunch();
            if (isLocalLaunch
                && !App.START_JUNIT.equals(System.getProperty(App.START_TYPE))
                && StringUtils.isNotBlank(url)
                && this.isEnableBrowser) {
                log.debug("will open browse to access {}, "
                          + "you can set spark.rest.enable-browser=false to close this function, "
                          + "not local env default close", url);
                LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2));
                OpenBrowserRunner.browse(url);
            }
        });

    }

    /**
     * Browse *
     *
     * @param url url
     * @since 1.0.0
     */
    private static void browse(String url) {
        // 获取操作系统的名字
        try {
            if (SystemUtils.isMac()) {
                // 苹果
                Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
                fileMgr.getDeclaredMethod("openURL", String.class).invoke(null, url);
            } else if (SystemUtils.isWindows()) {
                // windows
                Runtime.getRuntime().exec(
                    "rundll32 url.dll,FileProtocolHandler " + url);
            } else {
                // Unix or Linux
                String[] browsers = {
                    "firefox",
                    "opera",
                    "konqueror",
                    "epiphany",
                    "mozilla",
                    "netscape"
                };
                String browser = null;
                // 执行代码,在brower有值后跳出,
                // 这里是如果进程创建成功了,==0是表示正常结束.
                for (int count = 0; count < browsers.length && browser == null; count++) {
                    if (Runtime.getRuntime()
                            .exec(new String[] {"which", browsers[count]})
                            .waitFor() == 0) {
                        browser = browsers[count];
                    }
                }

                if (browser == null) {
                    throw new Exception("Could not find web browser");
                } else {
                    // 这个值在上面已经成功的得到了一个进程.
                    Runtime.getRuntime().exec(new String[] {browser, url});
                }
            }

        } catch (Exception e) {
            // 非业务错误, 吞掉异常堆栈信息
            log.error("open browse error: {}", e.getMessage());
        }
    }
}
