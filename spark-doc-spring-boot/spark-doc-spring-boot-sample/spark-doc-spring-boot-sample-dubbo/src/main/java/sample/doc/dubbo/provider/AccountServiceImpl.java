package sample.doc.dubbo.provider;

import org.apache.dubbo.config.annotation.Service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import sample.doc.dubbo.api.service.AccountService;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.15 19:36
 * @since 1.4.0
 */
@Service
@Api(value = "帐号服务")
public class AccountServiceImpl implements AccountService {

    /**
     * Logout *
     *
     * @param account account
     * @since 1.4.0
     */
    @Override
    @ApiOperation(value = "登出", notes = "退出用户信息")
    public void logout(String account) {}

    /**
     * Login boolean
     *
     * @param account  account
     * @param password password
     * @return the boolean
     * @since 1.4.0
     */
    @Override
    @ApiOperation(value = "登录")
    public boolean login(@ApiParam(value = "用户帐号") String account,
                         @ApiParam(value = "用户密码") String password) {
        return false;
    }

    /**
     * Login boolean
     *
     * @param account account
     * @param code    code
     * @return the boolean
     * @since 1.4.0
     */
    @Override
    @ApiOperation(nickname = "byCode", value = "登录", notes = "邀请码登录")
    public boolean login(@ApiParam(value = "用户帐号") String account, @ApiParam(value = "邀请码") int code) {
        return false;
    }

    /**
     * Update info *
     *
     * @param isBoy  is boy
     * @param number number
     * @since 1.4.0
     */
    @Override
    public void updateInfo(boolean isBoy, Integer number) {
    }

}
