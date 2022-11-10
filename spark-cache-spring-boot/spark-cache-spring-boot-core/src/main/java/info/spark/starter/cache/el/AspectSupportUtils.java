package info.spark.starter.cache.el;

import info.spark.starter.basic.support.StrFormatter;
import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.basic.util.StringPool;
import info.spark.starter.cache.exception.CacheLockException;
import info.spark.starter.core.util.DataTypeUtils;
import info.spark.starter.util.StringUtils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.10.17 20:27
 * @since 1.6.0
 */
@Slf4j
@UtilityClass
public class AspectSupportUtils {
    /** evaluator */
    private static final ExpressionEvaluator EVALUATOR = new ExpressionEvaluator();

    /**
     * Gets key value *
     *
     * @param joinPoint     join point
     * @param keyExpression key expression
     * @return the key value
     * @since 1.6.0
     */
    public static Object getKeyValue(@NotNull JoinPoint joinPoint, String keyExpression) {
        return getKeyValue(joinPoint.getTarget(),
                           joinPoint.getArgs(),
                           joinPoint.getTarget().getClass(),
                           ((MethodSignature) joinPoint.getSignature()).getMethod(),
                           keyExpression);
    }

    /**
     * 如果存在 EL 表达式占位符, 则解析 EL 表达式, 不存在则返回 "", 需要保证 key 冲突问题.
     * todo-dong4j : (2021.05.19 14:06) [重构 if]
     *
     * @param object        object
     * @param args          args
     * @param clazz         clazz
     * @param method        method
     * @param keyExpression key expression
     * @return the key value
     * @since 1.6.0
     */
    private static Object getKeyValue(Object object,
                                      Object[] args,
                                      Class<?> clazz,
                                      Method method,
                                      String keyExpression) {
        // 如果存在 EL 表达式占位符, 则解析 EL 表达式
        if (StringUtils.hasText(keyExpression)) {
            Object[] argsTmp = new Object[args.length];
            System.arraycopy(args, 0, argsTmp, 0, args.length);
            if (method.getParameterTypes().length > 0) {
                //若分布式锁的key取值的父参数对象是非基础类型且参数值为null，
                //且参数类含有无参构造方法，则初始化一个对象作为参数。避免EL 表达式解析 #obj.username 时报错
                //若参数为null ，但是key取值不是参数的成员属性，则不处理
                //若参数为null，但是没有作为key的取值，则不处理
                for (int i = 0; i < method.getParameterTypes().length; i++) {
                    if (args[i] == null && notBaseType(method.getParameterTypes()[i])) {
                        if (!(keyExpression.contains("#" + method.getParameters()[i].getName() + StringPool.DOT))) {
                            continue;
                        }
                        try {
                            Constructor<?> constructor = null;
                            for (Constructor<?> c : method.getParameterTypes()[i].getConstructors()) {
                                if (c.getParameterTypes().length == 0) {
                                    constructor = c;
                                    break;
                                }
                            }
                            if (constructor == null) {
                                throw new CacheLockException(
                                    StrFormatter.format(
                                        "@CacheLock 锁指定的分布式锁 key为null，初始化对象错误,参数无无参构造方法。method:[{}],args:[{}]",
                                        method.toGenericString(),
                                        JsonUtils.toJson(args)));
                            } else {
                                argsTmp[i] = constructor.newInstance();
                                log.warn("@CacheLock 锁指定的分布式锁 key为null，初始化空对象。method:[{}],args:[{}]",
                                         method.toGenericString(),
                                         JsonUtils.toJson(args));
                            }
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                            log.error("@CacheLock 锁指定的分布式锁 key为null，初始化对象错误。method:[{}]", method.toGenericString());
                            throw new CacheLockException(
                                StrFormatter.format(
                                    "@CacheLock 锁指定的分布式锁 key为null，初始化对象错误。method:[{}],args:[{}]",
                                    method.toGenericString(),
                                    JsonUtils.toJson(args)));
                        }
                    }
                }
            }
            EvaluationContext evaluationContext = EVALUATOR.createEvaluationContext(object, clazz, method, argsTmp);
            AnnotatedElementKey methodKey = new AnnotatedElementKey(method, clazz);
            return EVALUATOR.key(keyExpression, methodKey, evaluationContext);
        }
        return "";
    }

    /**
     * Not base type
     *
     * @param clazz clazz
     * @return the boolean
     * @since 1.7.0
     */
    @Contract("null -> false")
    private static boolean notBaseType(Class<?> clazz) {
        return clazz != null
               && !String.class.isAssignableFrom(clazz)
               && !DataTypeUtils.isPrimitive(DataTypeUtils.typeUnBoxing(clazz));
    }
}
