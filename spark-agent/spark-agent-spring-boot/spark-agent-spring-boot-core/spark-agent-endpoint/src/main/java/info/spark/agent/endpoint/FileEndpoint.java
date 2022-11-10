package info.spark.agent.endpoint;

import info.spark.agent.constant.AgentConstant;
import info.spark.starter.endpoint.Endpoint;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 文件相关请求接口 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.28 00:06
 * @since 1.0.0
 */
@Slf4j
@Endpoint
public class FileEndpoint extends AbstractEndpoint {
    /** ROOT_ENDPOINT */
    private static final String FILE_ROOT_ENDPOINT = AgentConstant.ROOT_ENDPOINT + "/file";

    /**
     * Upload
     *
     * @since 1.0.0
     */
    @PostMapping(value = FILE_ROOT_ENDPOINT)
    public void upload() {

    }

    /**
     * Download
     *
     * @since 1.0.0
     */
    @GetMapping(value = FILE_ROOT_ENDPOINT)
    public void download() {

    }
}
