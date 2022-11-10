package sample.doc.restdoc.controller;

import info.spark.starter.basic.Result;
import info.spark.starter.rest.base.AbstractController;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import sample.doc.restdoc.entity.User;
import sample.doc.restdoc.repository.MapBackedRepository;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.15 22:23
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Users API", description = "User相关API")
public class UserController extends AbstractController {
    /** User data */
    private final UserRepository userData = new UserRepository();

    /**
     * Create user response entity
     *
     * @param user user
     * @return the response entity
     * @since 1.0.0
     */
    @PostMapping
    @ApiOperation(value = "创建用户", notes = "登陆后才能调用")
    public Result<User> createUser(@RequestBody User user) {
        this.userData.add(user);
        return this.ok(user);
    }

    /**
     * Create users with array response entity
     *
     * @param users users
     * @return the response entity
     * @since 1.0.0
     */
    @PostMapping("/createWithArray")
    @ApiOperation("创建一组用户")
    public Result<List<User>> createUsersWithArray(
        @ApiParam(value = "User List", required = true)
        @RequestBody List<User> users) {
        for (User user : users) {
            this.userData.add(user);
        }
        return this.ok(null);
    }

    /**
     * Update user response entity
     *
     * @param username username
     * @param user     user
     * @return the response entity
     * @since 1.0.0
     */
    @PutMapping("/{username}")
    @ApiOperation(value = "更新用户", notes = "登陆后才能调用")
    public Result<String> updateUser(
        @ApiParam(value = "待更新用户的用户名", required = true) @PathVariable("username") String username,
        @ApiParam(value = "待更新的用户对象", required = true) @RequestBody User user) {
        if (this.userData.exists(username)) {
            this.userData.add(user);
        }
        return this.ok();
    }

    /**
     * Delete user response entity
     *
     * @param username username
     * @return the response entity
     * @since 1.0.0
     */
    @DeleteMapping("/{username}")
    @ApiOperation(value = "删除用户", notes = "登陆后才能调用")
    @ApiResponses(value = {@ApiResponse(code = 404, message = "not found")})
    public Result<String> deleteUser(@PathVariable("username") String username) {
        if (this.userData.exists(username)) {
            this.userData.delete(username);
        }
        return this.ok();
    }


    /**
     * Gets user by name *
     *
     * @param username username
     * @return the user by name
     * @since 1.0.0
     */
    @GetMapping("/{username}")
    @ApiOperation(value = "通过username查找用户", response = User.class)
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid username supplied"),
        @ApiResponse(code = 404, message = "User not found")})
    public Result<User> getUserByName(
        @PathVariable("username") String username) {
        return this.ok(this.userData.get(username));
    }

    /**
     * Login user response entity
     *
     * @param username username
     * @param password password
     * @return the response entity
     * @since 1.0.0
     */
    @GetMapping(value = "/login")
    @ApiOperation(value = "登录", response = String.class)
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid username/password supplied")})
    public Result<String> loginUser(@RequestParam("username") String username, @RequestParam("password") String password) {
        return this.ok("logged in user session:" + System.currentTimeMillis());
    }

    /**
     * Logout user response entity
     *
     * @return the response entity
     * @since 1.0.0
     */
    @GetMapping(value = "/logout")
    @ApiOperation(value = "退出登录")
    public Result<String> logoutUser() {
        return this.ok("logged out user session:" + System.currentTimeMillis());
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.2.4
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.02.15 22:16
     * @since 1.0.0
     */
    private static class UserRepository extends MapBackedRepository<String, User> {
    }
}
