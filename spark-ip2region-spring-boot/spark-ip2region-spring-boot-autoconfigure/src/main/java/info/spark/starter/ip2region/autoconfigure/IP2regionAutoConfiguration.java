package info.spark.starter.ip2region.autoconfigure;

import info.spark.starter.ip2region.IP2regionTemplate;

import org.jetbrains.annotations.NotNull;
import org.nutz.plugins.ip2region.DBReader;
import org.nutz.plugins.ip2region.DbConfig;
import org.nutz.plugins.ip2region.DbMakerConfigException;
import org.nutz.plugins.ip2region.DbSearcher;
import org.nutz.plugins.ip2region.impl.ByteArrayDBReader;
import org.nutz.plugins.ip2region.impl.RandomAccessFileDBReader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.12.05 16:25
 * @since 1.7.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(DbSearcher.class)
@EnableConfigurationProperties(value = {IP2regionProperties.class})
public class IP2regionAutoConfiguration implements ResourceLoaderAware {

    /** Resource loader */
    private ResourceLoader resourceLoader;

    /**
     * Ip 2 region template
     *
     * @param properties properties
     * @return the ip 2 region template
     * @throws IOException            io exception
     * @throws DbMakerConfigException db maker config exception
     * @since 1.7.0
     */
    @Bean
    public IP2regionTemplate ip2regionTemplate(IP2regionProperties properties) throws IOException, DbMakerConfigException {

        DbSearcher dbSearcher;
        if (properties.isExternal()) {

            DBReader reader;
            // 查找resource
            Resource resource = this.resourceLoader.getResource(properties.getLocation());

            if (resource.isFile() && resource.exists()) {
                reader = new RandomAccessFileDBReader(new RandomAccessFile(resource.getFile(), "r"));
            } else {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                FileCopyUtils.copy(resource.getInputStream(), output);
                reader = new ByteArrayDBReader(output.toByteArray());
            }

            DbConfig dbConfig = new DbConfig(properties.getTotalHeaderSize());
            dbConfig.setIndexBlockSize(properties.getIndexBlockSize());
            dbSearcher = new DbSearcher(dbConfig, reader);
        } else {
            dbSearcher = new DbSearcher();
        }

        return new IP2regionTemplate(dbSearcher);
    }

    /**
     * Sets resource loader *
     *
     * @param resourceLoader resource loader
     * @since 1.7.0
     */
    @Override
    public void setResourceLoader(@NotNull ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

}
