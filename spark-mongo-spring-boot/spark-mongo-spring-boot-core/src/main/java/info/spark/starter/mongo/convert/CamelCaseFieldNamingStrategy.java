package info.spark.starter.mongo.convert;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.CamelCaseAbbreviatingFieldNamingStrategy;
import org.springframework.data.mapping.model.CamelCaseSplittingFieldNamingStrategy;
import org.springframework.data.mapping.model.FieldNamingStrategy;
import org.springframework.data.mapping.model.SnakeCaseFieldNamingStrategy;
import org.springframework.data.util.ParsingUtils;

import java.util.Iterator;
import java.util.List;

/**
 * <p>Description:
 * 自定义策略, 目的: myName -> MY_NAME, 其他自带的策略:
 * {@link CamelCaseAbbreviatingFieldNamingStrategy}: 首字母大写
 * {@link CamelCaseSplittingFieldNamingStrategy}: 分隔驼峰命名, 传入自定义分隔符
 * {@link SnakeCaseFieldNamingStrategy}: 下划线风格
 * </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.17 11:17
 * @since 1.0.0
 */
public class CamelCaseFieldNamingStrategy implements FieldNamingStrategy {
    /**
     * Gets field name *
     *
     * @param property property
     * @return the field name
     * @since 1.0.0
     */
    @NotNull
    @Override
    public String getFieldName(@NotNull PersistentProperty<?> property) {
        List<String> parts = ParsingUtils.splitCamelCaseToLower(property.getName());
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = parts.iterator();
        if (it.hasNext()) {
            sb.append(it.next().toUpperCase());
            while (it.hasNext()) {
                sb.append("_");
                sb.append(it.next().toUpperCase());
            }
        }
        return sb.toString();
    }
}
