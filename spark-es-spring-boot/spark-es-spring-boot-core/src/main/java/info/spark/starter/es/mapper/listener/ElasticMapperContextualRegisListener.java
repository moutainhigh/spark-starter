package info.spark.starter.es.mapper.listener;

import info.spark.starter.common.SparkApplicationListener;
import info.spark.starter.common.context.SpringContext;
import info.spark.starter.es.mapper.container.EsMapperContextual;
import info.spark.starter.processor.annotation.AutoListener;

import org.springframework.boot.context.event.ApplicationStartedEvent;

/**
 * <p>Description:  </p>
 *
 * @author wanghao
 * @version 1.7.1
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.22 14:24
 * @since 1.7.1
 */
@AutoListener
public class ElasticMapperContextualRegisListener implements SparkApplicationListener {

    /**
     * 启动程序,
     *
     * @param event event
     * @since 1.0.0
     */
    @Override
    public void onApplicationStartedEvent(ApplicationStartedEvent event) {
        if (!EsMapperContextual.MAPPER_CACHE.isEmpty()) {
            Runner.executeAtLast(this.key(event, this.getClass()),
                                 () -> {
                                     EsMapperContextual contextual = SpringContext.getInstance(EsMapperContextual.class);
                                     contextual.build(EsMapperContextual.MAPPER_CACHE);
                                     // help gc
                                     EsMapperContextual.MAPPER_CACHE.clear();
                                 });
        }
    }
}
