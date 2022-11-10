package sample.doc.dubbo.provider;

import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.rpc.RpcContext;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import sample.doc.dubbo.api.dto.UserDTO;
import sample.doc.dubbo.api.service.UserService;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:26
 * @since 1.4.0
 */
@Slf4j
@Api(value = "用户服务")
@Service
public class UserServiceImpl implements UserService {

    /** User */
    private static final UserDTO user = new UserDTO();

    /** List */
    private static final List<UserDTO> list = new ArrayList<>();

    static {
        user.setId(1L);
        user.setName("dong4j");
        user.setSite("http://dong4j.info");
        list.add(user);
    }

    /**
     * Gets by id *
     *
     * @param id id
     * @return the by id
     * @since 1.4.0
     */
    @Override
    @ApiOperation(value = "查询用户信息")
    public UserDTO getById(@ApiParam(value = "用户 ID") Long id) {
        RpcContext rpcContext = RpcContext.getContext();
        String info = String.format("Service [name :%s , port : %d] %s",
                                          rpcContext.getUrl().getPath(),
                                          rpcContext.getLocalPort(),
                                          rpcContext.getMethodName());

        return new UserDTO(Long.parseLong(rpcContext.getAttachment("userId")), "dong4j", info);
    }

    /**
     * Query list
     *
     * @param phone phone
     * @return the list
     * @since 1.4.0
     */
    @Override
    public List<UserDTO> query(String phone) {
        return list;
    }

    /**
     * Query list
     *
     * @param areaCode area code
     * @return the list
     * @since 1.4.0
     */
    @Override
    public List<UserDTO> query(int areaCode) {
        return list;
    }

    /**
     * Get user dto
     *
     * @param id id
     * @return the user dto
     * @since 1.4.0
     */
    @Override
    public UserDTO get(String id) {
        return user;
    }

    /**
     * Save *
     *
     * @param user user
     * @since 1.4.0
     */
    @Override
    public void save(UserDTO user) {}

    /**
     * Update user dto
     *
     * @param user user
     * @return the user dto
     * @since 1.4.0
     */
    @Override
    public UserDTO update(UserDTO user) {
        return null;
    }

    /**
     * Delete *
     *
     * @param id id
     * @since 1.4.0
     */
    @Override
    public void delete(String id) {}

    /**
     * Compare int
     *
     * @param src  src
     * @param dest dest
     * @return the int
     * @since 1.4.0
     */
    @Override
    public int compare(UserDTO src, UserDTO dest) {
        return 0;
    }

}
