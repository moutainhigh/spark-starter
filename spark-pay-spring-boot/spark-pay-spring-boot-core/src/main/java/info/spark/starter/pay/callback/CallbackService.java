package info.spark.starter.pay.callback;

/**
 * <p>Description: 回调业务处理接口, 实现类必须注入到 IoC, 用于注入业务需要的 bean </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.04.01 20:43
 * @since 1.0.0
 */
public interface CallbackService {

    /**
     * 检查业务对应的业务是否处理完成
     *
     * @param agrs agrs
     * @return the boolean true: 已完成, 将执行 commit; false: 未完成, 将循环重试
     * @since 1.0.0
     */
    boolean check(Object... agrs);
}
