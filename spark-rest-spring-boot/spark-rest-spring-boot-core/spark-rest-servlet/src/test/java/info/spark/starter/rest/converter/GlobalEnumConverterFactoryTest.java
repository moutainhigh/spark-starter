package info.spark.starter.rest.converter;

import info.spark.starter.rest.entity.UserType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Objects;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.10 15:15
 * @since 1.0.0
 */
class GlobalEnumConverterFactoryTest {

    /**
     * Test
     *
     * @since 1.0.0
     */
    @Test
    void test() {
        GlobalEnumConverterFactory globalEnumConverterFactory = new GlobalEnumConverterFactory();

        // 通过 getValue() 查找枚举
        UserType userType = globalEnumConverterFactory.getConverter(UserType.class).convert("sa");
        String desc = Objects.requireNonNull(userType).getDesc();
        Assertions.assertEquals(UserType.SA.getDesc(), desc);

        // 通过 name() 查找枚举
        UserType userType1 = globalEnumConverterFactory.getConverter(UserType.class).convert("SA");
        String desc3 = Objects.requireNonNull(userType1).getDesc();
        Assertions.assertEquals(UserType.SA.getDesc(), desc3);

        // 通过 ordinal() 查找枚举
        UserType userType2 = globalEnumConverterFactory.getConverter(UserType.class).convert("0");
        String desc4 = Objects.requireNonNull(userType2).getDesc();
        Assertions.assertEquals(UserType.SA.getDesc(), desc4);
    }
}
