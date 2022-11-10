package info.spark.starter.mybatis.injector.methods;

import info.spark.starter.mybatis.injector.MybatisSqlMethod;

/**
 * <p>Description: 插入一条数据 (选择字段插入) 插入如果中已经存在相同的记录,则忽略当前新数据 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 14:52
 * @since 1.0.0
 */
public class InsertIgnore extends AbstractInsertMethod {

    /**
     * Insert ignore
     *
     * @since 1.0.0
     */
    public InsertIgnore() {
        super(MybatisSqlMethod.INSERT_IGNORE_ONE);
    }
}
