package info.spark.starter.id.provider;

import info.spark.starter.basic.support.StrFormatter;
import info.spark.starter.basic.util.StringUtils;
import info.spark.starter.id.util.IpUtils;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 依赖数据库分配机器 id</p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.24 16:25
 * @since 1.5.0
 */
@Slf4j
public class DbMachineIdProvider implements MachineIdProvider {

    /** Machine id */
    private long machineId;
    /** Jdbc template */
    private JdbcTemplate jdbcTemplate;
    /** SELECT_SQL */
    private static final String SELECT_SQL = "select id from db_machine_id_provider where ip = ?";
    /** UPDATE_SQL */
    private static final String UPDATE_SQL = "update db_machine_id_provider set ip = ? where ip is null limit 1";

    /**
     * Db machine id provider
     *
     * @since 1.5.0
     */
    public DbMachineIdProvider() {
        log.debug("IpConfigurableMachineIdProvider constructed.");
    }

    /**
     * Init
     *
     * @since 1.5.0
     */
    public void init() {
        String ip = IpUtils.getHostIp();

        if (StringUtils.hasText(ip)) {
            String msg = "Fail to get host IP address. Stop to initialize the DbMachineIdProvider provider.";

            log.error(msg);
            throw new IllegalStateException(msg);
        }

        Long id = null;
        try {
            id = this.jdbcTemplate.queryForObject(
                SELECT_SQL,
                new Object[] {ip}, Long.class);

        } catch (EmptyResultDataAccessException e) {
            // Ignore the exception
            log.error("No allocation before for ip {}.", ip);
        }

        if (id != null) {
            this.machineId = id;
            return;
        }

        log.info("Fail to get ID from DB for host IP address {}. Next step try to allocate one.", ip);

        int count = this.jdbcTemplate.update(UPDATE_SQL, ip);

        if (count != 1) {
            String msg = StrFormatter.format("Fail to allocte ID for host IP address {}. "
                                             + "The {} records are updated. "
                                             + "Stop to initialize the DbMachineIdProvider provider.",
                                             ip,
                                             count);

            log.error(msg);
            throw new IllegalStateException(msg);
        }

        try {
            id = this.jdbcTemplate.queryForObject(
                SELECT_SQL,
                new Object[] {ip}, Long.class);

        } catch (EmptyResultDataAccessException e) {
            // Ignore the exception
            log.error("Fail to do allocation for ip {}.", ip);
        }

        if (id == null) {
            String msg = StrFormatter.format("Fail to get ID from DB for host IP address {} after allocation. "
                                             + "Stop to initialize the DbMachineIdProvider provider.", ip);

            log.error(msg);
            throw new IllegalStateException(msg);
        }

        this.machineId = id;
    }

    /**
     * Gets machine id *
     *
     * @return the machine id
     * @since 1.5.0
     */
    @Override
    public long getMachineId() {
        return this.machineId;
    }

    /**
     * Sets machine id *
     *
     * @param machineId machine id
     * @since 1.5.0
     */
    public void setMachineId(long machineId) {
        this.machineId = machineId;
    }

    /**
     * Gets jdbc template *
     *
     * @return the jdbc template
     * @since 1.5.0
     */
    public JdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }

    /**
     * Sets jdbc template *
     *
     * @param jdbcTemplate jdbc template
     * @since 1.5.0
     */
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
