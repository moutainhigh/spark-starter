package info.spark.start.mqtt.core.converter;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author zhonghaijun
 * @version 1.0.0
 * @email "mailto:zhonghaijun@gmail.com"
 * @date 2021.12.17 11:25
 * @since 2.1.0
 */
@Slf4j
public class MqttConverterService extends GenericConversionService {


    /** sharedInstance */
    private static volatile MqttConverterService sharedInstance;

    /**
     * Mqtt converter service
     *
     * @since 2.1.0
     */
    public MqttConverterService() {
        configure(this);
    }

    /**
     * Gets shared instance *
     *
     * @return the shared instance
     * @since 2.1.0
     */
    public static MqttConverterService getSharedInstance() {
        if (sharedInstance == null) {
            synchronized (MqttConverterService.class) {
                if (sharedInstance == null) {
                    sharedInstance = new MqttConverterService();
                }
            }
        }
        return sharedInstance;
    }


    /**
     * Configure
     *
     * @param registry registry
     * @since 2.1.0
     */
    public static void configure(ConverterRegistry registry) {
        DefaultConversionService.addDefaultConverters(registry);
        registry.addConverter((StringToByteArrayConverter) source -> source.getBytes(StandardCharsets.UTF_8));
        registry.addConverter((ByteArrayToStringConverter) source -> new String(source, StandardCharsets.UTF_8));
        registry.addConverter((ByteArrayToBooleanConverter) source -> Boolean.parseBoolean(new String(source, StandardCharsets.UTF_8)));
        registry.addConverter((ByteArrayToByteConverter) source -> Byte.parseByte(new String(source, StandardCharsets.UTF_8)));
        registry.addConverter((ByteArrayToShortConverter) source -> Short.parseShort(new String(source, StandardCharsets.UTF_8)));
        registry.addConverter((ByteArrayToIntegerConverter) source -> Integer.parseInt(new String(source, StandardCharsets.UTF_8)));
        registry.addConverter((ByteArrayToLongConverter) source -> Long.parseLong(new String(source, StandardCharsets.UTF_8)));
        registry.addConverter((ByteArrayToFloatConverter) source -> Float.parseFloat(new String(source, StandardCharsets.UTF_8)));
        registry.addConverter((ByteArrayToDoubleConverter) source -> Double.parseDouble(new String(source, StandardCharsets.UTF_8)));
    }

    /**
     * Add configure
     *
     * @param registry    registry
     * @param beanFactory bean factory
     * @since 2.1.0
     */
    public static void addConfigure(ConverterRegistry registry, ListableBeanFactory beanFactory) {
        Set<Object> beans = new LinkedHashSet<>();
        beans.addAll(beanFactory.getBeansOfType(PayloadDeserialize.class).values());
        beans.addAll(beanFactory.getBeansOfType(PayloadSerialize.class).values());
        beans.addAll(beanFactory.getBeansOfType(ConverterFactory.class).values());
        beans.addAll(beanFactory.getBeansOfType(GenericConverter.class).values());
        beans.addAll(beanFactory.getBeansOfType(Converter.class).values());
        for (Object bean : beans) {
            if (bean instanceof PayloadDeserialize) {
                registry.addConverterFactory((PayloadDeserialize) bean);
            } else if (bean instanceof ConverterFactory) {
                registry.addConverterFactory((ConverterFactory<?, ?>) bean);
            } else if (bean instanceof PayloadSerialize) {
                registry.addConverter((PayloadSerialize) bean);
            } else if (bean instanceof GenericConverter) {
                registry.addConverter((GenericConverter) bean);
            } else if (bean instanceof Converter) {
                registry.addConverter((Converter<?, ?>) bean);
            }
        }
    }


    /**
     * mqtt消息转换，如果都不能转换，直接转换为String类型
     *
     * @param source     source
     * @param target     target
     * @param converters converters
     * @return the object
     * @since 2.1.0
     */
    public Object fromBytes(byte[] source,
                            Class<?> target,
                            List<Converter<Object, Object>> converters) {
        if (source == null) {
            return null;
        }
        Object payload = source;
        if (converters != null && converters.size() > 0) {
            for (Converter<Object, Object> converter : converters) {
                try {
                    if (payload == null) {
                        log.warn("Execute covert {} return null.", converter.getClass().getName());
                        return null;
                    }
                    payload = converter.convert(payload);
                } catch (Exception e) {
                    log.error("转换器执行失败：{}", e.getMessage());
                }
            }
        }
        if (payload == null) {
            return null;
        }
        Class<?> payloadClass = payload.getClass();
        if (payloadClass == target) {
            return payload;
        } else {
            if (this.canConvert(payloadClass, target)) {
                return this.convert(source, target);
            }
            if (this.canConvert(payloadClass, String.class)) {
                return this.convert(payload, String.class);
            } else {
                log.warn("Unsupported covert from {} to {}", payloadClass.getName(), target.getName());
                return null;
            }
        }
    }

    /**
     * To bytes
     *
     * @param source source
     * @return the byte [ ]
     * @since 2.1.0
     */
    public byte[] toBytes(Object source) {
        Class<?> sourceClass = source.getClass();
        Assert.notNull(source, "数据不能为空！！！");
        if (this.canConvert(sourceClass, byte[].class)) {
            return this.convert(source, byte[].class);
        } else if (this.canConvert(sourceClass, String.class)) {
            String convert = this.convert(source, String.class);
            if (StringUtils.isEmpty(convert)) {
                log.error("数据转换失败");
                return null;
            }
            return convert.getBytes(StandardCharsets.UTF_8);
        } else {
            log.warn("Unsupported covert from {} to {}", sourceClass.getName(), byte[].class);
            return null;
        }
    }


    /**
         * <p>Description: </p>
     *
     * @author zhonghaijun
     * @version 1.0.0
     * @email "mailto:zhonghaijun@gmail.com"
     * @date 2021.12.17 11:34
     * @since 2.1.0
     */
    interface StringToByteArrayConverter extends Converter<String, byte[]> {
    }

    /**
         * <p>Description: </p>
     *
     * @author zhonghaijun
     * @version 1.0.0
     * @email "mailto:zhonghaijun@gmail.com"
     * @date 2021.12.17 11:34
     * @since 2.1.0
     */
    interface ByteArrayToStringConverter extends Converter<byte[], String> {
    }

    /**
         * <p>Description: </p>
     *
     * @author zhonghaijun
     * @version 1.0.0
     * @email "mailto:zhonghaijun@gmail.com"
     * @date 2021.12.17 11:34
     * @since 2.1.0
     */
    interface ByteArrayToBooleanConverter extends Converter<byte[], Boolean> {
    }

    /**
         * <p>Description: </p>
     *
     * @author zhonghaijun
     * @version 1.0.0
     * @email "mailto:zhonghaijun@gmail.com"
     * @date 2021.12.17 11:34
     * @since 2.1.0
     */
    interface ByteArrayToByteConverter extends Converter<byte[], Byte> {
    }

    /**
         * <p>Description: </p>
     *
     * @author zhonghaijun
     * @version 1.0.0
     * @email "mailto:zhonghaijun@gmail.com"
     * @date 2021.12.17 11:34
     * @since 2.1.0
     */
    interface ByteArrayToShortConverter extends Converter<byte[], Short> {
    }

    /**
         * <p>Description: </p>
     *
     * @author zhonghaijun
     * @version 1.0.0
     * @email "mailto:zhonghaijun@gmail.com"
     * @date 2021.12.17 11:34
     * @since 2.1.0
     */
    interface ByteArrayToIntegerConverter extends Converter<byte[], Integer> {
    }

    /**
         * <p>Description: </p>
     *
     * @author zhonghaijun
     * @version 1.0.0
     * @email "mailto:zhonghaijun@gmail.com"
     * @date 2021.12.17 11:34
     * @since 2.1.0
     */
    interface ByteArrayToLongConverter extends Converter<byte[], Long> {
    }

    /**
         * <p>Description: </p>
     *
     * @author zhonghaijun
     * @version 1.0.0
     * @email "mailto:zhonghaijun@gmail.com"
     * @date 2021.12.17 11:34
     * @since 2.1.0
     */
    interface ByteArrayToFloatConverter extends Converter<byte[], Float> {
    }

    /**
         * <p>Description: </p>
     *
     * @author zhonghaijun
     * @version 1.0.0
     * @email "mailto:zhonghaijun@gmail.com"
     * @date 2021.12.17 11:34
     * @since 2.1.0
     */
    interface ByteArrayToDoubleConverter extends Converter<byte[], Double> {
    }

}
