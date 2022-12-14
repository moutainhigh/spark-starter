package info.spark.starter.dubbo.check;

import info.spark.starter.common.util.ConfigKit;

import org.apache.dubbo.rpc.RpcContext;

/**
 * <p>Description: RPC 请求检查接口 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.18 02:39
 * @since 1.5.0
 */
public class RpcCheckImpl implements RpcCheck {

    /**
     * Check
     *
     * @return the rpc context
     * @since 1.5.0
     */
    @Override
    public String check() {
        return ConfigKit.getAppName() + ":" + RpcContext.getContext().getLocalAddressString();
    }
}
