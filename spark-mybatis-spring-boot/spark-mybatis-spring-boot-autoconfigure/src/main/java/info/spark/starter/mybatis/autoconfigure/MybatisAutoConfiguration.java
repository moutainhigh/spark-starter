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
 * <p>Description: mybatis ??????????????? </p>
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
     * ?????? SQL ???????????????: SQL ????????????
     * 1.?????????????????????,?????? left jion ????????????,????????????????????????
     * ????????????????????????:
     * 1.1 ?????????????????? SQL,bug ?????? update ??? where ??????????????????,???????????????????????????
     * 1.2 ??????????????????????????????,SQL ????????????????????????
     * <p>
     * 2.SQL ??????????????????,????????? left jion ?????????,?????????????????????????????? SQL ??????,??????????????????,??? left jion ?????????,????????????????????????????????? SQL,??? leader ??????????????????
     * http://gaoxianglong.github.io/shark/
     * SQL ???????????????????????????:
     * 2.1 ?????????????????????????????????????????????;
     * 2.2 ???????????????;  (???????????????????????????)
     * 2.3 ??????????????????;
     * <p>
     * 2.????????????????????????
     * 3.where ????????????
     * 4.where ???????????????: !=
     * 5.where ???????????????: not ?????????
     * 6.where ???????????????: or ?????????
     * 7.where ???????????????: ?????????
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
     * SQL??????????????????, ????????????????????????,???????????????????????????.
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
     * ????????????, ?????????????????????, mybatis-plus ????????????
     *
     * @param mybatisProperties mybatis properties
     * @return the pagination interceptor
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(PaginationInterceptor.class)
    public PaginationInterceptor paginationInterceptor(@NotNull MybatisProperties mybatisProperties) {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // ??????????????????????????? (spark.mybatis.single-page-limit)
        paginationInterceptor.setLimit(mybatisProperties.getSinglePageLimit());
        return paginationInterceptor;
    }

    /**
     * sql ??????
     *
     * @return the sql injector
     * @since 1.0.0
     */
    @Bean
    public ISqlInjector sqlInjector() {
        return new MybatisSqlInjector();
    }

    /**
     * mybatis-plus SQL?????????????????? (????????????????????????)
     * ????????? com.p6spy.engine.spy.P6SpyDriver ??????????????????.
     *
     * @param mybatisProperties ?????????
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
     * ????????????-??????
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
     * ????????????-??????
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
     * ?????? MybatisEnumTypeHandler ??????????????? EnumTypeHandler, ?????? EntityEnum ?????????????????????(???????????? value, ?????? Entity)
     *
     * @return the configuration customizer
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(MybatisEnumTypeHandler.class)
    public ConfigurationCustomizer configurationCustomizer() {
        // ?????????????????????
        return configuration -> configuration.setDefaultEnumTypeHandler(GeneralEnumTypeHandler.class);
    }

    /**
     * id ????????????
     *
     * @return the configuration customizer
     * @since 1.0.0
     */
    @Bean
    public ConfigurationCustomizer idTypeHandlerCustomizer() {
        // id ?????????
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
        log.warn("classpath ?????? p6spy ???????????????, ???????????? PerformanceInterceptor, "
                 + "???????????? p6spy ?????????????????????, ????????????????????? p6spy");
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
         * ?????????????????????????????????
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
