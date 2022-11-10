package info.spark.starter.rest;

import info.spark.starter.util.core.api.GeneralResult;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.24 20:42
 * @since 1.5.0
 */
@Validated
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public abstract class ServletController implements GeneralResult {

    /**
     * The Request.
     */
    @Autowired
    protected HttpServletRequest request;
    /**
     * The Response.
     */
    @Autowired
    protected HttpServletResponse response;

}
