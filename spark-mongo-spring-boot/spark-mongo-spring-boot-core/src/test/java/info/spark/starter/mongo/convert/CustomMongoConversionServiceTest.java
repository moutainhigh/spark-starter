package info.spark.starter.mongo.convert;

import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.common.enums.SerializeEnum;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.09 01:52
 * @since 1.0.0
 */
@Slf4j
class CustomMongoConversionServiceTest {

    /**
     * Test convert
     *
     * @since 1.0.0
     */
    @Test
    void test_convert() {

        log.info("{}", CustomMongoConversionService.getInstance().convert(5, UserStatusEnum.class));
        log.info("{}", CustomMongoConversionService.getInstance().convert("CHECKING", UserStatusEnum.class));
        log.info("{}", CustomMongoConversionService.getInstance().convert(UserStatusEnum.CHECK_FAILED, Integer.class));
        log.info("{}", CustomMongoConversionService.getInstance().convert(UserStatusEnum.CHECK_FAILED, String.class));


        String json = "{\n" +
                      "    \"value\": 1,\n" +
                      "    \"desc\": \"xxxx\"\n" +
                      "}";
        log.info("{}", JsonUtils.parse(json, UserStatusEnum.class));

        Assertions.assertThrows(Exception.class, () -> {
            log.info("{}", CustomMongoConversionService.getInstance().convert(json, UserStatusEnum.class));
        });

        log.info("{}", JsonUtils.toJson(UserStatusEnum.CHECK_FAILED));
    }

    /**
         * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.04.04 21:25
     * @since 1.0.0
     */
    @Getter
    @AllArgsConstructor
    public enum UserStatusEnum implements SerializeEnum<Integer> {
        /** Not check user status enum */
        NOT_CHECK(0, "未审核"),
        /** Checking user status enum */
        CHECKING(1, "审核中"),
        /** Check failed user status enum */
        CHECK_FAILED(2, "审核未通过"),
        /** Checked user status enum */
        CHECKED(3, "已锁定"),
        /** Normal user status enum */
        NORMAL(5, "正常状态");

        /** 数据库存储的值 */
        private final Integer value;
        /** 枚举描述 */
        private final String desc;
    }
}
