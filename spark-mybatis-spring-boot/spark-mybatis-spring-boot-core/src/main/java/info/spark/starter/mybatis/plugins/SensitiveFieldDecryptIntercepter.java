package info.spark.starter.mybatis.plugins;

import info.spark.starter.common.base.BaseDTO;
import info.spark.starter.common.base.BasePO;
import info.spark.starter.util.AesUtils;
import info.spark.starter.util.Base64Utils;
import info.spark.starter.util.ReflectionUtils;
import info.spark.starter.util.StringUtils;
import info.spark.starter.support.annotation.SensitiveBody;
import info.spark.starter.support.annotation.SensitiveField;

import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.Statement;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author zhubo
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.13 11:24
 * @since 1.5.0
 */
@Intercepts(@Signature(
    type = ResultSetHandler.class,
    method = "handleResultSets",
    args = {Statement.class}))
@Slf4j
public class SensitiveFieldDecryptIntercepter implements Interceptor {
    /** Sensitive key */
    private final String sensitiveKey;

    /**
     * Sensitive field encrypt intercepter
     *
     * @param sensitiveKey sensitive key
     * @since 1.5.0
     */
    @Contract(pure = true)
    public SensitiveFieldDecryptIntercepter(String sensitiveKey) {
        this.sensitiveKey = sensitiveKey;
    }

    /**
     * Intercept
     * todo-dong4j : (2020.05.25 19:50) [类型转换问题]
     *
     * @param invocation invocation
     * @return the object
     * @throws Throwable throwable
     * @since 1.0.0
     */
    @Override
    public Object intercept(@NotNull Invocation invocation) throws Throwable {
        //执行请求方法, 并将所得结果保存到result中
        Object result = invocation.proceed();
        if (result instanceof List) {
            for (Object o : (List<?>) result) {
                if (o instanceof BasePO || o instanceof BaseDTO) {
                    this.process(o);
                }
            }
        }
        return result;

    }

    /**
     * 加密处理, 目前递归
     *
     * @param o o
     * @since 1.6.0
     */
    private void process(Object o) {
        ReflectionUtils.doWithFields(o.getClass(), field -> {
            Object fieldValue = ReflectionUtils.getFieldValue(o, field.getName());
            if (fieldValue != null) {
                this.process(fieldValue);
            }
        }, field -> o instanceof BaseDTO && field.getAnnotation(SensitiveBody.class) != null);

        ReflectionUtils.doWithFields(o.getClass(), field -> {
            Object fieldValue = ReflectionUtils.getFieldValue(o, field.getName());
            if (!StringUtils.isEmpty(fieldValue)) {
                try {
                    String decrypt = AesUtils.decryptToStr(Base64Utils.decodeFromString(String.valueOf(fieldValue)),
                                                           this.sensitiveKey);

                    ReflectionUtils.setFieldValue(o, field.getName(), decrypt);
                } catch (Exception e) {
                    log.debug("敏感字段解密异常, fieldValue={}, exception={}", fieldValue, e.getMessage());
                }
            }
        }, field -> field.getAnnotation(SensitiveField.class) != null);
    }

    /**
     * Plugin
     *
     * @param target target
     * @return the object
     * @since 1.0.0
     */
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

}
