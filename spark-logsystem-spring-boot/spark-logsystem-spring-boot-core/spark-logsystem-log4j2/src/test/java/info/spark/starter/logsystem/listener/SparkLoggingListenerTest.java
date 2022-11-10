package info.spark.starter.logsystem.listener;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.16 09:45
 * @since 1.0.0
 */
@ContextConfiguration
@TestExecutionListeners(value = {DependencyInjectionTestExecutionListener.class})
class SparkLoggingListenerTest {

}
