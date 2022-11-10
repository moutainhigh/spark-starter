package info.spark.starter.dingtalk.service;

import info.spark.starter.notify.Message;
import info.spark.starter.notify.Notify;

/**
 * <p>Description: </p>
 * https://developers.dingtalk.com/document/robots/robot-overview
 *
 * @param <T> parameter
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.12.05 00:27
 * @since 2.1.0
 */
public interface DingtalkNotifyService<T extends Message<?>> extends Notify<T> {

}
