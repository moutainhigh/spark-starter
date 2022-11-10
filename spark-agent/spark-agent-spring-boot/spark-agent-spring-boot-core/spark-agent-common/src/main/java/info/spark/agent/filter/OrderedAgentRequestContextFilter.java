package info.spark.agent.filter;

import org.springframework.boot.web.servlet.filter.OrderedFilter;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.03.11 11:30
 * @since 1.8.0
 */
public class OrderedAgentRequestContextFilter extends AgentRequestContextFilter implements OrderedFilter {

    /** Order */
    private int order = REQUEST_WRAPPER_FILTER_MAX_ORDER - 105;

    /**
     * Gets order *
     *
     * @return the order
     * @since 1.8.0
     */
    @Override
    public int getOrder() {
        return this.order;
    }

    /**
     * Set the order for this filter.
     *
     * @param order the order to set
     * @since 1.8.0
     */
    public void setOrder(int order) {
        this.order = order;
    }

}
