package sample.knife4j;

import info.spark.starter.basic.Result;
import info.spark.starter.util.core.api.R;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:19
 * @since 1.4.0
 */
@Api(tags = "swagger test api")
@RestController
public class TestController {
    /**
     * Find by id result.
     *
     * @param id the id
     * @return the result
     * @since 1.4.0
     */
    @GetMapping("/user/{id}")
    @ApiOperation(value = "获取用户详情", notes = "xxx", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses( {
                       @ApiResponse(code = 2000, message = "操作成功", response = R.class),
                       @ApiResponse(code = 6000, message = "参数校验失败", response = R.class),
                       @ApiResponse(code = 4000, message = "用户不存在", response = R.class)
                   })
    public Result<Void> findById(@Valid @NotNull @Size(min = 1) @PathVariable("id") Long id) {
        return R.succeed();
    }

    /**
     * Save result.
     *
     * @param user the user
     * @return the result
     * @since 1.4.0
     */
    @PostMapping("/user")
    @ApiOperation(value = "创建新用户", notes = "传入一个 User 实体", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Void> save(@Valid @RequestBody User user) {
        return R.succeed();
    }

    /**
     * Update result.
     *
     * @param user the user
     * @return the result
     * @since 1.4.0
     */
    @PutMapping("/user")
    @ApiOperation(value = "修改用户信息", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<String> update(@Valid @RequestBody User user) {
        return R.succeed();
    }

    /**
     * Delete by id result.
     *
     * @param id the id
     * @return the result
     * @since 1.4.0
     */
    @DeleteMapping("/user/{id}")
    @ApiOperation(value = "删除用户", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<String> deleteById(@Valid @NotNull @Size(min = 1) @PathVariable("id") Long id) {
        return R.succeed("delete user : " + id);
    }

    /**
     * List.
     *
     * @param pageIndex the page index
     * @param pageSize  the page size
     * @return the list
     * @since 1.4.0
     */
    @GetMapping("/user")
    @ApiOperation(value = "用户列表", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Void> list(@ApiParam("查看第几页") @RequestParam(value = "pageIndex", required = false) Integer pageIndex,
                             @ApiParam("每页多少条") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return R.succeed();
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.4.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.05.08 17:27
     * @since 1.4.0
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class User {
        /** Username */
        @NotBlank(message = "用户名不能为空")
        private String username;
        /** Version */
        private String version;
        /** Date */
        private Date date;
        /** Age */
        @Max(150)
        @Min(1)
        private Integer age;
    }
}
