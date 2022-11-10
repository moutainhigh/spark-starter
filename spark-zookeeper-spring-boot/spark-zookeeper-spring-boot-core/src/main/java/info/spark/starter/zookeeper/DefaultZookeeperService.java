package info.spark.starter.zookeeper;

import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.zookeeper.CreateMode;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.29 16:27
 * @since 1.8.0
 */
@Slf4j
@SuppressWarnings("PMD.ServiceOrDaoClassShouldEndWithImplRule")
@AllArgsConstructor
public class DefaultZookeeperService implements ZookeeperService {
    /** Client */
    private final CuratorFramework client;

    /**
     * 判断节点是否存在
     *
     * @param path path
     * @return the boolean
     * @since 1.8.0
     */
    @Override
    public boolean isExistNode(String path) {
        this.client.sync();
        try {
            return this.client.checkExists().forPath(path) != null;
        } catch (Exception e) {
            log.error("isExistNode error...", e);
        }
        return false;
    }

    /**
     * 创建节点
     *
     * @param mode mode
     * @param path path
     * @since 1.8.0
     */
    @Override
    public void createNode(CreateMode mode, String path) {
        try {
            // 递归创建所需父节点
            this.client.create().creatingParentsIfNeeded().withMode(mode).forPath(path);
        } catch (Exception e) {
            log.error("createNode error...", e);
        }
    }

    /**
     * 设置节点数据
     *
     * @param path     path
     * @param nodeData node data
     * @since 1.8.0
     */
    @Override
    public void setNodeData(String path, String nodeData) {
        try {
            // 设置节点数据
            this.client.setData().forPath(path, nodeData.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("setNodeData error...", e);
        }
    }

    /**
     * 创建节点
     *
     * @param mode     mode
     * @param path     path
     * @param nodeData node data
     * @since 1.8.0
     */
    @Override
    public void createNodeAndData(CreateMode mode, String path, String nodeData) {
        try {
            // 创建节点，关联数据
            this.client.create().creatingParentsIfNeeded().withMode(mode)
                .forPath(path, nodeData.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("createNode error...", e);
        }
    }

    /**
     * 获取节点数据
     *
     * @param path path
     * @return the node data
     * @since 1.8.0
     */
    @Override
    public String getNodeData(String path) {
        try {
            // 数据读取和转换
            byte[] dataByte = this.client.getData().forPath(path);
            String data = new String(dataByte, StandardCharsets.UTF_8);
            if (StringUtils.isNotEmpty(data)) {
                return data;
            }
        } catch (Exception e) {
            log.error("getNodeData error...", e);
        }
        return null;
    }

    /**
     * 获取节点下数据
     *
     * @param path path
     * @return the node child
     * @since 1.8.0
     */
    @Override
    public List<String> getNodeChild(String path) {
        List<String> nodeChildDataList = new ArrayList<>();
        try {
            // 节点下数据集
            nodeChildDataList = this.client.getChildren().forPath(path);
        } catch (Exception e) {
            log.error("getNodeChild error...", e);
        }
        return nodeChildDataList;
    }

    /**
     * 是否递归删除节点
     *
     * @param path      path
     * @param recursive recursive
     * @since 1.8.0
     */
    @Override
    public void deleteNode(String path, Boolean recursive) {
        try {
            if (recursive) {
                // 递归删除节点
                this.client.delete().guaranteed().deletingChildrenIfNeeded().forPath(path);
            } else {
                // 删除单个节点
                this.client.delete().guaranteed().forPath(path);
            }
        } catch (Exception e) {
            log.error("deleteNode error...", e);
        }
    }

    /**
     * 获取读写锁
     *
     * @param path path
     * @return the read write lock
     * @since 1.8.0
     */
    @Override
    public InterProcessReadWriteLock getReadWriteLock(String path) {
        // 写锁互斥、读写互斥
        return new InterProcessReadWriteLock(this.client, path);
    }
}
