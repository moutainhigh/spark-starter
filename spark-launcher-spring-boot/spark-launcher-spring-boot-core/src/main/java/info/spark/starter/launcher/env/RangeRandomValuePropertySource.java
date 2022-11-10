package info.spark.starter.launcher.env;

import info.spark.starter.common.exception.PropertiesException;
import info.spark.starter.util.core.exception.BaseException;
import info.spark.starter.core.util.EnumUtils;
import info.spark.starter.core.util.NetUtils;
import info.spark.starter.core.util.NumberUtils;
import info.spark.starter.util.RandomUtils;
import info.spark.starter.util.StringUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.boot.env.RandomValuePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: 扩展 {@link RandomValuePropertySource}
 * 以支持 range.random.int(min, max) 和 range.random.key(字符串长度), 可用于随机端口(一个范围内, 如果端口被占用, 可自动使用下一个端口, 直到可用),
 * 原来的 ${random.uuid} 只支持 32 位的小写字母和数字的字符串, 这里扩展为有大写字母且长度可自定义, 可用于自动生成密钥的业务
 * </p>
 *
 * @author zhubo
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.23 14:21
 * @see RandomValuePropertySource
 * @since 1.0.0
 */
@Slf4j
public final class RangeRandomValuePropertySource extends RandomValuePropertySource {

    /** RANGE_RANDOM_PROPERTY_SOURCE_NAME */
    private static final String RANGE_RANDOM_PROPERTY_SOURCE_NAME = "range.random";
    /** PREFIX */
    private static final String PREFIX = "range.random.";
    /** 生成长度小于字符串的随机数字从而生成随机字符串 */
    private static final String CONSTANT_STRING = "0123456789abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    /** DEFAULT_START */
    private static final Integer DEFAULT_START = 8080;
    /** DEFAULT_END */
    private static final Integer DEFAULT_END = 18080;

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.03.31 17:00
     * @since 1.0.0
     */
    public enum RangeType {
        /** Int range type */
        INT,
        /** Key range type */
        KEY,
    }

    /**
     * Range random port value property source
     *
     * @since 1.0.0
     */
    public RangeRandomValuePropertySource() {
        super(RANGE_RANDOM_PROPERTY_SOURCE_NAME);
    }

    /**
     * Gets property *
     *
     * @param name name
     * @return the property
     * @since 1.0.0
     */
    @Override
    public Object getProperty(@NotNull String name) {
        if (!name.startsWith(PREFIX)) {
            return null;
        } else {
            return this.getRangeRandomValue(StringUtils.trimAllWhitespace(name.substring(PREFIX.length())));
        }
    }

    /**
     * Gets range random value *
     *
     * @param type type
     * @return the range random value
     * @since 1.0.0
     */
    private Object getRangeRandomValue(@NotNull String type) {
        RangeType rangeType = EnumUtils.of(RangeType.class, e -> type.toUpperCase().contains(e.name()))
            .orElseThrow(() -> new PropertiesException("目前只支持 range.random.int, range.random.int(min, max), "
                                                       + "range.random.key 和 range.random.key(size)"));

        String range = this.getRange(type, rangeType.name().toLowerCase());

        if (rangeType == RangeType.INT) {
            return this.getIntValueInRange(range);
        } else if (rangeType == RangeType.KEY) {
            return this.getStringInSize(range);
        } else {
            throw new PropertiesException("不支持的 type: [{}]", type);
        }
    }

    /**
     * Gets range *
     *
     * @param type   type
     * @param prefix prefix
     * @return the range
     * @since 1.0.0
     */
    @Nullable
    private String getRange(@NotNull String type, String prefix) {
        if (type.startsWith(prefix)) {
            int startIndex = prefix.length() + 1;
            if (type.length() > startIndex) {
                return type.substring(startIndex, type.length() - 1);
            }
        }
        return null;
    }

    /**
     * 处理 ${range.random.int} 和 ${range.random.int(61000,61100)} 类型的配置
     *
     * @param range range
     * @return the next int in range
     * @since 1.0.0
     */
    @Nullable
    private @Unmodifiable Object getIntValueInRange(String range) {
        if (StringUtils.isBlank(range)) {
            return RangeRandomPort.nextValue(DEFAULT_START, DEFAULT_END);
        }

        if (this.checkPattern(range)) {
            String[] tokens = StringUtils.commaDelimitedListToStringArray(range);
            int start = Integer.parseInt(tokens[0]);
            int end = Integer.parseInt(tokens[1]);
            return RangeRandomPort.nextValue(start, end);
        }

        return null;
    }

    /**
     * Gets string in size *
     *
     * @param range range
     * @return the string in size
     * @since 1.0.0
     */
    @NotNull
    private @Unmodifiable Object getStringInSize(String range) {
        // 默认生成 64 位的随机字符串
        if (StringUtils.isBlank(range)) {
            return this.randomString(64);
        }
        if (NumberUtils.isNumer(range)) {
            // 指定了字符串长度, 生成指定长度字符串
            return this.randomString(NumberUtils.toInt(range));
        }

        throw new PropertiesException("不是正确的字符串长度: [{}]", range);
    }

    /**
     * 生成随机字符串
     *
     * @param length length
     * @return the string
     * @since 1.0.0
     */
    @NotNull
    private String randomString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = this.getSource().nextInt(CONSTANT_STRING.length());
            sb.append(CONSTANT_STRING.charAt(number));
        }
        return sb.toString();
    }

    /**
     * Check pattern boolean
     *
     * @param range range range, 符合格式 61000,61100, 可处理空白符
     * @return the string
     * @since 1.0.0
     */
    private boolean checkPattern(String range) {
        String pattern = "^(\\d+),(\\d+)$";
        if (!Pattern.matches(pattern, range)) {
            throw new PropertiesException("{} 无法解析, 请使用正确的配置", range);
        }
        return true;
    }

    /**
     * 将配置源添加到最后, 优先级最低
     *
     * @param environment environment
     * @since 1.0.0
     */
    public static void addToEnvironment(@NotNull ConfigurableEnvironment environment) {
        environment.getPropertySources().addLast(new RangeRandomValuePropertySource());
    }

    /**
         * <p>Description: </p>
     *
     * @author zhubo
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.03.23 14:21
     * @since 1.0.0
     */
    @Slf4j
    private static class RangeRandomPort {
        /** START */
        private static final int START = 1024;
        /** END */
        private static final int END = 65535;

        /**
         * Next value int
         *
         * @param start start
         * @param end   end
         * @return the int
         * @since 1.0.0
         */
        static int nextValue(Integer start, Integer end) {
            int serverPort;
            start = start == null ? START : Math.max(start, START);
            end = end == null ? END : Math.min(end, END);
            if (start > end) {
                // 说明未找到满足条件的端口, 抛出异常
                throw new BaseException("在端口范围内[" + start + "," + end + "]未找到可用端口, 请修改随机端口范围");
            }
            serverPort = RandomUtils.nextInt(start, end + 1);
            // 端口没有占用, 直接返回
            if (NetUtils.available(serverPort)) {
                log.trace("成功获取可用随机端口:[{}]", serverPort);
                return serverPort;
            }
            // 初始化循环开始数
            serverPort = start;
            // 端口被占用, 则顺延 ManagementEnvironmentCustomizer 往后查找一个可用端口并返回
            while (serverPort <= end) {
                log.trace("随机端口已被占用:[{}]", serverPort);
                if (NetUtils.available(++serverPort)) {
                    log.trace("成功获取可用随机端口:[{}]", serverPort);
                    break;
                }
            }
            if (serverPort == end + 1) {
                // 说明 while 循环结束都没找到可用端口或者最后一次找到了, 但超过了end返回, 仍然不满足条件
                throw new BaseException("在端口范围内[" + start + "," + end + "]未找到可用端口, 请修改随机端口范围");
            }
            return serverPort;
        }
    }
}
