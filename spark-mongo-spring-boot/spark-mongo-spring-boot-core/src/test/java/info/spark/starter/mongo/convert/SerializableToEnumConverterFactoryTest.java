package info.spark.starter.mongo.convert;

import info.spark.starter.common.enums.SerializeEnum;
import info.spark.starter.util.core.exception.BaseException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.07 12:07
 * @since 1.0.0
 */
class SerializableToEnumConverterFactoryTest {

    /**
     * 转换实现了 SerializeEnum 接口的枚举
     *
     * @since 1.0.0
     */
    @Test
    void test_1() {
        SerializableToEnumConverterFactory serializableToEnumConverterFactory = new SerializableToEnumConverterFactory();

        // 通过 value 转换
        Assertions.assertEquals(UserType1.SA, serializableToEnumConverterFactory.getConverter(UserType1.class).convert("xxx"));
        Assertions.assertEquals(UserType1.ADMIN, serializableToEnumConverterFactory.getConverter(UserType1.class).convert("yyy"));

        // 通过 name() 转换
        Assertions.assertEquals(UserType1.SA, serializableToEnumConverterFactory.getConverter(UserType1.class).convert("SA"));
        Assertions.assertEquals(UserType1.ADMIN, serializableToEnumConverterFactory.getConverter(UserType1.class).convert("ADMIN"));

        Assertions.assertThrows(BaseException.class, () -> {
            serializableToEnumConverterFactory.getConverter(UserType1.class).convert("zzz");
        });


        // 通过下标转换
        Assertions.assertEquals(UserType1.SA, serializableToEnumConverterFactory.getConverter(UserType1.class).convert(0));
        Assertions.assertEquals(UserType1.ADMIN, serializableToEnumConverterFactory.getConverter(UserType1.class).convert(1));

        Assertions.assertThrows(BaseException.class, () -> {
            serializableToEnumConverterFactory.getConverter(UserType1.class).convert(3);
        });

        Assertions.assertEquals(UserType1.SA, serializableToEnumConverterFactory.getConverter(UserType1.class).convert("0"));
        Assertions.assertEquals(UserType1.ADMIN, serializableToEnumConverterFactory.getConverter(UserType1.class).convert("1"));
    }

    /**
     * 转换未实现 SerializeEnum 接口的枚举
     *
     * @since 1.0.0
     */
    @Test
    void test_2() {
        SerializableToEnumConverterFactory serializableToEnumConverterFactory = new SerializableToEnumConverterFactory();

        // 未实现 SerializeEnum 接口, 无法通过 value 转换
        Assertions.assertThrows(BaseException.class, () -> {
            serializableToEnumConverterFactory.getConverter(UserType2.class).convert("xxx");
        });
        Assertions.assertThrows(BaseException.class, () -> {
            serializableToEnumConverterFactory.getConverter(UserType2.class).convert("yyy");
        });

        // 通过 name() 转换
        Assertions.assertEquals(UserType2.SA, serializableToEnumConverterFactory.getConverter(UserType2.class).convert("SA"));
        Assertions.assertEquals(UserType2.ADMIN, serializableToEnumConverterFactory.getConverter(UserType2.class).convert("ADMIN"));

        Assertions.assertThrows(BaseException.class, () -> {
            serializableToEnumConverterFactory.getConverter(UserType2.class).convert("zzz");
        });

        // 通过下标转换
        Assertions.assertEquals(UserType2.SA, serializableToEnumConverterFactory.getConverter(UserType2.class).convert(0));
        Assertions.assertEquals(UserType2.ADMIN, serializableToEnumConverterFactory.getConverter(UserType2.class).convert(1));

        Assertions.assertThrows(BaseException.class, () -> {
            serializableToEnumConverterFactory.getConverter(UserType2.class).convert(3);
        });

        Assertions.assertEquals(UserType2.SA, serializableToEnumConverterFactory.getConverter(UserType2.class).convert("0"));
        Assertions.assertEquals(UserType2.ADMIN, serializableToEnumConverterFactory.getConverter(UserType2.class).convert("1"));
    }

    /**
     * Test 3
     *
     * @since 1.0.0
     */
    @Test
    void test_3() {
        SerializableToEnumConverterFactory serializableToEnumConverterFactory = new SerializableToEnumConverterFactory();

        // 通过 value 转换
        Assertions.assertEquals(UserType3.SA, serializableToEnumConverterFactory.getConverter(UserType3.class).convert(111));
        Assertions.assertEquals(UserType3.ADMIN, serializableToEnumConverterFactory.getConverter(UserType3.class).convert(222));

        // 通过 name() 转换
        Assertions.assertEquals(UserType3.SA, serializableToEnumConverterFactory.getConverter(UserType3.class).convert("SA"));
        Assertions.assertEquals(UserType3.ADMIN, serializableToEnumConverterFactory.getConverter(UserType3.class).convert("ADMIN"));

        Assertions.assertThrows(BaseException.class, () -> {
            serializableToEnumConverterFactory.getConverter(UserType3.class).convert(333);
        });


        // 通过下标转换
        Assertions.assertEquals(UserType3.SA, serializableToEnumConverterFactory.getConverter(UserType3.class).convert(0));
        Assertions.assertEquals(UserType3.ADMIN, serializableToEnumConverterFactory.getConverter(UserType3.class).convert(1));

        Assertions.assertThrows(BaseException.class, () -> {
            serializableToEnumConverterFactory.getConverter(UserType3.class).convert(3);
        });

        Assertions.assertEquals(UserType3.SA, serializableToEnumConverterFactory.getConverter(UserType3.class).convert("0"));
        Assertions.assertEquals(UserType3.ADMIN, serializableToEnumConverterFactory.getConverter(UserType3.class).convert("1"));
    }

    /**
     * Test 4
     *
     * @since 1.0.0
     */
    @Test
    void test_4() {
        SerializableToEnumConverterFactory serializableToEnumConverterFactory = new SerializableToEnumConverterFactory();

        // 通过 value 转换
        Assertions.assertEquals(UserType4.SA, serializableToEnumConverterFactory.getConverter(UserType4.class).convert(111));
        Assertions.assertEquals(UserType4.ADMIN, serializableToEnumConverterFactory.getConverter(UserType4.class).convert("admin"));

        // 通过 name() 转换
        Assertions.assertEquals(UserType4.SA, serializableToEnumConverterFactory.getConverter(UserType4.class).convert("SA"));
        Assertions.assertEquals(UserType4.ADMIN, serializableToEnumConverterFactory.getConverter(UserType4.class).convert("ADMIN"));

        Assertions.assertThrows(BaseException.class, () -> {
            serializableToEnumConverterFactory.getConverter(UserType4.class).convert("xxxxxxxx");
        });

        // 通过下标转换
        Assertions.assertEquals(UserType4.SA, serializableToEnumConverterFactory.getConverter(UserType4.class).convert(0));
        Assertions.assertEquals(UserType4.ADMIN, serializableToEnumConverterFactory.getConverter(UserType4.class).convert(1));

        Assertions.assertThrows(BaseException.class, () -> {
            serializableToEnumConverterFactory.getConverter(UserType4.class).convert(3);
        });

        Assertions.assertEquals(UserType4.SA, serializableToEnumConverterFactory.getConverter(UserType4.class).convert("0"));
        Assertions.assertEquals(UserType4.ADMIN, serializableToEnumConverterFactory.getConverter(UserType4.class).convert("1"));
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.04.07 14:10
     * @since 1.0.0
     */
    @Getter
    @AllArgsConstructor
    private enum UserType1 implements SerializeEnum<String> {
        /** Sa user type */
        SA("xxx", "普通用户"),
        /** Admin user type */
        ADMIN("yyy", "管理员");

        /** 数据库存储的值 */
        private final String value;
        /** 枚举描述 */
        private final String desc;
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.04.07 15:06
     * @since 1.0.0
     */
    @Getter
    @AllArgsConstructor
    private enum UserType2 {
        /** Sa user type */
        SA("xxx", "普通用户"),
        /** Admin user type */
        ADMIN("yyy", "管理员");

        /** 数据库存储的值 */
        private final String value;
        /** 枚举描述 */
        private final String desc;
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.04.07 15:14
     * @since 1.0.0
     */
    @Getter
    @AllArgsConstructor
    private enum UserType3 implements SerializeEnum<Integer> {
        /** Sa user type */
        SA(111, "普通用户"),
        /** Admin user type */
        ADMIN(222, "管理员");

        /** 数据库存储的值 */
        private final Integer value;
        /** 枚举描述 */
        private final String desc;
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.04.07 15:14
     * @since 1.0.0
     */
    @Getter
    @AllArgsConstructor
    private enum UserType4 implements SerializeEnum<Serializable> {
        /** Sa user type */
        SA(111, "普通用户"),
        /** Admin user type */
        ADMIN("admin", "管理员");

        /** 数据库存储的值 */
        private final Serializable value;
        /** 枚举描述 */
        private final String desc;
    }

}
