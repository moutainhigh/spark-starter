package info.spark.starter.sms;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.03.01 10:37
 * @since 1.0.0
 */
public abstract class SmsConfig implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 7906553469908486568L;
    /** App id */
    @Getter
    @Setter
    private String appId;
    /** App key */
    @Getter
    @Setter
    private String appKey;
    /** 是否开启短信 */
    @Getter
    @Setter
    private boolean enable;
    /** 中国大陆国际区号 */
    public static final String MAINLAND_CHINA = "86";

}
