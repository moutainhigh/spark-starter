package info.spark.starter.mybatis.plugins;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import info.spark.starter.util.AesUtils;
import info.spark.starter.util.Base64Utils;
import info.spark.starter.util.CollectionUtils;
import info.spark.starter.util.ReflectionUtils;
import info.spark.starter.util.StringUtils;
import info.spark.starter.support.annotation.SensitiveField;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Description: </p>
 *
 * @author zhubo
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.13 11:24
 * @since 1.5.0
 */
@Intercepts(
    @Signature(
        type = Executor.class,
        method = "update",
        args = {MappedStatement.class, Object.class}))
public class SensitiveFieldEncryptIntercepter implements Interceptor {
    /** Sensitive key */
    private final String sensitiveKey;

    /**
     * Sensitive field encrypt intercepter
     *
     * @param sensitiveKey sensitive key
     * @since 1.5.0
     */
    @Contract(pure = true)
    public SensitiveFieldEncryptIntercepter(String sensitiveKey) {
        this.sensitiveKey = sensitiveKey;
    }

    /**
     * Intercept
     *
     * @param invocation invocation
     * @return the object
     * @throws Throwable throwable
     * @since 1.0.0
     */
    @Override
    public Object intercept(@NotNull Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        List<Object> parameterList = new ArrayList<>(2);
        // 更新操作获取参数实体需要特殊处理下
        if (SqlCommandType.UPDATE.equals(sqlCommandType)) {
            Object arg = invocation.getArgs()[1];
            // 如果期望实现敏感字段更新自动脱敏, 请使用UpdateWrapper方式操作更新
            if (arg instanceof MapperMethod.ParamMap) {
                MapperMethod.ParamMap<?> paramMap = (MapperMethod.ParamMap<?>) arg;
                // et 实体对象 (set 条件值,可以为 null)
                if (paramMap.containsKey(Constants.ENTITY)) {
                    Object parameter1 = paramMap.get("et");
                    if (parameter1 != null) {
                        parameterList.add(parameter1);
                    }
                }
            }
        } else if (SqlCommandType.INSERT.equals(sqlCommandType)) {
            // 插入只会有一个实体
            Object parameter = invocation.getArgs()[1];
            if (parameter != null) {
                parameterList.add(parameter);
            }
        }
        if (CollectionUtils.isNotEmpty(parameterList)) {
            for (Object o : parameterList) {
                Class<?> clazz = o.getClass();
                if (!clazz.getSuperclass().isInstance(Object.class)) {
                    // 如果有父类, 需要对父类中判断是否使用自定义注解
                    Class<?> superclass = clazz.getSuperclass();
                    this.encryptField(superclass.getDeclaredFields(), o, sqlCommandType);
                }
                this.encryptField(o.getClass().getDeclaredFields(), o, sqlCommandType);
            }
        }

        return invocation.proceed();
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

    /**
     * Encrypt field
     *
     * @param declaredFields declared fields
     * @param parameter      parameter
     * @param sqlCommandType sql command type
     * @since 1.0.0
     */
    private void encryptField(Field @NotNull [] declaredFields, Object parameter, SqlCommandType sqlCommandType) {
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(SensitiveField.class)) {
                // 如果使用了指定注解, 对内容加密再存储
                Object fieldValue = ReflectionUtils.getFieldValue(parameter, field.getName());
                if (!StringUtils.isEmpty(fieldValue)) {
                    byte[] encrypt = AesUtils.encrypt(String.valueOf(fieldValue), this.sensitiveKey);
                    String encryptStr = Base64Utils.encodeToString(encrypt);
                    ReflectionUtils.setFieldValue(parameter, field.getName(), encryptStr);
                }
            }
        }
    }
}
