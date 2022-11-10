package info.spark.starter.rest.support;

import info.spark.starter.auth.CurrentUser;
import info.spark.starter.auth.util.AuthUtils;
import info.spark.starter.auth.util.JwtUtils;
import info.spark.starter.util.StringUtils;

import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.09.11 13:06
 * @since 1.6.0
 */
public interface CurrentUserService {

    /**
     * 通过 token 获取用户信息
     *
     * @param token token
     * @return the current user
     * @since 1.6.0
     */
    default CurrentUser getCurrentUser(String token) {
        return JwtUtils.PlayGround.getUser(token);
    }

    /**
     * 从 request 获取用户信息
     *
     * @param request request
     * @return the current user
     * @since 1.6.0
     */
    default CurrentUser getCurrentUser(@NotNull HttpServletRequest request) {
        String token = AuthUtils.getToken(request);

        if (StringUtils.isNotBlank(token)) {
            return this.getCurrentUser(token);
        }
        return null;
    }
}
