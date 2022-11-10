package info.spark.starter.cloud.autoconfigure.nacos;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.Service;
import com.alibaba.nacos.api.selector.AbstractSelector;
import com.alibaba.nacos.api.selector.ExpressionSelector;
import com.alibaba.nacos.api.selector.NoneSelector;
import com.alibaba.nacos.client.naming.NacosNamingMaintainService;
import com.alibaba.nacos.client.naming.core.ServerListManager;
import com.alibaba.nacos.client.naming.remote.http.NamingHttpClientManager;
import com.alibaba.nacos.client.naming.remote.http.NamingHttpClientProxy;
import com.alibaba.nacos.client.naming.utils.InitUtils;
import com.alibaba.nacos.client.security.SecurityProxy;
import com.alibaba.nacos.client.utils.ValidatorUtils;
import com.alibaba.nacos.common.utils.ThreadUtils;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.alibaba.nacos.client.utils.LogUtils.NAMING_LOGGER;


/**
 * <p>Description: 解决 {@link NacosNamingMaintainService} 不能自动登录的问题, 自定义以实现更新元数据 403 问题  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.16 19:54
 * @since 2022.1.1
 */
@SuppressWarnings("all")
public class CustomNacosNamingMaintainService implements NamingMaintainService {

    /** Namespace */
    private String namespace;
    /** Server proxy */
    private NamingHttpClientProxy serverProxy;
    /** Security proxy */
    private SecurityProxy securityProxy;
    /** Executor service */
    private ScheduledExecutorService executorService;
    /** Server list manager */
    private ServerListManager serverListManager;

    /**
     * Custom nacos naming maintain service
     *
     * @param serverList server list
     * @throws NacosException nacos exception
     * @since 2022.1.1
     */
    public CustomNacosNamingMaintainService(String serverList) throws NacosException {
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.SERVER_ADDR, serverList);
        init(properties);
    }

    /**
     * Custom nacos naming maintain service
     *
     * @param properties properties
     * @throws NacosException nacos exception
     * @since 2022.1.1
     */
    public CustomNacosNamingMaintainService(Properties properties) throws NacosException {
        init(properties);
    }

    /**
     * Init
     *
     * @param properties properties
     * @throws NacosException nacos exception
     * @since 2022.1.1
     */
    private void init(Properties properties) throws NacosException {
        ValidatorUtils.checkInitParam(properties);
        namespace = InitUtils.initNamespaceForNaming(properties);
        InitUtils.initSerialization();
        InitUtils.initWebRootContext(properties);
        serverListManager = new ServerListManager(properties, namespace);

        this.serverListManager = new ServerListManager(properties, namespace);
        securityProxy = new SecurityProxy(properties,
                                          NamingHttpClientManager.getInstance().getNacosRestTemplate());
        initSecurityProxy();

        serverProxy = new NamingHttpClientProxy(namespace, securityProxy, serverListManager, properties, null);
    }

    /**
     * Update instance
     *
     * @param serviceName service name
     * @param instance    instance
     * @throws NacosException nacos exception
     * @since 2022.1.1
     */
    @Override
    public void updateInstance(String serviceName, Instance instance) throws NacosException {
        updateInstance(serviceName, Constants.DEFAULT_GROUP, instance);
    }

    /**
     * Update instance
     *
     * @param serviceName service name
     * @param groupName   group name
     * @param instance    instance
     * @throws NacosException nacos exception
     * @since 2022.1.1
     */
    @Override
    public void updateInstance(String serviceName, String groupName, Instance instance) throws NacosException {
        serverProxy.updateInstance(serviceName, groupName, instance);
    }

    /**
     * Query service
     *
     * @param serviceName service name
     * @return the service
     * @throws NacosException nacos exception
     * @since 2022.1.1
     */
    @Override
    public Service queryService(String serviceName) throws NacosException {
        return queryService(serviceName, Constants.DEFAULT_GROUP);
    }

    /**
     * Query service
     *
     * @param serviceName service name
     * @param groupName   group name
     * @return the service
     * @throws NacosException nacos exception
     * @since 2022.1.1
     */
    @Override
    public Service queryService(String serviceName, String groupName) throws NacosException {
        return serverProxy.queryService(serviceName, groupName);
    }

    /**
     * Create service
     *
     * @param serviceName service name
     * @throws NacosException nacos exception
     * @since 2022.1.1
     */
    @Override
    public void createService(String serviceName) throws NacosException {
        createService(serviceName, Constants.DEFAULT_GROUP);
    }

    /**
     * Create service
     *
     * @param serviceName service name
     * @param groupName   group name
     * @throws NacosException nacos exception
     * @since 2022.1.1
     */
    @Override
    public void createService(String serviceName, String groupName) throws NacosException {
        createService(serviceName, groupName, Constants.DEFAULT_PROTECT_THRESHOLD);
    }

    /**
     * Create service
     *
     * @param serviceName      service name
     * @param groupName        group name
     * @param protectThreshold protect threshold
     * @throws NacosException nacos exception
     * @since 2022.1.1
     */
    @Override
    public void createService(String serviceName, String groupName, float protectThreshold) throws NacosException {
        Service service = new Service();
        service.setName(serviceName);
        service.setGroupName(groupName);
        service.setProtectThreshold(protectThreshold);

        createService(service, new NoneSelector());
    }

    /**
     * Create service
     *
     * @param serviceName      service name
     * @param groupName        group name
     * @param protectThreshold protect threshold
     * @param expression       expression
     * @throws NacosException nacos exception
     * @since 2022.1.1
     */
    @Override
    public void createService(String serviceName, String groupName, float protectThreshold, String expression)
        throws NacosException {
        Service service = new Service();
        service.setName(serviceName);
        service.setGroupName(groupName);
        service.setProtectThreshold(protectThreshold);

        ExpressionSelector selector = new ExpressionSelector();
        selector.setExpression(expression);

        createService(service, selector);
    }

    /**
     * Create service
     *
     * @param service  service
     * @param selector selector
     * @throws NacosException nacos exception
     * @since 2022.1.1
     */
    @Override
    public void createService(Service service, AbstractSelector selector) throws NacosException {
        serverProxy.createService(service, selector);
    }

    /**
     * Delete service
     *
     * @param serviceName service name
     * @return the boolean
     * @throws NacosException nacos exception
     * @since 2022.1.1
     */
    @Override
    public boolean deleteService(String serviceName) throws NacosException {
        return deleteService(serviceName, Constants.DEFAULT_GROUP);
    }

    /**
     * Delete service
     *
     * @param serviceName service name
     * @param groupName   group name
     * @return the boolean
     * @throws NacosException nacos exception
     * @since 2022.1.1
     */
    @Override
    public boolean deleteService(String serviceName, String groupName) throws NacosException {
        return serverProxy.deleteService(serviceName, groupName);
    }

    /**
     * Update service
     *
     * @param serviceName      service name
     * @param groupName        group name
     * @param protectThreshold protect threshold
     * @throws NacosException nacos exception
     * @since 2022.1.1
     */
    @Override
    public void updateService(String serviceName, String groupName, float protectThreshold) throws NacosException {
        Service service = new Service();
        service.setName(serviceName);
        service.setGroupName(groupName);
        service.setProtectThreshold(protectThreshold);

        updateService(service, new NoneSelector());
    }

    /**
     * Update service
     *
     * @param serviceName      service name
     * @param groupName        group name
     * @param protectThreshold protect threshold
     * @param metadata         metadata
     * @throws NacosException nacos exception
     * @since 2022.1.1
     */
    @Override
    public void updateService(String serviceName, String groupName, float protectThreshold,
                              Map<String, String> metadata) throws NacosException {
        Service service = new Service();
        service.setName(serviceName);
        service.setGroupName(groupName);
        service.setProtectThreshold(protectThreshold);
        service.setMetadata(metadata);

        updateService(service, new NoneSelector());
    }

    /**
     * Update service
     *
     * @param service  service
     * @param selector selector
     * @throws NacosException nacos exception
     * @since 2022.1.1
     */
    @Override
    public void updateService(Service service, AbstractSelector selector) throws NacosException {
        serverProxy.updateService(service, selector);
    }

    /**
     * Shut down
     *
     * @throws NacosException nacos exception
     * @since 2022.1.1
     */
    @Override
    public void shutDown() throws NacosException {
        serverProxy.shutdown();
        ThreadUtils.shutdownThreadPool(executorService, NAMING_LOGGER);
    }

    /**
     * Init security proxy
     *
     * @since 2022.1.1
     */
    private void initSecurityProxy() {
        this.executorService = new ScheduledThreadPoolExecutor(1, r -> {
            Thread t = new Thread(r);
            t.setName("com.alibaba.nacos.client.naming.security");
            t.setDaemon(true);
            return t;
        });
        this.securityProxy.login(serverListManager.getServerList());
        this.executorService.scheduleWithFixedDelay(() -> securityProxy.login(serverListManager.getServerList()), 0,
                                                    30000, TimeUnit.MILLISECONDS);
    }
}
