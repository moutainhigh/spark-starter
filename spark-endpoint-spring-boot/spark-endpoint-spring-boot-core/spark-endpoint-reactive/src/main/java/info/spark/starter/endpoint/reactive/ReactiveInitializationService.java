package info.spark.starter.endpoint.reactive;

import info.spark.starter.endpoint.initialization.InitializationService;
import info.spark.starter.endpoint.initialization.WarmUpRequestDTO;

import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.21 17:07
 * @since 2022.1.1
 */
@Slf4j
@SuppressWarnings("PMD.ServiceOrDaoClassShouldEndWithImplRule")
public class ReactiveInitializationService implements InitializationService {
    /** Web client builder */
    private final WebClient.Builder webClientBuilder;

    /**
     * Reactive initialization service
     *
     * @param webClientBuilder web client builder
     * @since 2022.1.1
     */
    public ReactiveInitializationService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    /**
     * Request
     *
     * @param warmUpEndpoint warm up endpoint
     * @since 2022.1.1
     */
    @Override
    public void request(String warmUpEndpoint) {
        this.webClientBuilder.build().post()
            .uri(warmUpEndpoint)
            .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .body(Mono.just(this.createSampleMessage()), WarmUpRequestDTO.class)
            .retrieve()
            .bodyToMono(String.class)
            .timeout(Duration.ofSeconds(5))
            .block();
    }
}
