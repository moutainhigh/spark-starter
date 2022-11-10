package info.spark.starter.endpoint.initialization;

import info.spark.starter.common.util.ConfigKit;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.21 17:06
 * @since 2022.1.1
 */
public interface InitializationService {

    /**
     * Send warm up rest request
     *
     * @since 2022.1.1
     */
    default void sendWarmUpRestRequest() {
        final String baseUrl = "http://localhost:" + ConfigKit.getRestPort();
        final String warmUpEndpoint = baseUrl + "/warmup";
        request(warmUpEndpoint);
    }

    /**
     * Request
     *
     * @param warmUpEndpoint warm up endpoint
     * @since 2022.1.1
     */
    void request(String warmUpEndpoint);

    /**
     * Create sample message
     *
     * @return the warm up request dto
     * @since 2022.1.1
     */
    default @NotNull WarmUpRequestDTO createSampleMessage() {
        return WarmUpRequestDTO.builder()
            .warmUpString("warm me up")
            .warmUpNumber(15)
            .warmUpBigDecimal(BigDecimal.TEN)
            .warmUpEnumDto(WarmUpEnum.WARM)
            .build();
    }
}
