package info.spark.starter.dubbo.filter;

import info.spark.starter.basic.constant.TraceConstant;
import info.spark.starter.basic.context.Trace;
import info.spark.starter.util.StringUtils;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.ListenableFilter;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Description: 跨进行参数传递 </p>
 *
 * @author zhubo
 * @version 1.5.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.28 16:58
 * @since 1.5.0
 */
@Activate(group = {CommonConstants.PROVIDER, CommonConstants.CONSUMER}, order = 4)
public class CrossJvmParameterPassingFilter extends ListenableFilter {

    /**
     * Cross jvm parameter passing filter
     *
     * @since 1.6.0
     */
    public CrossJvmParameterPassingFilter() {
        super.listener = new ContextListener();
    }

    /**
     * Invoke
     *
     * @param invoker    invoker
     * @param invocation invocation
     * @return result result
     * @throws RpcException rpc exception
     * @since 1.6.0
     */
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        RpcContext rpcContext = RpcContext.getContext();
        boolean consumerSide = rpcContext.isConsumerSide();
        // 将 application context 数据写入到 rpc context
        if (consumerSide) {
            // 如果集成了 tracer 插件, 则会将 traceId 写入到 Trace 类中, 这里先从 Trace 获取, 没有则生成一个 UUID
            String traceId = Trace.context().get();
            if (StringUtils.isBlank(traceId)) {
                traceId = StringUtils.getUid();
            }
            RpcContext.getContext().setAttachment(TraceConstant.TRACE_ID, traceId);
            // 调用完成后不删除 context, 避免再次调用另一个服务时 context 无数据
            return invoker.invoke(invocation);
        }

        // 将 rpc context 数据写入到 application context
        Trace.context().set(invocation.getAttachment(TraceConstant.TRACE_ID, StringUtils.getUid()));
        try {
            return invoker.invoke(invocation);
        } finally {
            // provider 侧在调用完成后统一删除
            Trace.context().remove();
        }
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.09.13 15:52
     * @since 1.6.0
     */
    static class ContextListener implements Listener {
        /**
         * On response
         *
         * @param appResponse app response
         * @param invoker     invoker
         * @param invocation  invocation
         * @since 1.6.0
         */
        @Override
        public void onResponse(@NotNull Result appResponse, Invoker<?> invoker, Invocation invocation) {
            // pass attachments to result
            appResponse.addAttachments(invocation.getAttachments());
        }

        /**
         * On error
         *
         * @param t          t
         * @param invoker    invoker
         * @param invocation invocation
         * @since 1.6.0
         */
        @Override
        public void onError(Throwable t, Invoker<?> invoker, Invocation invocation) {

        }
    }
}
