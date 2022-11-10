package info.spark.starter.zookeeper;

import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.zookeeper.CreateMode;

import java.util.List;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.29 16:04
 * @since 1.8.0
 */
public interface ZookeeperService {
    /**
     * 判断节点是否存在
     *
     * @param path path
     * @return the boolean
     * @since 1.8.0
     */
    boolean isExistNode(String path);

    /**
     * 创建节点
     *
     * @param mode mode
     * @param path path
     * @since 1.8.0
     */
    void createNode(CreateMode mode, String path);

    /**
     * 设置节点数据
     *
     * @param path     path
     * @param nodeData node data
     * @since 1.8.0
     */
    void setNodeData(String path, String nodeData);

    /**
     * 创建节点
     *
     * @param mode     mode
     * @param path     path
     * @param nodeData node data
     * @since 1.8.0
     */
    void createNodeAndData(CreateMode mode, String path, String nodeData);

    /**
     * 获取节点数据
     *
     * @param path path
     * @return the node data
     * @since 1.8.0
     */
    String getNodeData(String path);

    /**
     * 获取节点下数据
     *
     * @param path path
     * @return the node child
     * @since 1.8.0
     */
    List<String> getNodeChild(String path);

    /**
     * 是否递归删除节点
     *
     * @param path      path
     * @param recursive recursive
     * @since 1.8.0
     */
    void deleteNode(String path, Boolean recursive);

    /**
     * 获取读写锁
     *
     * @param path path
     * @return the read write lock
     * @since 1.8.0
     */
    InterProcessReadWriteLock getReadWriteLock(String path);
}
