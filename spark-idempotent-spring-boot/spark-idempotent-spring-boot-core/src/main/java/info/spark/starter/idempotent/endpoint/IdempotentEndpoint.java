package info.spark.starter.idempotent.endpoint;

import info.spark.starter.basic.Result;
import info.spark.starter.idempotent.service.TokenService;
import info.spark.starter.util.core.api.R;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * <p>Description:  </p>
 *
 * @author liujintao
 * @version 1.0.0
 * @email "mailto:liujintao@gmail.com"
 * @date 2020.07.22 09:16
 * @since 1.0.0
 */
@Api(tags = "幂等 token 获取")
@RestController
public class IdempotentEndpoint {

    /** Token service */
    @Resource
    private TokenService tokenService;

    /**
     * Idempotent token
     *
     * @return the string
     * @since 1.0.0
     */
    @ApiOperation(value = "幂等 token 获取接口")
    @GetMapping(value = "/idempotent/token")
    public Result<String> idempotentToken() {
        return R.succeed(this.tokenService.createToken());
    }
}
