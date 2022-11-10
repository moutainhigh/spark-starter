package info.spark.starter.mq.entity;

import info.spark.starter.mq.annotation.MessageHandler;
import info.spark.starter.mq.consumer.AbstractMessageHandler;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.14 15:52
 * @since 1.5.0
 */
@Slf4j
@MessageHandler
public class TestHandler extends AbstractMessageHandler<TestMessage> {

    /**
     * Handle
     *
     * @param content content
     * @since 1.5.0
     */
    @Override
    public void handle(String content) {
        log.info("start to handle test message:" + content);
        int start = 1;
        int end = 5;
        Random random = new Random();
        int number = random.nextInt(end - start + 1) + start;
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(number));
    }

    /**
     * Handle
     *
     * @param content content
     * @since 1.5.0
     */
    @Override
    public void handle(TestMessage content) {
        super.handle(content);
    }

}
