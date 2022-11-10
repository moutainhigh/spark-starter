package info.spark.starter.schedule.service;

import info.spark.starter.schedule.entity.ScheduleAddDTO;

/**
 * <p>Description: 定时任务服务 </p>
 *
 * @author dong4j
 * @version 1.4.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.23 14:38
 * @since 1.4.0
 */
public interface JobService {

    /**
     * Add
     *
     * @param dto dto
     * @return the string
     * @since 1.4.0
     */
    String add(ScheduleAddDTO dto);

    /**
     * 修改任务信息 (操作成功返回null)
     *
     * @param dto dto
     * @return the string
     * @since 2.0.0
     */
    String update(ScheduleAddDTO dto);

    /**
     * 删除任务(操作成功返回null)
     *
     * @param id id
     * @return the string
     * @since 2.0.0
     */
    String remove(Integer id);

    /**
     * Stop
     *
     * @param id id
     * @return the string
     * @since 2.0.0
     */
    String stop(Integer id);

    /**
     * 开启任务(操作成功返回null)
     *
     * @param id id
     * @return the string
     * @since 2.0.0
     */
    String start(Integer id);

    /**
     * 修改任务的corn表达式(操作成功返回null)
     *
     * @param dto dto
     * @return the string
     * @since 2.0.0
     */
    String updateCorn(ScheduleAddDTO dto);
}
