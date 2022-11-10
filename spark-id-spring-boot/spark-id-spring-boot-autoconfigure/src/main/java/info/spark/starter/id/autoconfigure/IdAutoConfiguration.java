package info.spark.starter.id.autoconfigure;

import info.spark.starter.common.start.SparkAutoConfiguration;
import info.spark.starter.id.enums.ProviderType;
import info.spark.starter.id.provider.DbMachineIdProvider;
import info.spark.starter.id.provider.IpConfigurableMachineIdProvider;
import info.spark.starter.id.provider.MachineIdProvider;
import info.spark.starter.id.provider.PropertyMachineIdProvider;
import info.spark.starter.id.service.IdService;
import info.spark.starter.id.service.IdServiceImpl;
import info.spark.starter.id.service.SnowflakeIdServiceImpl;
import info.spark.starter.util.StringUtils;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.beans.PropertyVetoException;
import java.util.concurrent.ThreadLocalRandom;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.04 22:32
 * @since 1.5.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(IdProperties.class)
public class IdAutoConfiguration implements SparkAutoConfiguration {

    /**
     * Property id service
     *
     * @param idProperties id properties
     * @return the id service
     * @since 1.5.0
     */
    @Bean
    @Conditional(value = PropertyTypeCondition.class)
    public IdService propertyIdService(@NotNull IdProperties idProperties) {
        log.info("Construct Property IdService machineId [{}]", idProperties.getMachineId());

        PropertyMachineIdProvider propertyMachineIdProvider = new PropertyMachineIdProvider();
        propertyMachineIdProvider.setMachineId(idProperties.getMachineId());
        return this.buildIdService(idProperties, propertyMachineIdProvider);
    }

    /**
     * Construct ip configurable id service
     *
     * @param idProperties id properties
     * @return the id service
     * @since 1.5.0
     */
    @Bean
    @Conditional(value = IpConfigurableCondition.class)
    public @NotNull IdService ipConfigurableIdService(@NotNull IdProperties idProperties) {
        log.info("Construct Ip Configurable IdService ips [{}]", idProperties.getIps());

        IpConfigurableMachineIdProvider ipConfigurableMachineIdProvider = new IpConfigurableMachineIdProvider(idProperties.getIps());
        return this.buildIdService(idProperties, ipConfigurableMachineIdProvider);
    }

    /**
     * Snowflake id service
     *
     * @param idProperties id properties
     * @return the id service
     * @since 1.5.0
     */
    @Bean
    @Conditional(value = SnowFlakeCondition.class)
    public @NotNull IdService snowflakeIdService(@NotNull IdProperties idProperties) {
        log.info("Construct SnowflakeIdServiceImpl [{}]", idProperties.getMachineId());
        // 这里需要转化一下, 雪花算法使用的机器 id 不能超过 32
        return new SnowflakeIdServiceImpl(idProperties.getMachineId() % 32,
                                          ThreadLocalRandom.current().nextInt(32));
    }

    /**
     * Construct db id service
     *
     * @param idProperties id properties
     * @return the id service
     * @since 1.5.0
     */
    @Bean
    @Conditional(value = DbCondition.class)
    public @NotNull IdService dbIdService(@NotNull IdProperties idProperties) {
        log.info("Construct Db IdService dbUrl [{}] dbName [{}] dbUser [{}] dbPassword [{}]",
                 idProperties.getDbUrl(),
                 idProperties.getDbName(),
                 idProperties.getDbUser(),
                 idProperties.getDbPassword());

        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();

        String jdbcDriver = "com.mysql.jdbc.Driver";
        try {
            comboPooledDataSource.setDriverClass(jdbcDriver);
        } catch (PropertyVetoException e) {
            log.error("Wrong JDBC driver [{}]", jdbcDriver);
            log.error("Wrong JDBC driver error: ", e);
            throw new IllegalStateException("Wrong JDBC driver ", e);
        }

        comboPooledDataSource.setMinPoolSize(5);
        comboPooledDataSource.setMaxPoolSize(30);
        comboPooledDataSource.setIdleConnectionTestPeriod(20);
        comboPooledDataSource.setMaxIdleTime(25);
        comboPooledDataSource.setBreakAfterAcquireFailure(false);
        comboPooledDataSource.setCheckoutTimeout(3000);
        comboPooledDataSource.setAcquireRetryAttempts(50);
        comboPooledDataSource.setAcquireRetryDelay(1000);

        String url = StringUtils.format("jdbc:mysql://{}/{}?useUnicode=true&amp;characterEncoding=UTF-8&amp;autoReconnect=true",
                                        idProperties.getDbUrl(),
                                        idProperties.getDbName());

        comboPooledDataSource.setJdbcUrl(url);
        comboPooledDataSource.setUser(idProperties.getDbUser());
        comboPooledDataSource.setPassword(idProperties.getDbPassword());

        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setLazyInit(false);
        jdbcTemplate.setDataSource(comboPooledDataSource);

        DbMachineIdProvider dbMachineIdProvider = new DbMachineIdProvider();
        dbMachineIdProvider.setJdbcTemplate(jdbcTemplate);
        dbMachineIdProvider.init();


        return this.buildIdService(idProperties, dbMachineIdProvider);
    }

    /**
     * Build id service
     *
     * @param idProperties      id properties
     * @param machineIdProvider machine id provider
     * @return the id service
     * @since 1.5.0
     */
    @NotNull
    private IdServiceImpl buildIdService(@NotNull IdProperties idProperties, MachineIdProvider machineIdProvider) {
        IdServiceImpl idServiceImpl = new IdServiceImpl();
        idServiceImpl.setMachineIdProvider(machineIdProvider);

        if (idProperties.getDeployType() != null) {
            idServiceImpl.setDeployType(idProperties.getDeployType().getType());
        }
        if (idProperties.getIdType() != null) {
            idServiceImpl.setIdTypeValue(idProperties.getIdType().getVaule());
        }
        if (idProperties.getVersion() != null) {
            idServiceImpl.setVersion(idProperties.getVersion());
        }

        idServiceImpl.init();
        return idServiceImpl;
    }

    /**
         *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.02.21 19:59
     * @since 1.5.0
     */
    private static class PropertyTypeCondition extends ProviderTypeCondition {

        /**
         * Aborted captcha condition
         *
         * @since 1.5.0
         */
        PropertyTypeCondition() {
            super(ProviderType.PROPERTY);
        }
    }

    /**
         *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.02.21 19:59
     * @since 1.5.0
     */
    private static class IpConfigurableCondition extends ProviderTypeCondition {

        /**
         * Frequency captcha condition
         *
         * @since 1.5.0
         */
        IpConfigurableCondition() {
            super(ProviderType.IP_CONFIGURABLE);
        }
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.06.24 17:24
     * @since 1.5.0
     */
    private static class DbCondition extends ProviderTypeCondition {

        /**
         * Frequency captcha condition
         *
         * @since 1.5.0
         */
        DbCondition() {
            super(ProviderType.DB);
        }
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.06.29 16:57
     * @since 1.5.0
     */
    private static class SnowFlakeCondition extends ProviderTypeCondition {

        /**
         * Frequency captcha condition
         *
         * @since 1.5.0
         */
        SnowFlakeCondition() {
            super(ProviderType.SNOW_FLAKE);
        }
    }
}
