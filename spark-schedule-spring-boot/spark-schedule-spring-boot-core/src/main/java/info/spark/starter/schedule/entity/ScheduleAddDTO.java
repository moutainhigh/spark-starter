package info.spark.starter.schedule.entity;

import info.spark.starter.common.base.BaseDTO;
import info.spark.starter.schedule.enums.ExecutorBlockStrategyEnum;
import info.spark.starter.schedule.enums.ExecutorRouteStrategyEnum;
import info.spark.starter.schedule.enums.GlueTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.4.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.23 14:44
 * @since 1.4.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ScheduleAddDTO extends BaseDTO<Integer> {

    /** serialVersionUID */
    private static final long serialVersionUID = 4019921154839509724L;
    /** 执行器主键ID */
    private Integer jobGroup;
    /** 任务执行CRON表达式 */
    private String jobCron;
    /** Job desc */
    private String jobDesc;
    /** 负责人 */
    private String author;
    /** 报警邮件 */
    private String alarmEmail;
    /** 执行器路由策略 */
    private String executorRouteStrategy;
    /** 执行器, 任务Handler名称 */
    private String executorHandler;
    /** 执行器, 任务参数 */
    private String executorParam;
    /** 阻塞处理策略 */
    private String executorBlockStrategy;
    /** 任务执行超时时间, 单位秒 */
    private Integer executorTimeout;
    /** 失败重试次数 */
    private Integer executorFailRetryCount;
    /** GLUE 类型 com.xxl.job.core.glue.GlueTypeEnum */
    private String glueType;
    /** 子任务ID, 多个逗号分隔 */
    private String childJobId;

    /**
     * Schedule add dto
     *
     * @param jobGroup      job group
     * @param author        author
     * @param jobDesc       job desc
     * @param executorParam executor param
     * @since 1.4.0
     */
    public ScheduleAddDTO(Integer jobGroup, String author, String jobDesc, String executorParam) {
        this.jobGroup = jobGroup;
        this.author = author;
        this.jobDesc = jobDesc;
        this.executorParam = executorParam;
        this.jobCron = "* * * 1/1 * ?";
        this.executorRouteStrategy = ExecutorRouteStrategyEnum.RANDOM.name();
        this.executorHandler = "checkExpireHandler";
        this.executorBlockStrategy = ExecutorBlockStrategyEnum.SERIAL_EXECUTION.name();
        this.executorTimeout = 30;
        this.executorFailRetryCount = 2;
        this.glueType = GlueTypeEnum.BEAN.name();
    }
}
