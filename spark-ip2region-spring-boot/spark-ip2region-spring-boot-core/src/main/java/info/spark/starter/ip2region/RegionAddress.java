package info.spark.starter.ip2region;

import org.jetbrains.annotations.Contract;

import lombok.Data;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.12.05 16:20
 * @since 1.7.0
 */
@Data
@SuppressWarnings(value = {"checkstyle:MemberName", "PMD.LowerCamelCaseVariableNamingRule"})
public class RegionAddress {

    /** Country */
    private String country;
    /** Province */
    private String province;
    /** City */
    private String city;
    /** Area */
    private String area;
    /** Isp */
    private String ISP;

    /**
     * Region address
     *
     * @since 1.7.0
     */
    @Contract(pure = true)
    public RegionAddress() {
    }

    /**
     * Translate this string "中国|华东|江苏省|南京市|电信" to location fields.
     *
     * @param region location region address info array
     * @since 1.7.0
     */
    @Contract(pure = true)
    public RegionAddress(String[] region) {
        this(region[0], region[2], region[3], region[1], region[4]);
    }

    /**
     * Basic constructor method
     *
     * @param country  Country name
     * @param province province name
     * @param city     city name
     * @param area     area name
     * @param ISP      ISP name
     * @since 1.7.0
     */
    @Contract(pure = true)
    @SuppressWarnings(value = {"checkstyle:ParameterName", "PMD.LowerCamelCaseVariableNamingRule"})
    public RegionAddress(String country, String province, String city, String area, String ISP) {
        this.country = country;
        this.province = province;
        this.city = city;
        this.area = area;
        this.ISP = ISP;
    }
}
