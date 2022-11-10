package info.spark.starter.zookeeper;

import org.apache.curator.framework.CuratorFramework;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.29 16:57
 * @since 1.8.0
 */
@Slf4j
@UtilityClass
public class ZkClientUtils {
    /** zkAddr */
    private static String zkAddr;
    /** curatorClient */
    private static CuratorFramework curatorClient;

    /**
     * 静态内部类实现单例
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2021.01.29 16:57
     * @since 1.8.0
     */
    private static class SingletonHolder {
        /** zkClientInstance */
        private static ZkClient zkClientInstance;

        static {
            try {
                zkClientInstance = new ZkClient(zkAddr);
                curatorClient = zkClientInstance.getCuratorClient();
            } catch (Exception e) {
                log.error("init zookeeper client error", e);
            }
        }
    }

    /**
     * 注入已存在的 CuratorFramework curatorClient, 然后使用封装的 ZkClient 操作 zk
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2021.01.29 16:57
     * @since 1.8.0
     */
    private static class InjectHolder {
        /** zkClientInstance */
        private static ZkClient zkClientInstance;

        static {
            try {
                zkClientInstance = new ZkClient(curatorClient);
                curatorClient = zkClientInstance.getCuratorClient();
            } catch (Exception e) {
                log.error("init zookeeper client error", e);
            }
        }
    }

    /**
     * Gets zk client *
     *
     * @param zkAddr zk addr
     * @return the zk client
     * @since 1.8.0
     */
    public static ZkClient getZkClient(String zkAddr) {
        ZkClientUtils.zkAddr = zkAddr;
        return SingletonHolder.zkClientInstance;
    }

    /**
     * Gets zk client *
     *
     * @param curatorClient curator client
     * @return the zk client
     * @since 1.8.0
     */
    public static ZkClient getZkClient(CuratorFramework curatorClient) {
        ZkClientUtils.curatorClient = curatorClient;
        return InjectHolder.zkClientInstance;
    }
}
