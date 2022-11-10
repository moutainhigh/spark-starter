package info.spark.starter.captcha.endpoint;

import info.spark.starter.basic.Result;
import info.spark.starter.captcha.ICaptcha;
import info.spark.starter.captcha.entity.Captcha;
import info.spark.starter.captcha.enums.CaptchaCodes;
import info.spark.starter.util.core.api.R;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * <p>Description: 验证码接口 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.19 11:10
 * @since 1.0.0
 */
@Api(tags = "验证码接口")
@RestController
@RequestMapping("/captcha")
public class CaptchaEndpoint {

    /** Captcha */
    private final ICaptcha captchaService;

    /**
     * Instantiates a new Kaptcha controller.
     *
     * @param captchaService the kaptcha
     * @since 1.0.0
     */
    @Contract(pure = true)
    public CaptchaEndpoint(ICaptcha captchaService) {
        this.captchaService = captchaService;
    }

    /**
     * 验证码检查优先级: 配置文件 > 动态检查
     * 如果应用设置为不检查验证码, 则直接返回 false
     * 如果设置为 ture, 会检查当前用户登录失败的次数, 如果在配置的时间内累计登录次数达到阈值, 则返回 true, 即需要验证码
     *
     * @param request request
     * @return the result true: 调用验证码接口, 显示验证码图片; false: 不需要调用验证码接口
     * @since 1.0.0
     */
    @GetMapping(value = "/check")
    @ApiOperation(value = "是否显示验证码", notes = "true: 调用验证码接口, 显示验证码图片; false: 不需要调用验证码接口")
    public Result<Boolean> render(HttpServletRequest request) {
        // 默认需要检查验证码, 当 enbale = true 时, 才根据登陆次数动态显示验证码
        return R.succeed(this.captchaService.showCaptcha(request));
    }

    /**
     * 将图片编码为 Base64 返回, header 中获取 clientId
     * todo-dong4j : (2019年10月28日 21:56) [接口次数限制]
     *
     * @return the result
     * @since 1.0.0
     */
    @GetMapping(value = "/render")
    @ApiOperation(value = "获取验证码实体", notes = "用于 V5 版本使用, 返回的验证码图片是 Base64 格式, 前端需要保存 uuid, 登录时传给后端.")
    public Result<Captcha> render() {
        Captcha captcha = this.captchaService.render();
        return captcha == null ? R.failed(CaptchaCodes.CODE_RENDER_ERROR) : R.succeed(captcha);
    }

    /**
     * 直接返回验证码图片 IO 流, 写入到 response 中, 服务消费端需要从 response 中获取数据
     * header 中获取 clientId
     *
     * @param response response
     * @throws IOException io exception
     * @since 1.0.0
     */
    @GetMapping(value = "/render/io", produces = MediaType.IMAGE_JPEG_VALUE)
    @ApiOperation(value = "获取验证码图片流", notes = "用于 V4 版本使用, 返回的验证码图片是 I/O 流,前端需要从 Response.header 中获取 uuid,登录时传给后端.")
    public void renderIo(@NotNull HttpServletResponse response) throws IOException {
        Captcha io = this.captchaService.renderIo();
        response.getOutputStream().write(io.getImageByte());
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        response.setHeader("uuid", io.getUuid());
        response.flushBuffer();
    }

}
