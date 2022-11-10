package info.spark.starter.mongo.mapper;

import info.spark.starter.mongo.datasource.MongoContextHolder;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.lang.reflect.Method;

/**
 * <p>Description:  </p>
 *
 * @author zengminlong
 * @version 1.2.3
 * @email "mailto:zengminlong@gmail.com"
 * @date 2021.07.19 14:27
 * @since 1.0.0
 */
public class MongoTemplateProxy implements MethodInterceptor {


    /** INSERT */
    private static final String INSERT = "insert";

    /**
     * Intercept
     *
     * @param o           o
     * @param method      method
     * @param objects     objects
     * @param methodProxy method proxy
     * @return the object
     * @throws Throwable throwable
     * @since 1.0.0
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        if (INSERT.equals(method.getName())) {
            MongoContextHolder.setDataSource((MongoTemplate) o);
        }
        return methodProxy.invokeSuper(o, objects);
    }
}
