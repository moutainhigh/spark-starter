package sample.pay.service;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import info.spark.starter.util.StringUtils;
import info.spark.starter.util.ThreadUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import sample.pay.entity.dto.PaymentTransactionDTO;
import sample.pay.entity.dto.PaymentTransactionLogDTO;
import sample.pay.entity.form.CallbackForm;
import sample.pay.entity.po.PaymentTransaction;
import sample.pay.enums.PaymentStatus;
import sample.pay.wrapper.PaymentTransactionLogWrapper;
import sample.pay.wrapper.PaymentTransactionWrapper;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.04.01 20:58
 * @since 1.0.0
 */
@Slf4j
@Service
public class PayService {
    /** Void service */
    @Resource
    private VoidService voidService;
    /** Payment transaction repository service */
    @Resource
    private PaymentTransactionRepositoryService paymentTransactionRepositoryService;

    /** 使用 serialPolicy 自定义序列化/反序列化 */
    @CreateCache(expire = 10000, cacheType = CacheType.REMOTE, name = "pay:callback:", serialPolicy = "bean:jacksonValueSerialPolicy")
    private Cache<String, CallbackForm> payCallbackCache;

    /**
     * Pay
     *
     * @return the long
     * @since 1.0.0
     */
    @Transactional(rollbackFor = Exception.class)
    public void pay() {

        String payNo = StringUtils.getUid();
        BigDecimal payAmount = new BigDecimal("88.88");

        PaymentTransactionDTO paymentTransaction = PaymentTransactionDTO.builder()
            .paymentStatus(PaymentStatus.UNPAY)
            .orderId(1L)
            .payAmount(payAmount)
            .payNo(payNo)
            .build();

        // 创建支付交易
        PaymentTransaction po = PaymentTransactionWrapper.INSTANCE.po(paymentTransaction);
        po.insert();

        // 调用第三方支付服务
        this.invokeRemotePayService(payNo, payAmount);

        // 模拟耗时操作, 让异步回调在此事务提交之前执行
        ThreadUtils.sleep(1, TimeUnit.SECONDS);
        // 创建支付交易日志
        PaymentTransactionLogWrapper.INSTANCE.po(PaymentTransactionLogDTO.builder()
                                                     .synchLog("创建交易记录")
                                                     .payId(po.getId())
                                                     .build()).insert();

        ThreadUtils.sleep(2, TimeUnit.SECONDS);

        this.voidService.service();

        log.info("业务逻辑处理完成, 提交事务");
    }

    /**
     * 模拟调用第三方支付服务, 这里把业务参数存入 redis, 在单元测试中, 从 redis 获取业务传过来的参数, 在传给回调接口
     *
     * @param payNo     pay no
     * @param payAmount pay amount
     * @since 1.0.0
     */
    private void invokeRemotePayService(String payNo, BigDecimal payAmount) {
        this.payCallbackCache.put("test", CallbackForm.builder().payNo(payNo).payAmount(payAmount).build());
    }
}
