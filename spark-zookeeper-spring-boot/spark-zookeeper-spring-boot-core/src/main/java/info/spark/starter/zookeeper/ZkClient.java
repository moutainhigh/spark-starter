package info.spark.starter.zookeeper;

import info.spark.starter.util.DateUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLBackgroundPathAndBytesable;
import org.apache.curator.framework.api.BackgroundPathable;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: todo-dong4j : (2021.01.29 17:05) [合并到 ZookeeperService] </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.29 16:52
 * @since 1.8.0
 */
@Slf4j
@SuppressWarnings("checkstyle:MethodLimit")
public class ZkClient {
    /** Time out */
    private int timeOut;
    /** Auth schema */
    private String authSchema;
    /** Auth info */
    private String authInfo;
    /** Client */
    private final CuratorFramework client;

    /**
     * Instantiates a new Zk client.
     *
     * @param client the client
     * @since 1.8.0
     */
    public ZkClient(CuratorFramework client) {
        this.client = client;
    }

    /**
     * Instantiates a new Zk client.
     *
     * @param zkAddr the zk addr
     * @since 1.8.0
     */
    public ZkClient(String zkAddr) {
        this(zkAddr, null);
    }

    /**
     * Instantiates a new Zk client.
     *
     * @param zkAddr    the zk addr
     * @param namespace the namespace
     * @since 1.8.0
     */
    public ZkClient(String zkAddr, String namespace) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        this.client = CuratorFrameworkFactory.builder()
            .connectString(zkAddr).namespace(StringUtils.isEmpty(namespace) ? "" : namespace)
            .sessionTimeoutMs(5000)
            .connectionTimeoutMs(5000)
            .retryPolicy(retryPolicy)
            .build();
        this.client.start();
    }

    /**
     * Instantiates a new Zk client.
     *
     * @param zkAddr    the zk addr
     * @param timeOut   the time out
     * @param namespace the namespace
     * @throws Exception the exception
     * @since 1.8.0
     */
    public ZkClient(String zkAddr, int timeOut, String namespace) throws Exception {
        this(zkAddr, timeOut, namespace, null);
    }

    /**
     * 获取zk 连接客户端
     *
     * @param zkAddr    zk地址 ip:port,ip:port,ip:port
     * @param timeOut   连接超时ms
     * @param namespace 所有的操作都是在 /namespace 下的节点操作
     * @param acl       Access Control List（访问控制列表）。Znode被创建时带有一个ACL列表<br>
     *                  acl 主要由三个维度：schema,id,permision 控制节点权限 <br>
     *                  eg:<br>
     *                  Id id = new Id("digest", DigestAuthenticationProvider.generateDigest("username:password"));<br>
     *                  ACL acl = new ACL(ZooDefs.Perms.ALL, id); <br>
     *                  <br>
     *                  维度 schema: <br>
     *                  1：digest 用户名+密码验证 它对应的维度id=username:BASE64(SHA1(password))<br>
     *                  2：host 客户端主机名hostname验证 <br>
     *                  3：ip 它对应的维度id=客户机的IP地址，设置的时候可以设置一个ip段，比如ip:192.168.1.0/16, 表示匹配前16个bit的IP段<br>
     *                  4：auth 使用sessionID验证 <br>
     *                  5：world 无验证，默认是无任何权限  它下面只有一个id, 叫anyone  <br>
     *                  6：super: 在这种scheme情况下，对应的id拥有超级权限，可以做任何事情(cdrwa)  <br>
     *                  7：sasl: sasl的对应的id，是一个通过了kerberos认证的用户id  <br>
     *                  <br>
     *                  维度：permision <br>
     *                  ZooDefs.Perms.READ 读权限<br>
     *                  ZooDefs.Perms.WRITE 写权限<br>
     *                  ZooDefs.Perms.CREATE 创建节点权限<br>
     *                  ZooDefs.Perms.DELETE 删除节点权限<br>
     *                  ZooDefs.Perms.ADMIN 能设置权限<br>
     *                  ZooDefs.Perms.ALL 所有权限<br>
     *                  ALL = READ | WRITE | CREATE | DELETE | ADMIN<br>
     * @throws Exception exception
     * @since 1.8.0
     */
    public ZkClient(String zkAddr, int timeOut, String namespace, ACL acl) throws Exception {
        if (timeOut > 0) {
            this.timeOut = timeOut;
        }
        if (null != acl) {
            this.authSchema = acl.getId().getScheme();
            this.authInfo = acl.getId().getId();
        }
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory
            .builder().connectString(zkAddr).namespace(StringUtils.isEmpty(namespace) ? "" : namespace)
            .connectionTimeoutMs(this.timeOut)
            .retryPolicy(new RetryNTimes(5, 10));
        if ((!StringUtils.isBlank(this.authSchema))
            && (!StringUtils.isBlank(this.authInfo))) {
            builder.authorization(this.authSchema, this.authInfo.getBytes());
        }
        this.client = builder.build();
        this.client.start();
        this.client.blockUntilConnected(5, TimeUnit.SECONDS);
    }

    /**
     * Get curator client curator framework.
     *
     * @return the curator framework
     * @since 1.8.0
     */
    public CuratorFramework getCuratorClient() {
        return this.client;
    }

    /**
     * 创建一个所有权限的永久节点, 默认当不父节点不能存在时, 创建父节点
     *
     * @param nodePath the node path
     * @param data     the data
     * @since 1.8.0
     */
    public void createPersitentNode(String nodePath, String data) {
        this.createNode(nodePath, data, CreateMode.PERSISTENT, true);
    }

    /**
     * Async create persitent node.
     * 异步创建永久节点
     *
     * @param nodePath the node path
     * @param data     the data
     * @throws Exception the exception
     * @since 1.8.0
     */
    public void asyncCreatePersitentNode(String nodePath, String data) throws Exception {
        if (this.exists(nodePath) != null) {
            log.error("path = {} is exists", nodePath);
            return;
        }
        this.client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
            .inBackground((client, event) -> {
                log.error("event[code: {} ,type: {} ]", event.getPath(), event.getType());
                log.error("Thread of processResult: {}", Thread.currentThread().getName());
            }).forPath(nodePath, data.getBytes());
    }

    /**
     * 创建一个所有权限的永久节点
     *
     * @param nodePath  the node path
     * @param data      the data
     * @param recursion 当父目录不存在是否创建 true:创建，fasle:不创建
     * @since 1.8.0
     */
    public void createPersitentNode(String nodePath, String data, boolean recursion) {
        this.createNode(nodePath, data, CreateMode.PERSISTENT, recursion);
    }

    /**
     * 创建一个所有权限节点即schema:world;id:annyone;permision:ZooDefs.Perms.ALL
     *
     * @param nodePath   创建的结点路径
     * @param data       节点数据
     * @param createMode 节点模式
     * @param recursion  当父目录不存在是否创建 true:创建，fasle:不创建
     * @since 1.8.0
     */
    public void createNode(String nodePath, String data, CreateMode createMode, boolean recursion) {
        this.createNode(nodePath, ZooDefs.Ids.OPEN_ACL_UNSAFE, data, createMode, recursion);
    }


    /**
     * 创建节点
     *
     * @param nodePath   创建节点的路径
     * @param acls       节点控制权限列表
     * @param data       节点存放的数据
     * @param createMode 创建节点的模式
     * @param recursion  当父目录不存在是否创建 true:创建，fasle:不创建
     *                   节点模式CreateMode<br>
     *                   1：CreateMode.EPHEMERAL 创建临时节点；该节点在客户端掉线的时候被删除<br>
     *                   2：CreateMode.EPHEMERAL_SEQUENTIAL  临时自动编号节点，一旦创建这个节点的客户端与服务器端口也就是session
     *                   超时，这种节点会被自动删除，并且根据当前已近存在的节点数自动加 1，然后返回给客户端已经成功创建的目录节点(可做分布式锁)<br>
     *                   3：CreateMode.PERSISTENT 持久化目录节点，存储的数据不会丢失。<br>
     *                   4：CreateMode.PERSISTENT_SEQUENTIAL  顺序自动编号的持久化目录节点，存储的数据不会丢失，并且根据当前已近存在的节点数自动加
     *                   1，然后返回给客户端已经成功创建的目录节点名<br>
     * @since 1.8.0
     */
    public void createNode(String nodePath, List<ACL> acls, String data,
                           CreateMode createMode, boolean recursion) {
        byte[] bytes = null;
        if (!StringUtils.isBlank(data)) {
            bytes = data.getBytes(StandardCharsets.UTF_8);
        }
        this.createNode(nodePath, acls, bytes, createMode, recursion);
    }

    /**
     * Create node.
     *
     * @param nodePath   创建节点的路径
     * @param acls       节点控制权限列表
     * @param data       节点存放的数据
     * @param createMode 创建节点的模式
     * @param recursion  当父目录不存在是否创建 true:创建，fasle:不创建
     *                   节点模式CreateMode<br>
     *                   1：CreateMode.EPHEMERAL 创建临时节点；该节点在客户端掉线的时候被删除<br>
     *                   2：CreateMode.EPHEMERAL_SEQUENTIAL  临时自动编号节点，一旦创建这个节点的客户端与服务器端口也就是session
     *                   超时，这种节点会被自动删除，并且根据当前已近存在的节点数自动加 1，然后返回给客户端已经成功创建的目录节点(可做分布式锁)<br>
     *                   3：CreateMode.PERSISTENT 持久化目录节点，存储的数据不会丢失。<br>
     *                   4：CreateMode.PERSISTENT_SEQUENTIAL  顺序自动编号的持久化目录节点，存储的数据不会丢失，并且根据当前已近存在的节点数自动加
     *                   1，然后返回给客户端已经成功创建的目录节点名<br>
     * @since 1.8.0
     */
    public void createNode(String nodePath, List<ACL> acls, byte[] data,
                           CreateMode createMode, boolean recursion) {
        if (this.exists(nodePath) != null) {
            log.error("path = {} is exists", nodePath);
            return;
        }
        try {
            if (recursion) {
                ((ACLBackgroundPathAndBytesable<?>) this.client
                    .create().creatingParentsIfNeeded().withMode(createMode))
                    .withACL(acls).forPath(nodePath, data);
            } else {
                ((ACLBackgroundPathAndBytesable<?>) this.client
                    .create().withMode(createMode))
                    .withACL(acls).forPath(nodePath, data);
            }
        } catch (Exception e) {
            log.error("create node:{} error", nodePath);
            throw new RuntimeException("create node error");
        }
    }

    /**
     * 创建一个所有权限的零时节点
     *
     * @param nodePath  the node path
     * @param data      the data
     * @param recursion 当父目录不存在是否创建 true:创建，fasle:不创建
     * @since 1.8.0
     */
    public void createEphemeralNode(String nodePath, String data, boolean recursion) {
        this.createNode(nodePath, data, CreateMode.EPHEMERAL, recursion);
    }

    /**
     * 创建一个带权限的永久节点
     *
     * @param nodePath  the node path
     * @param data      the data
     * @param acls      the acls
     * @param recursion 当父目录不存在是否创建 true:创建，fasle:不创建
     * @since 1.8.0
     */
    public void createPersitentNodeWithAcl(String nodePath, String data, List<ACL> acls, boolean recursion) {
        this.createNode(nodePath, acls, data, CreateMode.PERSISTENT, recursion);
    }

    /**
     * 创建一个带权限的零时节点
     *
     * @param nodePath  the node path
     * @param data      the data
     * @param acls      the acls
     * @param recursion 当父目录不存在是否创建 true:创建，fasle:不创建
     * @since 1.8.0
     */
    public void createEphemeralNodeAcl(String nodePath, String data, List<ACL> acls, boolean recursion) {
        this.createNode(nodePath, acls, data, CreateMode.EPHEMERAL, recursion);
    }


    /**
     * 创建序列节点且当父节点不存在时创建父节点
     *
     * @param nodePath   the node path
     * @param acls       可参考：ZooDefs.Ids
     * @param createMode the create mode
     * @param recursion  当父目录不存在是否创建 true:创建，fasle:不创建
     * @throws Exception the exception
     * @since 1.8.0
     */
    public void createSeqNode(String nodePath, List<ACL> acls, CreateMode createMode, boolean recursion) throws Exception {
        if (recursion) {
            ((ACLBackgroundPathAndBytesable<?>) this.client
                .create().creatingParentsIfNeeded()
                .withMode(createMode))
                .withACL(acls).forPath(nodePath);
        } else {
            ((ACLBackgroundPathAndBytesable<?>) this.client
                .create()
                .withMode(createMode))
                .withACL(acls).forPath(nodePath);
        }
    }

    /**
     * 存在返回 节点stat 信息；否则返回null
     *
     * @param path the path
     * @return stat stat
     * @since 1.8.0
     */
    public Stat exists(String path) {
        try {
            return this.client.checkExists().forPath(path);
        } catch (Exception e) {
            log.error("check node exist error", e);
        }
        return null;
    }

    /**
     * 判断节点是否存在，存在则注册节点监视器
     *
     * @param path    the path
     * @param watcher the watcher
     * @return boolean boolean
     * @throws Exception the exception
     * @since 1.8.0
     */
    public boolean exists(String path, Watcher watcher) throws Exception {
        if (null != watcher) {
            return null != ((BackgroundPathable<?>) this.client.checkExists().usingWatcher(watcher)).forPath(path);
        }
        return null != this.client.checkExists().forPath(path);
    }

    /**
     * 判断是否处于连接状态
     *
     * @return boolean boolean
     * @since 1.8.0
     */
    public boolean isConnected() {
        return (null != this.client)
               && (CuratorFrameworkState.STARTED.equals(this.client.getState()));
    }

    /**
     * Retry connection.
     *
     * @since 1.8.0
     */
    public void retryConnection() {
        this.client.start();
    }

    /**
     * 获取连接客户端
     *
     * @return inner client
     * @since 1.8.0
     */
    public CuratorFramework getInnerClient() {
        return this.client;

    }

    /**
     * 关闭连接
     *
     * @since 1.8.0
     */
    public void quit() {
        if ((null != this.client)
            && (CuratorFrameworkState.STARTED
            .equals(this.client.getState()))) {
            this.client.close();
        }
    }


    /**
     * 删除节点
     *
     * @param path         the path
     * @param deleChildren the dele children
     * @throws Exception the exception
     * @since 1.8.0
     */
    public void deleteNode(String path, boolean deleChildren) throws Exception {
        if (this.exists(path) == null) {
            log.error("path = {} is not exists", path);
            return;
        }
        if (deleChildren) {
            this.client.delete().guaranteed().deletingChildrenIfNeeded()
                .forPath(path);
        } else {
            this.client.delete().forPath(path);
        }
    }

    /**
     * 异步删除节点
     *
     * @param path         the path
     * @param deleChildren the dele children
     * @throws Exception the exception
     * @since 1.8.0
     */
    public void asyncDeleteNode(String path, boolean deleChildren) throws Exception {
        if (this.exists(path) == null) {
            log.error("path = {} is not exists", path);
            return;
        }
        if (deleChildren) {
            this.client.delete().guaranteed().deletingChildrenIfNeeded().inBackground((client, event) -> {
                log.error("event[code: {} ,type: {} ]", event.getPath(), event.getType());
                log.error("Thread of processResult: {}", Thread.currentThread().getName());
            }).forPath(path);
        } else {
            this.client.delete().forPath(path);
        }
    }

    /**
     * 设置节点数据
     *
     * @param nodePath the node path
     * @param data     the data
     * @throws Exception the exception
     * @since 1.8.0
     */
    public void setNodeData(String nodePath, String data) throws Exception {
        if (this.exists(nodePath) == null) {
            log.error("path = {}, is not exists", nodePath);
        }
        byte[] bytes = null;
        if (!StringUtils.isBlank(data)) {
            bytes = data.getBytes(StandardCharsets.UTF_8);
        }
        this.setNodeData(nodePath, bytes);
    }

    /**
     * 设置节点数据
     *
     * @param nodePath the node path
     * @param data     the data
     * @throws Exception the exception
     * @since 1.8.0
     */
    public void setNodeData(String nodePath, byte[] data) throws Exception {
        this.client.setData().forPath(nodePath, data);
    }

    /**
     * Gets node data.
     *
     * @param nodePath the node path
     * @param watch    the watch
     * @return the node data
     * @throws Exception the exception
     * @since 1.8.0
     */
    public String getNodeData(String nodePath, boolean watch) throws Exception {
        byte[] data;
        if (watch) {
            data = (byte[]) ((BackgroundPathable<?>) this.client.getData()
                .watched()).forPath(nodePath);
        } else {
            data = this.client.getData().forPath(nodePath);
        }
        if ((null == data) || (data.length <= 0)) {
            return null;
        }
        return new String(data, StandardCharsets.UTF_8);
    }

    /**
     * Gets node data.
     *
     * @param nodePath the node path
     * @return the node data
     * @since 1.8.0
     */
    public String getNodeData(String nodePath) {
        if (this.exists(nodePath) == null) {
            log.error("path = {} is not exists", nodePath);
            return "";
        }
        try {
            return this.getNodeData(nodePath, false);
        } catch (Exception e) {
            log.error("get node data error", e);
        }
        return "";
    }

    /**
     * Gets node data.
     *
     * @param nodePath the node path
     * @param watcher  the watcher
     * @return the node data
     * @throws Exception the exception
     * @since 1.8.0
     */
    public String getNodeData(String nodePath, Watcher watcher)
        throws Exception {
        byte[] data = this.getNodeBytes(nodePath, watcher);
        return new String(data, StandardCharsets.UTF_8);
    }

    /**
     * Get node bytes byte [ ].
     *
     * @param nodePath the node path
     * @param watcher  the watcher
     * @return the byte [ ]
     * @throws Exception the exception
     * @since 1.8.0
     */
    public byte[] getNodeBytes(String nodePath, Watcher watcher)
        throws Exception {
        byte[] bytes = null;
        if (null != watcher) {
            bytes = (byte[]) ((BackgroundPathable<?>) this.client.getData()
                .usingWatcher(watcher)).forPath(nodePath);
        } else {
            bytes = this.client.getData().forPath(nodePath);
        }
        return bytes;
    }

    /**
     * Get node bytes byte [ ].
     *
     * @param nodePath the node path
     * @return the byte [ ]
     * @throws Exception the exception
     * @since 1.8.0
     */
    public byte[] getNodeBytes(String nodePath) throws Exception {
        return this.getNodeBytes(nodePath, null);
    }


    /**
     * Gets children.
     *
     * @param nodePath the node path
     * @param watcher  the watcher
     * @return the children
     * @throws Exception the exception
     * @since 1.8.0
     */
    @SuppressWarnings("unchecked")
    public List<String> getChildren(String nodePath, Watcher watcher)
        throws Exception {
        return (List<String>) ((BackgroundPathable<?>) this.client
            .getChildren().usingWatcher(watcher)).forPath(nodePath);
    }

    /**
     * Gets children.
     *
     * @param path the path
     * @return the children
     * @since 1.8.0
     */
    public List<String> getChildren(String path) {
        if (this.exists(path) == null) {
            log.error("path = {} is not exists", path);
            return new ArrayList<>();
        }
        try {
            return this.client.getChildren().forPath(path);
        } catch (Exception e) {
            log.error("get children node error", e);
            return null;
        }
    }

    /**
     * Gets children.
     *
     * @param path    the path
     * @param watcher the watcher
     * @return the children
     * @throws Exception the exception
     * @since 1.8.0
     */
    @SuppressWarnings("unchecked")
    public List<String> getChildren(String path, boolean watcher)
        throws Exception {
        if (watcher) {
            return (List<String>) ((BackgroundPathable<?>) this.client
                .getChildren().watched()).forPath(path);
        }
        return this.client.getChildren().forPath(path);
    }


    /**
     * Add auth zk client.
     *
     * @param authSchema the auth schema
     * @param authInfo   the auth info
     * @return the zk client
     * @throws Exception the exception
     * @since 1.8.0
     */
    public ZkClient addAuth(String authSchema, String authInfo)
        throws Exception {
        synchronized (ZkClient.class) {
            this.client.getZookeeperClient().getZooKeeper()
                .addAuthInfo(authSchema, authInfo.getBytes());
        }
        return this;
    }

    /**
     * 分布式锁
     *
     * @param lockPath the lock path
     * @return inter process lock
     * @since 1.8.0
     */
    public InterProcessLock getInterProcessLock(String lockPath) {
        return new InterProcessMutex(this.client, lockPath);
    }

    /**
     * Close.
     *
     * @since 1.8.0
     */
    public void close() {
        this.client.close();
    }

    /**
     * Create symbol.
     * 创建临时节点, 标识应用是否正在运行, spark-managerconfig 启动后 监听此节点的父节点, 变化后做通知
     *
     * @param path the path
     * @return the boolean
     * @since 1.8.0
     */
    public boolean createSymbol(String path) {
        try {
            if (this.exists(path) != null) {
                // 存在节点就先删除在创建
                this.client.delete().forPath(path);
            }
            this.client.create().withMode(CreateMode.EPHEMERAL).forPath(path, DateUtils.formatDateTime(new Date()).getBytes());
            return true;
        } catch (Exception e) {
            log.error("create runing_symbol node error", e);
        }
        return false;
    }

}
