package info.spark.starter.ip2region;

import org.nutz.plugins.ip2region.DataBlock;
import org.nutz.plugins.ip2region.DbSearcher;
import org.springframework.beans.factory.DisposableBean;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.12.05 16:23
 * @since 1.7.0
 */
public class IP2regionTemplate implements DisposableBean {

    /** Db searcher */
    protected DbSearcher dbSearcher;
    /** Rwl */
    protected ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Ip 2 region template
     *
     * @param dbSearcher db searcher
     * @since 1.7.0
     */
    public IP2regionTemplate(DbSearcher dbSearcher) {
        this.dbSearcher = dbSearcher;
    }

    /**
     * get the region with a int ip address with memory binary search algorithm
     *
     * @param ip :  int ip address
     * @return {@link DataBlock} instance
     * @throws IOException if reader db file error
     * @since 1.7.0
     */
    public DataBlock memorySearch(long ip) throws IOException {
        try {
            this.lock.readLock().lock();
            return this.dbSearcher.memorySearch(ip);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    /**
     * get the region throught the ip address with memory binary search algorithm
     *
     * @param ip :  string ip address
     * @return {@link DataBlock} instance
     * @throws IOException if reader db file error
     * @since 1.7.0
     */
    public DataBlock memorySearch(String ip) throws IOException {
        try {
            this.lock.readLock().lock();
            return this.dbSearcher.memorySearch(ip);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    /**
     * get by index ptr
     *
     * @param ptr :  index ptr
     * @return {@link DataBlock} instance
     * @throws IOException if reader db file error
     * @since 1.7.0
     */
    public DataBlock getByIndexPtr(long ptr) throws IOException {
        try {
            this.lock.readLock().lock();
            return this.dbSearcher.getByIndexPtr(ptr);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    /**
     * get the region with a int ip address with b-tree algorithm
     *
     * @param ip :  int ip address
     * @return {@link DataBlock} instance
     * @throws IOException if reader db file error
     * @since 1.7.0
     */
    public DataBlock btreeSearch(long ip) throws IOException {
        try {
            this.lock.readLock().lock();
            return this.dbSearcher.btreeSearch(ip);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    /**
     * get the region throught the ip address with b-tree search algorithm
     *
     * @param ip :  string ip address
     * @return {@link DataBlock} instance
     * @throws IOException if reader db file error
     * @since 1.7.0
     */
    public DataBlock btreeSearch(String ip) throws IOException {
        try {
            this.lock.readLock().lock();
            return this.dbSearcher.btreeSearch(ip);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    /**
     * get the region with a int ip address with binary search algorithm
     *
     * @param ip :  int ip address
     * @return {@link DataBlock} instance
     * @throws IOException if reader db file error
     * @since 1.7.0
     */
    public DataBlock binarySearch(long ip) throws IOException {
        try {
            this.lock.readLock().lock();
            return this.dbSearcher.binarySearch(ip);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    /**
     * get the region throught the ip address with binary search algorithm
     *
     * @param ip :  string ip address
     * @return {@link DataBlock} instance
     * @throws IOException if reader db file error
     * @since 1.7.0
     */
    public DataBlock binarySearch(String ip) throws IOException {
        try {
            this.lock.readLock().lock();
            return this.dbSearcher.binarySearch(ip);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    /**
     * Gets region *
     *
     * @param ip ip
     * @return the region
     * @since 1.7.0
     */
    public String getRegion(String ip) {
        try {
            this.lock.readLock().lock();
            return this.dbSearcher.getRegion(ip);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    /**
     * Gets region address *
     *
     * @param ip ip
     * @return the region address
     * @throws IOException io exception
     * @since 1.7.0
     */
    public RegionAddress getRegionAddress(String ip) throws IOException {
        try {
            this.lock.readLock().lock();
            return new RegionAddress(this.dbSearcher.memorySearch(ip).getRegion().split("\\|"));
        } finally {
            this.lock.readLock().unlock();
        }
    }

    /**
     * Destroy
     *
     * @throws Exception exception
     * @since 1.7.0
     */
    @Override
    public void destroy() throws Exception {
        this.dbSearcher.close();
    }

}
