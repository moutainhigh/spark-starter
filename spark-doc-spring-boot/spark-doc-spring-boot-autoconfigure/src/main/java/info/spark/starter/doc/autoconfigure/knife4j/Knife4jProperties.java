package info.spark.starter.doc.autoconfigure.knife4j;

import com.github.xiaoymin.knife4j.core.extend.OpenApiExtendSetting;
import com.github.xiaoymin.knife4j.core.model.MarkdownProperty;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

import lombok.Data;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.4.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.08 04:14
 * @since 1.4.0
 */
@Data
@ConfigurationProperties(prefix = Knife4jProperties.PREFIX)
public class Knife4jProperties {
    /** PREFIX */
    public static final String PREFIX = "spark.doc.knife4j";
    /** 是否开启BasicHttp验证 */
    private Knife4jHttpBasic basic = new Knife4jHttpBasic();
    /** markdown 路径 */
    private String markdowns;
    /** 是否开启Knife4j增强模式 */
    private boolean enable = false;
    /** 是否开启默认跨域 */
    private boolean cors = false;
    /** 是否生产环境 */
    private boolean production = false;
    /** 个性化配置 */
    private OpenApiExtendSetting setting;
    /** 分组文档集合 */
    private List<MarkdownProperty> documents;

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.4.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.05.08 16:02
     * @since 1.4.0
     */
    @Data
    public static class Knife4jHttpBasic {

        /** basic 是否开启,默认为false */
        private boolean enable = false;
        /** basic 用户名 */
        private String username = "spark";
        /** basic 密码 */
        private String password = "spark@0819";
    }
}
