package info.spark.starter.captcha.entity;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

/**
 * <p>Description: 验证码对象封装,用于传递到前端 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.01 09:41
 * @since 1.0.0
 */
@Getter
@ApiModel("验证码模型")
public class Captcha implements Serializable {
    /** serialVersionUID */
    private static final long serialVersionUID = 1171963899353665502L;
    /** 用于传给前端,校验时携带 */
    @ApiModelProperty("验证码唯一id")
    private final String uuid;
    /** 图片 base64 */
    @ApiModelProperty("验证码图片（base64), /render 接口使用")
    private String base64;
    /** Image byte */
    @ApiModelProperty("图片 IO 流, /render/io 接口使用")
    private byte[] imageByte;

    /**
     * Captcha
     *
     * @param uuid   uuid
     * @param base64 base 64
     * @since 1.0.0
     */
    @Contract(pure = true)
    public Captcha(String uuid, String base64) {
        this.uuid = uuid;
        this.base64 = base64;
    }

    /**
     * Captcha
     *
     * @param uuid      uuid
     * @param imageByte image byte
     * @since 1.0.0
     */
    @Contract(pure = true)
    public Captcha(String uuid, byte[] imageByte) {
        this.uuid = uuid;
        this.imageByte = imageByte;
    }
}
