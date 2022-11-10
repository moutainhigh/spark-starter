package info.spark.agent.endpoint;

import info.spark.agent.sender.AgentService;

import org.springframework.beans.factory.InitializingBean;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.30 03:28
 * @since 1.0.0
 */
@Slf4j
public abstract class AbstractEndpoint implements InitializingBean {
    /** Agent service */
    @Resource
    protected AgentService agentService;
    /** Request */
    @Resource
    protected HttpServletRequest request;
    /** Response */
    @Resource
    protected HttpServletResponse response;

    /**
     * After properties set
     *
     * @since 1.0.0
     */
    @Override
    public void afterPropertiesSet() {
    }

}
