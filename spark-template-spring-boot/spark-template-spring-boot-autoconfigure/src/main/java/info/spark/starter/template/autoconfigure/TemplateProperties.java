package info.spark.starter.template.autoconfigure;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.core.convert.converter.Converter;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.04 22:34
 * @since 1.0.0
 */
@Data
@Validated
@ConfigurationProperties(prefix = TemplateProperties.PREFIX, ignoreInvalidFields = true)
public class TemplateProperties {
    /** PREFIX */
    public static final String PREFIX = "spark.template";
    /** Key */
    @NotBlank(message = "key 必须设置")
    private String key;
    /** Hump key */
    private String humpKey;
    /** Enable */
    private final boolean enable = true;
    /** Keys */
    private List<String> keys;
    /** Times */
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration times;
    /** Min weight */
    private Weight minWeight;
    /** Max weight */
    private Weight maxWeight;

    /** Deprecated key */
    private String deprecatedKey;

    /**
     * Gets deprecated key *
     *
     * @return the deprecated key
     * @since 1.5.0
     */
    @DeprecatedConfigurationProperty(reason = "修改配置名", replacement = "newKey")
    public String getDeprecatedKey() {
        return this.deprecatedKey;
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.06.08 13:45
     * @since 1.0.0
     */
    @Data
    static class Weight {
        /** Weight */
        private String weight;

        /**
         * Weight
         *
         * @param weight weight
         * @since 1.5.0
         */
        @Contract(pure = true)
        Weight(String weight) {
            this.weight = weight;
        }
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.06.08 13:45
     * @since 1.0.0
     */
    static class WeightConverter implements Converter<String, Weight> {

        /**
         * Convert
         *
         * @param source source
         * @return the weight
         * @since 1.5.0
         */
        @Override
        public Weight convert(@NotNull String source) {
            return new Weight(source);
        }
    }
}
