package info.spark.starter.mongo.convert;

import info.spark.starter.common.enums.SerializeEnum;

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
 * @date 2020.04.07 15:49
 * @since 1.0.0
 */
class EnumToGenericConverterFactoryTest {

    /**
     * Test 1
     *
     * @since 1.0.0
     */
    @Test
    void test_1() {
        EnumToSerializableConverter enumToSerializableConverter = new EnumToSerializableConverter();
        // 实现了 SerializeEnum 接口, 转换为 value
        Assertions.assertEquals("xxx", enumToSerializableConverter.convert(UserType1.SA));
        Assertions.assertEquals("yyy", enumToSerializableConverter.convert(UserType1.ADMIN));
        Assertions.assertNotEquals("zzz", enumToSerializableConverter.convert(UserType1.ADMIN));

    }

    /**
     * Test 2
     *
     * @since 1.0.0
     */
    @Test
    void test_2() {
        EnumToSerializableConverter enumToSerializableConverter = new EnumToSerializableConverter();

        // 未实现 SerializeEnum 接口, 转换为 name
        Assertions.assertEquals("SA", enumToSerializableConverter.convert(UserType2.SA));
        Assertions.assertEquals("ADMIN", enumToSerializableConverter.convert(UserType2.ADMIN));
    }

    /**
     * Test 1
     *
     * @since 1.0.0
     */
    @Test
    void test_3() {
        EnumToSerializableConverter enumToSerializableConverter = new EnumToSerializableConverter();

        // 实现了 SerializeEnum 接口, 转换为 value
        Assertions.assertEquals(111, enumToSerializableConverter.convert(UserType4.SA));
        Assertions.assertEquals("admin", enumToSerializableConverter.convert(UserType4.ADMIN));
        Assertions.assertNotEquals("zzz", enumToSerializableConverter.convert(UserType4.ADMIN));
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.04.07 15:57
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
     * @date 2020.04.07 15:57
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
     * @date 2020.04.07 15:57
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
