package info.spark.starter.id.provider;

import info.spark.starter.basic.support.StrFormatter;
import info.spark.starter.basic.util.StringUtils;
import info.spark.starter.id.util.IpUtils;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 通过所有机器列表为每一个机器生成一个唯一的 id, 适合服务节点较少的情况</p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.24 16:25
 * @since 1.5.0
 */
@Slf4j
public class IpConfigurableMachineIdProvider implements MachineIdProvider {

    /** Machine id */
    @Getter
    @Setter
    private long machineId;

    /** Ips map */
    private final Map<String, Long> ipsMap = new HashMap<>(8);

    /**
     * Ip configurable machine id provider
     *
     * @since 1.5.0
     */
    public IpConfigurableMachineIdProvider() {
        log.debug("IpConfigurableMachineIdProvider constructed.");
    }

    /**
     * Ip configurable machine id provider
     *
     * @param ips ips
     * @since 1.5.0
     */
    public IpConfigurableMachineIdProvider(String ips) {
        this.setIps(ips);
        this.init();
    }

    /**
     * 每个服务节点根据本机 ip 获取 ip 映射的位置作为机器号.
     *
     * @since 1.5.0
     */
    public void init() {
        String ip = IpUtils.getHostIp();

        if (StringUtils.isEmpty(ip)) {
            String msg = "Fail to get host IP address. Stop to initialize the IpConfigurableMachineIdProvider provider.";

            log.error(msg);
            throw new IllegalStateException(msg);
        }

        if (!this.ipsMap.containsKey(ip)) {
            String msg = StrFormatter.format("Fail to configure ID for host IP address {}. "
                                             + "Stop to initialize the IpConfigurableMachineIdProvider provider.",
                                             ip);

            log.error(msg);
            throw new IllegalStateException(msg);
        }

        this.machineId = this.ipsMap.get(ip);

        log.info("IpConfigurableMachineIdProvider.init ip {} id {}", ip, this.machineId);
    }

    /**
     * Sets ips *
     *
     * @param ips ips
     * @since 1.5.0
     */
    public void setIps(String ips) {
        log.debug("IpConfigurableMachineIdProvider ips {}", ips);
        if (!StringUtils.isEmpty(ips)) {
            String[] ipArray = ips.split(",");

            for (int i = 0; i < ipArray.length; i++) {
                this.ipsMap.put(ipArray[i], (long) i);
            }
        }
    }
}
