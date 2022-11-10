package info.spark.starter.mongo.datasource;

import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * <p>Description: 私有线程，存放当前线程mongodb的数据源 </p>
 *
 * @author zengminlong
 * @version 1.2.3
 * @email "mailto:zengminlong@gmail.com"
 * @date 2021.07.19 09:28
 * @since 1.0.0
 */
public class MongoContextHolder {

    /**
     * 线程级参数
     */
    private static final ThreadLocal<MongoTemplate> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     *
     * @param datasource datasource 不能为null
     */
    public static void setDataSource(MongoTemplate datasource) {
        CONTEXT_HOLDER.set(datasource);
    }

    /**
     *
     * @return MongoTemplate
     */
    public static MongoTemplate getDatasource() {
        return CONTEXT_HOLDER.get();
    }

    public static void clear() {
        CONTEXT_HOLDER.remove();
    }

}
