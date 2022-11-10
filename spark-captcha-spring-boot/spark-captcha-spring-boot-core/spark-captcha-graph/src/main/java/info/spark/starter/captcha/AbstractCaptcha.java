package info.spark.starter.captcha;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.common.collect.Lists;

import info.spark.starter.captcha.enhancer.CaptchaCheckEnhancer;
import info.spark.starter.captcha.entity.Captcha;
import info.spark.starter.captcha.entity.CaptchaEnhancerEntity;
import info.spark.starter.captcha.enums.CaptchaCodes;
import info.spark.starter.util.Base64Utils;
import info.spark.starter.util.StringUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.FastByteArrayOutputStream;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.20 18:04
 * @since 1.0.0
 */
public abstract class AbstractCaptcha implements ICaptcha {

    /** Check enhancers */
    @Getter
    protected List<CaptchaCheckEnhancer> checkEnhancers = Lists.newArrayListWithExpectedSize(2);

    /** Captcha cache */
    @Getter
    @Setter
    protected CaptchaCache captchaCache = new LocalMemoryCaptchaCache(CaptchaEnhancerEntity.builder()
                                                                          .captchaExpiresTime(60L)
                                                                          .requestCount(10)
                                                                          .requestCountExpiresTime(240L)
                                                                          .requestFailedCount(5)
                                                                          .requestFailedCountExpiresTime(120L)
                                                                          .build());
    /** Kaptcha */
    private final DefaultKaptcha kaptcha;
    /** Expire time */
    private final Long expireTime;
    /** Time unit */
    private TimeUnit timeUnit;

    /**
     * Instantiates a new Google kaptcha.
     *
     * @param kaptcha    the kaptcha
     * @param expireTime expire time
     * @since 1.0.0
     */
    @Contract(pure = true)
    public AbstractCaptcha(DefaultKaptcha kaptcha, Long expireTime) {
        this.kaptcha = kaptcha;
        this.expireTime = expireTime;
        this.timeUnit = TimeUnit.SECONDS;
    }

    /**
     * Abstract captcha
     *
     * @param kaptcha    kaptcha
     * @param expireTime expire time
     * @param timeUnit   time unit
     * @since 1.0.0
     */
    public AbstractCaptcha(DefaultKaptcha kaptcha, Long expireTime, TimeUnit timeUnit) {
        this(kaptcha, expireTime);
        this.timeUnit = timeUnit;
    }

    /**
     * Render captcha
     *
     * @return the captcha
     * @since 1.0.0
     */
    @Override
    public Captcha render() {
        Captcha captcha = this.renderIo();
        String base64 = "data:image/jpeg;base64," + Base64Utils.encodeToString(captcha.getImageByte());
        return new Captcha(captcha.getUuid(), base64);
    }

    /**
     * Validate *
     *
     * @param uuid uuid
     * @param code code
     * @since 1.0.0
     */
    @Override
    public void validate(String uuid, String code) {
        String cacheCode = this.captchaCache.getCaptcha(uuid);
        CaptchaCodes.CODE_NOT_EXIST.notBlank(code);
        CaptchaCodes.CODE_TIMEOUT.notBlank(cacheCode);
        CaptchaCodes.CODE_ERROR.isTrue(cacheCode.equalsIgnoreCase(code));
        this.captchaCache.deleteCaptcha(uuid);
    }

    /**
     * 直接返回图片流
     *
     * @return the output stream
     * @since 1.0.0
     */
    @Override
    @NotNull
    public Captcha renderIo() {
        Captcha captcha = this.buildImage();
        CaptchaCodes.CODE_BUILD_ERROR.notNull(captcha);
        return Objects.requireNonNull(captcha);
    }

    /**
     * 将 uuid 和 code 存入 redis 后, 生成对应的图片流
     *
     * @return the fast byte array output stream 验证码图片流
     * @since 1.0.0
     */
    @NotNull
    @Contract(" -> new")
    private Captcha buildImage() {
        String captchaCode = this.kaptcha.createText();
        String uuid = StringUtils.randomUid();
        this.captchaCache.setCaptcha(uuid, captchaCode, this.expireTime, this.timeUnit);
        try (FastByteArrayOutputStream baos = new FastByteArrayOutputStream()) {
            ImageIO.write(this.kaptcha.createImage(captchaCode), "jpg", baos);
            return new Captcha(uuid, baos.toByteArray());
        } catch (IOException e) {
            throw CaptchaCodes.CODE_RENDER_ERROR.newException();
        }
    }
}
