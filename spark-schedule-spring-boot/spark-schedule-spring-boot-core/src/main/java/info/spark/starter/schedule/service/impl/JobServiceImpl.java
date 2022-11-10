package info.spark.starter.schedule.service.impl;

import info.spark.starter.schedule.constant.ApiPath;
import info.spark.starter.schedule.entity.ScheduleAddDTO;
import info.spark.starter.schedule.exception.ScheduleException;
import info.spark.starter.schedule.service.JobService;
import com.xxl.job.core.biz.client.AdminBizClient;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import com.xxl.job.core.util.XxlJobRemotingUtil;

import org.springframework.stereotype.Service;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.4.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.23 14:38
 * @since 1.4.0
 */
@Service
public class JobServiceImpl implements JobService {

    /**
     * 向任务调度中心添加定时任务
     *
     * @param dto dto
     * @return the string
     * @since 1.4.0
     */
    @Override
    public String add(ScheduleAddDTO dto) {
        return doJobPost(dto, ApiPath.ADD_JOB);
    }

    /**
     * 修改任务信息 (操作成功返回null)
     *
     * @param dto dto
     * @return the string
     * @since 2.0.0
     */
    @Override
    public String update(ScheduleAddDTO dto) {
        return doJobPost(dto, ApiPath.UPDATE_JOB);
    }

    /**
     * 删除任务(操作成功返回null)
     *
     * @param id id
     * @return the string
     * @since 2.0.0
     */
    @Override
    public String remove(Integer id) {
        return doJobPost(id, ApiPath.REMOVE_JOB);
    }

    /**
     * 停止任务(操作成功返回null)
     *
     * @param id id
     * @return the string
     * @since 2.0.0
     */
    @Override
    public String stop(Integer id) {
        return doJobPost(id, ApiPath.STOP_JOB);
    }

    /**
     * 开启任务(操作成功返回null)
     *
     * @param id id
     * @return the string
     * @since 2.0.0
     */
    @Override
    public String start(Integer id) {
        return doJobPost(id, ApiPath.START_JOB);
    }

    /**
     * 修改任务的corn表达式(操作成功返回null)
     *
     * @param dto dto
     * @return the string
     * @since 2.0.0
     */
    @Override
    public String updateCorn(ScheduleAddDTO dto) {
        return doJobPost(dto, ApiPath.UPDATE_CORN);
    }

    /**
     * 向xxlJob发起post请求
     *
     * @param dto dto
     * @param api api
     * @return the string
     * @since 2.0.0
     */
    private String doJobPost(Object dto, String api) {
        AdminBizClient adminBiz = (AdminBizClient) XxlJobSpringExecutor.getAdminBizList().get(0);

        ReturnT<String> result = XxlJobRemotingUtil.postBody(adminBiz.getAddressUrl() + api,
            adminBiz.getAccessToken(),
            adminBiz.getTimeout(),
            dto,
            String.class);
        if (result.getCode() == ReturnT.FAIL_CODE) {
            throw new ScheduleException("任务失败: [{}]", result);
        }
        return result.getContent();
    }
}
