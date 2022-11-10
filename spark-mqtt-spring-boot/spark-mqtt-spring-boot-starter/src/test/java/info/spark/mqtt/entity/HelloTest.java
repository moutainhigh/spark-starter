package info.spark.mqtt.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.ToString;

/**
 * <p>Description:  </p>
 *
 * @author zhonghaijun
 * @version 1.0.0
 * @email "mailto:zhonghaijun@gmail.com"
 * @date 2021.12.17 13:54
 * @since 2.1.0
 */
@Data
@ToString
public class HelloTest implements Serializable {

    private static final long serialVersionUID = -1507450473023076577L;
    private String msg;

    private Date time;

}
