package info.spark.starter.mybatis.autoconfigure;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.handlers.MybatisEnumTypeHandler;
import com.baomidou.mybatisplus.extension.plugins.IllegalSQLInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.SqlExplainInterceptor;
import info.spark.starter.basic.constant.ConfigDefaultValue;
import info.spark.starter.basic.constant.ConfigKey;
import info.spark.starter.common.constant.App;
import info.spark.starter.common.enums.LibraryEnum;
import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.common.start.SparkComponentBean;
import info.spark.starter.mybatis.handler.GeneralEnumTypeHandler;
import info.spark.starter.mybatis.handler.MetaObjectChain;
import info.spark.starter.mybatis.handler.SerializableIdTypeHandler;
import info.spark.starter.mybatis.handler.TenantIdMetaObjectHandler;
import info.spark.starter.mybatis.handler.TimeMetaObjectHandler;
import info.spark.starter.mybatis.plugins.PerformanceInterceptor;
import info.spark.starter.mybatis.plugins.SensitiveFieldEncryptIntercepter;
import info.spark.starter.mybatis.handler.ClientIdMetIdaObjectHandler;
import info.spark.starter.mybatis.handler.MetaHandlerChain;
import info.spark.starter.mybatis.injector.MybatisSqlInjector;
import info.spark.starter.mybatis.plugins.SensitiveFieldDecryptIntercepter;
import info.spark.starter.mybatis.util.SqlUtils;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.Serializable;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: mybatis 自动配置类 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.06 22:26
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(MybatisPlusAutoConfiguration.class)
@EnableConfigurationProperties(MybatisProperties.class)
public class MybatisAutoConfiguration implements SparkAutoConfiguration {

    /**
     * 非法 SQL 语句拦截器: SQL 严格模式
     * 1.必须使用到索引,包含 left jion 连接字段,符合索引最左原则
     * 必须使用索引好处:
     * 1.1 如果因为动态 SQL,bug 导致 update 的 where 条件没有带上,全表更新上万条数据
     * 1.2 如果检查到使用了索引,SQL 性能基本不会太差
     * <p>
     * 2.SQL 尽量单表执行,有查询 left jion 的语句,必须在注释里面允许该 SQL 运行,否则会被拦截,有 left jion 的语句,如果不能拆成单表执行的 SQL,请 leader 商量再做决定
     * http://gaoxianglong.github.io/shark/
     * SQL 尽量单表执行的好处:
     * 2.1 查询条件简单、易于开理解和维护;
     * 2.2 扩展性极强;  (可为分库分表做准备)
     * 2.3 缓存利用率高;
     * <p>
     * 2.在字段上使用函数
     * 3.where 条件为空
     * 4.where 条件使用了: !=
     * 5.where 条件使用了: not 关键字
     * 6.where 条件使用了: or 关键字
     * 7.where 条件使用了: 子查询
     *
     * @return the illegal sql interceptor
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(IllegalSQLInterceptor.class)
    @Profile(value = {App.START_JUNIT, App.ENV_LOCAL, App.ENV_DEV, App.ENV_TEST, App.ENV_PREV})
    @ConditionalOnProperty(value = ConfigKey.MYBATIS_ENABLE_ILLEGAL_SQL_INTERCEPTOR,
                           havingValue = ConfigDefaultValue.TRUE_STRING)
    public IllegalSQLInterceptor illegalSqlInterceptor() {
        return new IllegalSQLInterceptor();
    }

    /**
     * SQL执行分析插件, 拦截一些整表操作,在生产环境最好关闭.
     *
     * @return the sql explain interceptor
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(SqlExplainInterceptor.class)
    @Profile(value = {App.START_JUNIT, App.ENV_LOCAL, App.ENV_DEV, App.ENV_TEST, App.ENV_PREV})
    @ConditionalOnProperty(value = ConfigKey.MYBATIS_ENABLE_SQL_EXPLAIN_INTERCEPTOR,
                           havingValue = ConfigDefaultValue.TRUE_STRING,
                           matchIfMissing = true)
    public SqlExplainInterceptor sqlExplainInterceptor() {
        return new SqlExplainInterceptor();
    }

    /**
     * 分页插件, 不需要设置方言, mybatis-plus 自动判断
     *
     * @param mybatisProperties mybatis properties
     * @return the pagination interceptor
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(PaginationInterceptor.class)
    public PaginationInterceptor paginationInterceptor(@NotNull MybatisProperties mybatisProperties) {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // 设置默认最大分页数 (spark.mybatis.single-page-limit)
        paginationInterceptor.setLimit(mybatisProperties.getSinglePageLimit());
        return paginationInterceptor;
    }

    /**
     * sql 注入
     *
     * @return the sql injector
     * @since 1.0.0
     */
    @Bean
    public ISqlInjector sqlInjector() {
        return new MybatisSqlInjector();
    }

    /**
     * mybatis-plus SQL执行效率插件 (生产环境最好关闭)
     * 不存在 com.p6spy.engine.spy.P6SpyDriver 则使用此插件.
     *
     * @param mybatisProperties 配置类
     * @return the performance interceptor
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(PerformanceInterceptor.class)
    @ConditionalOnMissingClass("com.p6spy.engine.spy.P6SpyDriver")
    @Profile(value = {App.START_JUNIT, App.ENV_LOCAL, App.ENV_DEV, App.ENV_TEST, App.ENV_PREV})
    @ConditionalOnProperty(value = ConfigKey.MybatisConfigKey.MYBATIS_ENABLE_LOG,
                           havingValue = ConfigDefaultValue.TRUE_STRING,
                           matchIfMissing = true)
    public PerformanceInterceptor performanceInterceptor(MybatisProperties mybatisProperties) {
        PerformanceInterceptor performanceInterceptor = new PerformanceInterceptor();
        performanceInterceptor.setFormat(mybatisProperties.isSqlFormat());
        performanceInterceptor.setMaxTime(mybatisProperties.getPerformmaxTime());
        return performanceInterceptor;
    }

    /**
     * 脱敏插件-解码
     *
     * @param mybatisProperties mybatis properties
     * @return the sensitive field decrypt intercepter
     * @since 1.5.0
     */
    @Bean
    @ConditionalOnMissingBean(SensitiveFieldDecryptIntercepter.class)
    @ConditionalOnProperty(value = ConfigKey.MybatisConfigKey.MYBATIS_ENABLE_SENSITIVE,
                           havingValue = ConfigDefaultValue.TRUE_STRING,
                           matchIfMissing = true)
    public SensitiveFieldDecryptIntercepter sensitiveFieldDecryptIntercepter(@NotNull MybatisProperties mybatisProperties) {
        return new SensitiveFieldDecryptIntercepter(mybatisProperties.getSensitiveKey());
    }

    /**
     * 脱敏插件-编码
     *
     * @param mybatisProperties mybatis properties
     * @return the sensitive field encrypt intercepter
     * @since 1.5.0
     */
    @Bean
    @ConditionalOnMissingBean(SensitiveFieldEncryptIntercepter.class)
    @ConditionalOnProperty(value = ConfigKey.MybatisConfigKey.MYBATIS_ENABLE_SENSITIVE,
                           havingValue = ConfigDefaultValue.TRUE_STRING,
                           matchIfMissing = true)
    public SensitiveFieldEncryptIntercepter sensitiveFieldEncryptIntercepter(@NotNull MybatisProperties mybatisProperties) {
        SqlUtils.setSensitiveKey(mybatisProperties.getSensitiveKey());
        return new SensitiveFieldEncryptIntercepter(mybatisProperties.getSensitiveKey());
    }

    /**
     * 使用 MybatisEnumTypeHandler 代替默认的 EnumTypeHandler, 实现 EntityEnum 子类的类型转换(数据库存 value, 返回 Entity)
     *
     * @return the configuration customizer
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(MybatisEnumTypeHandler.class)
    public ConfigurationCustomizer configurationCustomizer() {
        // 通用枚举转换器
        return configuration -> configuration.setDefaultEnumTypeHandler(GeneralEnumTypeHandler.class);
    }

    /**
     * id 类型转换
     *
     * @return the configuration customizer
     * @since 1.0.0
     */
    @Bean
    public ConfigurationCustomizer idTypeHandlerCustomizer() {
        // id 转换器
        return configuration -> configuration.getTypeHandlerRegistry().register(new SerializableIdTypeHandler(Serializable.class));
    }

    /**
     * Mybatis spark component bean
     *
     * @return the object provider
     * @since 1.7.1
     */
    @Bean
    @ConditionalOnProperty(name = ConfigKey.DruidConfigKey.DRIVER_CLASS, havingValue = "com.mysql.cj.jdbc.Driver", matchIfMissing = true)
    @ConditionalOnClass(name = "com.p6spy.engine.spy.P6SpyDriver")
    public ObjectProvider<SparkComponentBean> mybatisSparkComponentBean() {
        log.warn("classpath 存在 p6spy 但是未使用, 忽略加载 PerformanceInterceptor, "
                 + "如不使用 p6spy 请删除相关依赖, 否则请正确配置 p6spy");
        return null;
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2021.04.07 21:01
     * @since 1.8.0
     */
    @Configuration(proxyBeanMethods = false)
    static class MetaObjectAutoConfiguration implements SparkAutoConfiguration {
        /**
         * 自动创建时间和更新时间
         *
         * @param chains chains
         * @return global config
         * @since 1.0.0
         */
        @Bean
        @ConditionalOnMissingBean(MetaHandlerChain.class)
        public MetaObjectHandler metaHandlerChain(List<MetaObjectChain> chains) {
            return new MetaHandlerChain(chains);
        }

        /**
         * Time meta handler
         *
         * @return the meta object chain
         * @since 1.8.0
         */
        @Bean
        @ConditionalOnMissingBean(name = "timeMetaObjectHandler")
        public MetaObjectChain timeMetaObjectHandler() {
            return new TimeMetaObjectHandler();
        }

        /**
         * Tenant meta handler
         *
         * @return the meta object chain
         * @since 1.8.0
         */
        @Bean
        @ConditionalOnMissingBean(name = "tenantMetaObjectHandler")
        public MetaObjectChain tenantMetaObjectHandler() {
            return new TenantIdMetaObjectHandler();
        }

        /**
         * Client id meta object handler
         *
         * @return the meta object chain
         * @since 1.8.0
         */
        @Bean
        @ConditionalOnMissingBean(name = "clientMetaObjectHandler")
        public MetaObjectChain clientMetaObjectHandler() {
            return new ClientIdMetIdaObjectHandler();
        }

    }

    /**
     * Gets library type *
     *
     * @return the library type
     * @since 1.0.0
     */
    @Override
    public LibraryEnum getLibraryType() {
        return LibraryEnum.DRUID;
    }

}
