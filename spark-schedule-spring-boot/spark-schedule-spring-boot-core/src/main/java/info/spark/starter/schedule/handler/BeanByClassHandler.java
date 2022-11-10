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
 * <p>Description: 通过传入的 class 执行任务 </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.03 10:47
 * @since 1.0.0
 */
@Component
public class BeanByClassHandler extends IJobHandler {
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
    @SuppressWarnings(value = {"PMD.UndefineMagicConstantRule"})
    @Job(value = "BeanByClassHandler")
    public ReturnT<String> execute(String param) {
        XxlJobLogger.log(param);
        if (StringUtils.isBlank(param)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "参数不能为空! ");
        }
        String[] split = param.split(",");
        if (split.length < 2) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "参数格式错误, 应为 完整类名, 方法名");
        }
        Class<?> taskBeanClass;
        try {
            taskBeanClass = Class.forName(split[0]);
        } catch (Exception e) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "类" + split[0] + "不存在");
        }
        Method method;
        try {
            method = taskBeanClass.getMethod(split[1]);
        } catch (Exception e) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "方法" + split[1] + "不存在");
        }
        Object o = this.applicationContext.getBean(taskBeanClass);
        try {
            method.invoke(o);
        } catch (Exception e) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "方法执行失败");
        }
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "执行成功");
    }
}
