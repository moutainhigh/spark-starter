package info.spark.starter.email.service;

import info.spark.starter.notify.Message;
import info.spark.starter.notify.Notify;

/**
 * <p>Description: </p>
 *
 * @param <T> parameter
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.07 21:30
 * @since 1.0.0
 */
public interface EmailNotifyService<T extends Message<?>> extends Notify<T> {

}
