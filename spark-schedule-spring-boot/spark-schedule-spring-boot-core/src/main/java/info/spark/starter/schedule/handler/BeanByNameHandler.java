package info.spark.starter.schedule.handler;

import info.spark.starter.schedule.annotation.Job;
import info.spark.starter.util.StringUtils;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.log.XxlJobLogger;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import javax.annotation.Resource;

/**
 * <p>Description: 通过传入的任务名执行任务</p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.03 10:53
 * @since 1.0.0
 */
@Component
class BeanByNameHandler extends IJobHandler {
    /** Application context */
    @Resource
    private ApplicationContext applicationContext;

    /**
     * Execute return t
     *
     * @param param param
     * @return the return t
     * @since 1.0.0
     */
    @Override
    @SuppressWarnings("PMD.UndefineMagicConstantRule")
    @Job(value = "BeanByNameHandler")
    public ReturnT<String> execute(String param) {
        XxlJobLogger.log(param);
        if (StringUtils.isBlank(param)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "参数不能为空! ");
        }
        String[] split = param.split(",");
        if (split.length < 2) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "参数格式错误, 应为 bean 名称, 方法名");
        }
        Object o = this.applicationContext.getBean(split[0]);
        Method method;
        try {
            method = o.getClass().getMethod(split[1]);
        } catch (Exception e) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "方法" + split[1] + "不存在");
        }
        try {
            method.invoke(o);
        } catch (Exception e) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "调用方法失败");
        }
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "调用 job 成功");
    }
}
