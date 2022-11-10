package info.spark.start.mqtt.autoconfigure;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import info.spark.start.mqtt.core.converter.PayloadDeserialize;
import info.spark.start.mqtt.core.converter.PayloadSerialize;
import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.common.start.SparkAutoConfiguration;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author zhonghaijun
 * @version 1.0.0
 * @email "mailto:zhonghaijun@gmail.com"
 * @date 2021.12.17 14:01
 * @since 2.1.0
 */
@Slf4j
@AutoConfigureAfter(value = {JacksonAutoConfiguration.class, MqttAutoConfiguration.class})
@ConditionalOnClass(ObjectMapper.class)
@ConditionalOnBean(MqttAutoConfiguration.class)
@Configuration(proxyBeanMethods = false)
public class PayloadJacksonAutoConfiguration implements SparkAutoConfiguration {

    /**
     * Object mapper
     *
     * @return the object mapper
     * @since 2.1.0
     */
    @Bean
    @Order(1001)
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper objectMapper() {
        return JsonUtils.getCopyMapper();
    }

    /**
     * Payload serialize
     *
     * @param objectMapper object mapper
     * @return the payload serialize
     * @since 2.1.0
     */
    @Bean
    @Order(1002)
    @ConditionalOnMissingBean(PayloadSerialize.class)
    public PayloadSerialize payloadSerialize(ObjectMapper objectMapper) {
        return source -> {
            try {
                return objectMapper.writeValueAsBytes(source);
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                log.warn("Payload serialize error: {}", e.getMessage(), e);
            }
            return null;
        };
    }

    /**
     * Payload deserialize
     *
     * @param objectMapper object mapper
     * @return the payload deserialize
     * @since 2.1.0
     */
    @Bean
    @Order(1002)
    @ConditionalOnMissingBean(PayloadDeserialize.class)
    public PayloadDeserialize payloadDeserialize(ObjectMapper objectMapper) {
        return new PayloadDeserialize() {
            @Override
            @SuppressWarnings("unchecked")
            public <T> Converter<byte[], T> getConverter(Class<T> targetType) {
                return source -> {
                    try {
                        if (targetType == String.class) {
                            return (T) new String(source, StandardCharsets.UTF_8);
                        }
                        return objectMapper.readValue(source, targetType);
                    } catch (IOException e) {
                        log.warn("Payload deserialize error: {}", e.getMessage(), e);
                    }
                    return null;
                };
            }
        };
    }

    /**
         * <p>Description: </p>
     *
     * @author zhonghaijun
     * @version 1.0.0
     * @email "mailto:zhonghaijun@gmail.com"
     * @date 2021.12.17 14:01
     * @since 2.1.0
     */
    public static class MqttDefaultJacksonModule extends SimpleModule {

        /** VERSION */
        public static final Version VERSION = VersionUtil.parseVersion("1.1.0",
                                                                       "info.spark",
                                                                       "spark-mqtt-spring-boot");

        /** ZONE_ID */
        private static final ZoneId ZONE_ID = ZoneId.of("GMT+8");
        /** DATE_TIME_FORMATTER */
        private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        /** DATE_FORMATTER */
        private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        /** TIME_FORMATTER */
        private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
        /** serialVersionUID */
        private static final long serialVersionUID = 6736053606337307153L;

        /**
         * Mqtt default jackson module
         *
         * @since 2.1.0
         */
        public MqttDefaultJacksonModule() {
            super(VERSION);

            addSerializer(LocalDateTime.class, LOCAL_DATE_TIME_JSON_SERIALIZER);
            addSerializer(LocalDate.class, LOCAL_DATE_JSON_SERIALIZER);
            addSerializer(LocalTime.class, LOCAL_TIME_JSON_SERIALIZER);
            addSerializer(Date.class, DATE_JSON_SERIALIZER);
            addDeserializer(LocalDateTime.class, LOCAL_DATE_TIME_JSON_DESERIALIZER);
            addDeserializer(LocalDate.class, LOCAL_DATE_JSON_DESERIALIZER);
            addDeserializer(LocalTime.class, LOCAL_TIME_JSON_DESERIALIZER);
            addDeserializer(Date.class, DATE_JSON_DESERIALIZER);
        }

        /** LOCAL_DATE_TIME_JSON_SERIALIZER */
        private static final JsonSerializer<LocalDateTime> LOCAL_DATE_TIME_JSON_SERIALIZER = new JsonSerializer<LocalDateTime>() {
            @Override
            public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                if (value == null) {
                    gen.writeNull();
                } else {
                    gen.writeString(value.atZone(ZONE_ID).format(DATE_TIME_FORMATTER));
                }
            }
        };
        /** LOCAL_DATE_JSON_SERIALIZER */
        private static final JsonSerializer<LocalDate> LOCAL_DATE_JSON_SERIALIZER = new JsonSerializer<LocalDate>() {

            @Override
            public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                if (value == null) {
                    gen.writeNull();
                } else {
                    gen.writeString(value.format(DATE_FORMATTER));
                }
            }
        };
        /** LOCAL_TIME_JSON_SERIALIZER */
        private static final JsonSerializer<LocalTime> LOCAL_TIME_JSON_SERIALIZER = new JsonSerializer<LocalTime>() {

            @Override
            public void serialize(LocalTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                if (value == null) {
                    gen.writeNull();
                } else {
                    gen.writeString(value.format(TIME_FORMATTER));
                }
            }
        };
        /** DATE_JSON_SERIALIZER */
        private static final JsonSerializer<Date> DATE_JSON_SERIALIZER = new JsonSerializer<Date>() {

            @Override
            public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                if (value == null) {
                    gen.writeNull();
                } else {
                    gen.writeString(DATE_TIME_FORMATTER.format(value.toInstant().atZone(ZONE_ID)));
                }
            }
        };

        /** LOCAL_DATE_TIME_JSON_DESERIALIZER */
        private static final JsonDeserializer<LocalDateTime> LOCAL_DATE_TIME_JSON_DESERIALIZER = new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                String value = p.getValueAsString();
                if (StringUtils.hasLength(value)) {
                    return LocalDateTime.parse(value, DATE_TIME_FORMATTER);
                }
                return null;
            }
        };
        /** LOCAL_DATE_JSON_DESERIALIZER */
        private static final JsonDeserializer<LocalDate> LOCAL_DATE_JSON_DESERIALIZER = new JsonDeserializer<LocalDate>() {
            @Override
            public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                String value = p.getValueAsString();
                if (StringUtils.hasLength(value)) {
                    return LocalDate.parse(value, DATE_FORMATTER);
                }
                return null;
            }
        };
        /** LOCAL_TIME_JSON_DESERIALIZER */
        private static final JsonDeserializer<LocalTime> LOCAL_TIME_JSON_DESERIALIZER = new JsonDeserializer<LocalTime>() {
            @Override
            public LocalTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                String value = p.getValueAsString();
                if (StringUtils.hasLength(value)) {
                    return LocalTime.parse(value, TIME_FORMATTER);
                }
                return null;
            }
        };
        /** DATE_JSON_DESERIALIZER */
        private static final JsonDeserializer<Date> DATE_JSON_DESERIALIZER = new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                String value = p.getValueAsString();
                if (StringUtils.hasLength(value)) {
                    return Date.from(LocalDateTime.parse(value, DATE_TIME_FORMATTER).atZone(ZONE_ID).toInstant());
                }
                return null;
            }
        };

    }
}
