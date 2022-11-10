package info.spark.starter.logsystem.publisher;

import org.jetbrains.annotations.Contract;

import lombok.extern.slf4j.Slf4j;

/**
 * 发送业务日志
 * todo-dong4j : (2019年11月22日 17:26) []
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 10:04
 * @since 1.0.0
 */
@Slf4j
public class BusinessLogPublisher {

    /**
     * Publish event *
     *
     * @param level level
     * @param id    id
     * @param data  data
     * @since 1.0.0
     */
    @Contract(pure = true)
    public static void publishEvent(String level, String id, String data) {

    }

}
