package sample.doc.dubbo.provider;

import org.apache.dubbo.config.annotation.Service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import sample.doc.dubbo.api.service.PushService;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.15 19:35
 * @since 1.4.0
 */
@Service
@Api(value = "推送服务")
public class PushServiceImpl implements PushService {

    /**
     * Push string
     *
     * @param account account
     * @return the string
     * @since 1.4.0
     */
    @ApiOperation(value = "推送", notes = "推送消息至指定帐号")
    @Override
    public String push(@ApiParam(value = "帐号") String account) {
        return "中文字符串";
    }

}
