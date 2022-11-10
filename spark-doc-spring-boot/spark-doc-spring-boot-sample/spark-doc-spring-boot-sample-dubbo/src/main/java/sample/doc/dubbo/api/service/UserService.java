package sample.doc.dubbo.api.service;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import sample.doc.dubbo.api.dto.UserDTO;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 13:27
 * @since 1.4.0
 */
@Api(tags = "用户API")
public interface UserService {
    /**
     * Gets by id.
     *
     * @param id the id
     * @return the by id
     * @since 1.4.0
     */
    UserDTO getById(Long id);

    /**
     * Query list
     *
     * @param phone phone
     * @return the list
     * @since 1.4.0
     */
    @ApiOperation(nickname = "byPhone", value = "查询用户", notes = "通过phone取用户信息", response = UserDTO.class, responseContainer = "List")
    List<UserDTO> query(@ApiParam(value = "用户phone") String phone);

    /**
     * Query list
     *
     * @param areaCode area code
     * @return the list
     * @since 1.4.0
     */
    @ApiOperation(nickname = "byArea", value = "查询用户", notes = "通过城市地区code取用户信息", response = UserDTO.class, responseContainer = "List")
    List<UserDTO> query(@ApiParam(value = "城市地区code") int areaCode);

    /**
     * Get user dto
     *
     * @param id id
     * @return the user dto
     * @since 1.4.0
     */
    @ApiOperation(value = "获取用户", notes = "通过id取用户信息", response = UserDTO.class, httpMethod="GET")
    UserDTO get(@ApiParam(value = "用户id") String id);

    /**
     * Save *
     *
     * @param user user
     * @since 1.4.0
     */
    @ApiOperation(value = "保存用户", notes = "保存用户信息")
    void save(@ApiParam(value = "用户信息") UserDTO user);

    /**
     * Update user dto
     *
     * @param user user
     * @return the user dto
     * @since 1.4.0
     */
    @ApiOperation(value = "更新用户", notes = "更新用户信息")
    UserDTO update(@ApiParam(value = "用户信息") UserDTO user);

    /**
     * Delete *
     *
     * @param id id
     * @since 1.4.0
     */
    @ApiOperation(value = "删除用户", notes = "保存用户信息")
    void delete(@ApiParam(value = "用户id") String id);

    /**
     * Compare int
     *
     * @param src  src
     * @param dest dest
     * @return the int
     * @since 1.4.0
     */
    @ApiOperation(value = "比较用户")
    int compare(@ApiParam(value = "用户id", required=true) UserDTO src, UserDTO dest);

}
