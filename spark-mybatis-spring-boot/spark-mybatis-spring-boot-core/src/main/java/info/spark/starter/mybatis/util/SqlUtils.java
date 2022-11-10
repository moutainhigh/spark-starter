package info.spark.starter.mybatis.util;

import info.spark.starter.util.AesUtils;
import info.spark.starter.util.Base64Utils;
import info.spark.starter.util.StringUtils;

import org.jetbrains.annotations.Contract;

import lombok.experimental.UtilityClass;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.02.17 18:09
 * @since 1.0.0
 */
@UtilityClass
public class SqlUtils {

    /** SQL_FORMATTER */
    private static final SqlFormatter SQL_FORMATTER = new SqlFormatter();

    /** Sensitive key */
    private static String sensitiveKey;

    /**
     * Set sensitive key
     *
     * @param sensitiveKey sensitive key
     * @since 1.6.0
     */
    public void setSensitiveKey(String sensitiveKey) {
        SqlUtils.sensitiveKey = sensitiveKey;
    }

    /**
     * Get encrypt filed
     * 敏感字段做查询条件需加密后查询
     *
     * @param value value
     * @return the string
     * @since 1.6.0
     * @deprecated 请使用 {@link SqlUtils#encryptFiled(java.lang.String)}
     */
    @Deprecated
    public String getEncryptFiled(String value) {
        return encryptFiled(value);
    }

    /**
     * 加密敏感数据
     *
     * @param value value
     * @return the string
     * @since 1.7.1
     */
    public String encryptFiled(String value) {
        if (StringUtils.isBlank(value) || StringUtils.isBlank(SqlUtils.sensitiveKey)) {
            return value;
        }
        byte[] encrypt = AesUtils.encrypt(value, SqlUtils.sensitiveKey);
        return Base64Utils.encodeToString(encrypt);
    }

    /**
     * 格式sql
     *
     * @param boundSql bound sql
     * @param format   format
     * @return the string
     * @since 1.0.0
     */
    @Contract("_, false -> param1")
    public static String sqlFormat(String boundSql, boolean format) {
        if (format) {
            try {
                return SQL_FORMATTER.format(boundSql);
            } catch (Exception ignored) {
            }
        }
        return boundSql;
    }

}
