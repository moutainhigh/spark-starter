/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package info.spark.starter.cloud.autoconfigure.nacos.registry;

import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.discovery.NacosDiscoveryAutoConfiguration;
import com.alibaba.cloud.nacos.registry.NacosAutoServiceRegistration;
import com.alibaba.cloud.nacos.registry.NacosRegistration;
import com.alibaba.cloud.nacos.registry.NacosRegistrationCustomizer;
import com.alibaba.cloud.nacos.registry.NacosServiceRegistryAutoConfiguration;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationAutoConfiguration;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationConfiguration;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

/**
 * <p>Description: 官方不解决就自己修复: https://github.com/alibaba/spring-cloud-alibaba/issues/1723 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.21 21:59
 * @since 2022.1.1
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties
@ConditionalOnNacosDiscoveryEnabled
@ConditionalOnProperty(value = "spring.cloud.service-registry.auto-registration.enabled",
                       matchIfMissing = true)
@AutoConfigureAfter(value = {AutoServiceRegistrationConfiguration.class,
                             AutoServiceRegistrationAutoConfiguration.class,
                             NacosDiscoveryAutoConfiguration.class})
@AutoConfigureBefore(NacosServiceRegistryAutoConfiguration.class)
public class CustomNacosServiceRegistryAutoConfiguration {

    /**
     * Nacos service registry
     *
     * @param nacosServiceManager      nacos service manager
     * @param nacosDiscoveryProperties nacos discovery properties
     * @return the nacos service registry
     * @since 2022.1.1
     */
    @Bean
    @Primary
    public CustomNacosServiceRegistry nacosServiceRegistry(NacosServiceManager nacosServiceManager,
                                                           NacosDiscoveryProperties nacosDiscoveryProperties) {
        return new CustomNacosServiceRegistry(nacosServiceManager, nacosDiscoveryProperties);
    }

    /**
     * Nacos registration
     *
     * @param registrationCustomizers  registration customizers
     * @param nacosDiscoveryProperties nacos discovery properties
     * @param context                  context
     * @return the nacos registration
     * @since 2022.1.1
     */
    @Bean
    @ConditionalOnBean(AutoServiceRegistrationProperties.class)
    public NacosRegistration nacosRegistration(
        ObjectProvider<List<NacosRegistrationCustomizer>> registrationCustomizers,
        NacosDiscoveryProperties nacosDiscoveryProperties,
        ApplicationContext context) {
        return new NacosRegistration(registrationCustomizers.getIfAvailable(),
                                     nacosDiscoveryProperties, context);
    }

    /**
     * Nacos auto service registration
     *
     * @param nacosServiceRegistry              nacos service registry
     * @param autoServiceRegistrationProperties auto service registration properties
     * @param registration                      registration
     * @return the nacos auto service registration
     * @since 2022.1.1
     */
    @Bean
    @ConditionalOnBean(AutoServiceRegistrationProperties.class)
    public NacosAutoServiceRegistration nacosAutoServiceRegistration(
        CustomNacosServiceRegistry nacosServiceRegistry,
        AutoServiceRegistrationProperties autoServiceRegistrationProperties,
        NacosRegistration registration) {
        return new NacosAutoServiceRegistration(nacosServiceRegistry,
                                                autoServiceRegistrationProperties, registration);
    }

}
