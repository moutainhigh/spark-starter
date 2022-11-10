package info.spark.starter.endpoint.initialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.spark.starter.basic.util.JsonUtils;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.util.StopWatch;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 应用启动完成后进行预热, 解决第一次请求慢的问题 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.21 17:27
 * @since 2022.1.1
 */
@Slf4j
public class PreloadComponent {
    /** Service */
    private final InitializationService service;
    /** Object mapper */
    private final ObjectMapper objectMapper = JsonUtils.getInstance();

    /**
     * Preload component
     *
     * @param service service
     * @since 2022.1.1
     */
    public PreloadComponent(InitializationService service) {
        this.service = service;
    }

    /**
     * On application event
     *
     * @param event event
     * @since 2022.1.1
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("应用启动完成, 开启执行预热逻辑");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        this.manualValidation();
        this.mapJson();
        this.service.sendWarmUpRestRequest();
        stopWatch.stop();
        log.info("服务预热完成, 第一次请求: {} ms", stopWatch.getTotalTimeMillis());
    }

    /**
     * Map json
     *
     * @since 2022.1.1
     */
    @SuppressWarnings("checkstyle:OperatorWrap")
    private void mapJson() {
        final String json = "{\n" +
                            "    \"inputMessage\": \"abc\",\n" +
                            "    \"someNumber\": 123.4,\n" +
                            "    \"patternString\": \"this is a fixed string\",\n" +
                            "    \"selectOne\": \"TWO\"\n" +
                            "}";

        try {
            this.objectMapper.readValue(json, WarmUpRequestDTO.class);
        } catch (JsonProcessingException ignored) {
        }
    }

    /**
     * Manual validation
     *
     * @since 2022.1.1
     */
    private void manualValidation() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<WarmUpRequestDTO>> violations = validator.validate(this.service.createSampleMessage());
    }

}
