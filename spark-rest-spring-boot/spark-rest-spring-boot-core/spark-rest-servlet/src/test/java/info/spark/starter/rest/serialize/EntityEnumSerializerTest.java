package info.spark.starter.rest.serialize;

import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.common.enums.SerializeEnum;
import info.spark.starter.common.enums.serialize.EntityEnumDeserializer;
import info.spark.starter.common.enums.serialize.EntityEnumSerializer;
import info.spark.starter.common.util.GsonUtils;
import info.spark.starter.core.util.EnumUtils;
import info.spark.starter.rest.entity.GenderEnum;
import info.spark.starter.rest.entity.UserForm;
import info.spark.starter.util.Tools;
import info.spark.starter.rest.entity.UserType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.10 15:16
 * @since 1.0.0
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EntityEnumSerializerTest {

    /** Object mapper */
    private final ObjectMapper objectMapper = JsonUtils.getCopyMapper();

    /**
     * 不设置也可以
     *
     * @since 1.0.0
     */
    @BeforeAll
    void init() {
        SimpleModule simpleModule = new SimpleModule("EntityEnum-Converter", PackageVersion.VERSION);
        // 设置枚举序列化/反序列化处理器
        simpleModule.addDeserializer(SerializeEnum.class, new EntityEnumDeserializer<>());
        simpleModule.addSerializer(SerializeEnum.class, new EntityEnumSerializer<>());
        this.objectMapper.registerModule(simpleModule);
        this.objectMapper.findAndRegisterModules();
    }

    /**
     * 将枚举序列化为 json
     *
     * @throws Exception exception
     * @since 1.0.0
     */
    @Test
    @Order(1)
    void object2Json() throws Exception {
        UserForm orderDto = UserForm.builder().date(new Date())
            .gender(GenderEnum.MAN)
            .type(UserType.ADMIN)
            .genderList(new ArrayList<GenderEnum>() {
                private static final long serialVersionUID = -8994472517933682403L;

                {
                    this.add(GenderEnum.MAN);
                    this.add(GenderEnum.UNKNOWN);
                    this.add(GenderEnum.WOMEN);
                }
            })
            .build();

        String json = this.objectMapper.writeValueAsString(orderDto);
        log.info("{}", json);
    }

    /**
     * 直接通过 value 反序列化为枚举
     *
     * @throws Exception exception
     * @since 1.0.0
     */
    @Test
    @Order(2)
    void jsonToObject1() throws Exception {
        String json = "{\"date\":\"2019-10-31 18:36:13\",\"gender\":1,\"type\":\"admin\",\"genderList\":[1,2,0]}";
        UserForm userForm = this.objectMapper.readValue(json, UserForm.class);

        // JsonUtils 没有序列化/反序列化枚举的功能
        log.info("{}", JsonUtils.toJson(userForm, true));

        // 使用 objectMapper 来序列化枚举
        log.info("{}", JsonUtils.toJson(this.objectMapper, userForm, true));
        Assertions.assertEquals(userForm.getGender(), GenderEnum.MAN);
        Assertions.assertEquals(userForm.getType(), UserType.ADMIN);
    }

    @Test
    @Order(2)
    void jsonToObject1_1() throws Exception {
        String json = "{\"date\":\"2019-10-31 18:36:13\",\"gender\":\"1\",\"type\":\"admin\",\"genderList\":[1,2,0]}";
        UserForm userForm = this.objectMapper.readValue(json, UserForm.class);

        // JsonUtils 没有序列化/反序列化枚举的功能
        log.info("{}", JsonUtils.toJson(userForm, true));

        // 使用 objectMapper 来序列化枚举
        log.info("{}", JsonUtils.toJson(this.objectMapper, userForm, true));
        Assertions.assertEquals(userForm.getGender(), GenderEnum.MAN);
        Assertions.assertEquals(userForm.getType(), UserType.ADMIN);
    }

    /**
     * 通过枚举的 json 格式反序列化为枚举
     *
     * @throws Exception exception
     * @since 1.0.0
     */
    @Test
    @Order(3)
    void jsonToObject2() throws Exception {
        String json = "{\n" +
                      "  \"date\": \"2019-10-31 18:36:13\",\n" +
                      "  \"gender\": {\n" +
                      "    \"value\": 0,\n" +
                      "    \"desc\": \"女\"\n" +
                      "  },\n" +
                      "  \"type\": \"\",\n" +
                      "  \"genderList\": [\n" +
                      "    1,\n" +
                      "    2,\n" +
                      "    0\n" +
                      "  ]\n" +
                      "}";
        UserForm userForm = this.objectMapper.readValue(json, UserForm.class);

        log.info("{}", JsonUtils.toJson(userForm, true));

        log.info("{}", JsonUtils.toJson(this.objectMapper, userForm, true));

        Assertions.assertEquals(userForm.getGender(), GenderEnum.WOMEN);
        Assertions.assertNull(userForm.getType());
    }

    /**
     * 通过枚举名反序列化为枚举
     *
     * @throws Exception exception
     * @since 1.0.0
     */
    @Test
    @Order(4)
    void jsonToObject3() throws Exception {
        String json = "{\n" +
                      "  \"date\": \"2019-10-31 18:36:13\",\n" +
                      "  \"gender\": {\n" +
                      "    \"value\": 0,\n" +
                      "    \"desc\": \"女\"\n" +
                      "  },\n" +
                      "  \"type\": \"ADMIN\",\n" +
                      "  \"genderList\": [\n" +
                      "    1,\n" +
                      "    2,\n" +
                      "    0\n" +
                      "  ]\n" +
                      "}";
        UserForm userForm = this.objectMapper.readValue(json, UserForm.class);

        log.info("{}", JsonUtils.toJson(userForm, true));

        log.info("{}", JsonUtils.toJson(this.objectMapper, userForm, true));

        Assertions.assertEquals(userForm.getGender(), GenderEnum.WOMEN);
        Assertions.assertEquals(userForm.getType(), UserType.ADMIN);
    }

    /**
     * 通过枚举的 index 反序列化为枚举
     *
     * @throws Exception exception
     * @since 1.0.0
     */
    @Test
    @Order(5)
    void jsonToObject4() throws Exception {
        String json = "{\n" +
                      "  \"date\": \"2019-10-31 18:36:13\",\n" +
                      "  \"gender\": {\n" +
                      "    \"value\": 0,\n" +
                      "    \"desc\": \"女\"\n" +
                      "  },\n" +
                      "  \"type\": 0,\n" +
                      "  \"genderList\": [\n" +
                      "    1,\n" +
                      "    2,\n" +
                      "    0\n" +
                      "  ]\n" +
                      "}";
        UserForm userForm = this.objectMapper.readValue(json, UserForm.class);

        log.info("{}", JsonUtils.toJson(userForm, true));

        log.info("{}", JsonUtils.toJson(this.objectMapper, userForm, true));

        Assertions.assertEquals(userForm.getGender(), GenderEnum.WOMEN);
        Assertions.assertEquals(userForm.getType(), UserType.SA);
    }

    /**
     * 测试反序列化的优先级 (value > 枚举名 > 枚举索引)
     *
     * @throws Exception exception
     * @since 1.0.0
     */
    @Test
    @Order(6)
    void jsonToObject5() throws Exception {
        String json = "{\n" +
                      "  \"date\": \"2019-10-31 18:36:13\",\n" +
                      "  \"gender\": {\n" +
                      "    \"value\": 0,\n" +
                      "    \"desc\": \"女\"\n" +
                      "  },\n" +
                      "  \"type\": 0,\n" +
                      "  \"genderList\": [\n" +
                      "    1,\n" +
                      "    0,\n" +
                      "    2\n" +
                      "  ]\n" +
                      "}";
        UserForm userForm = this.objectMapper.readValue(json, UserForm.class);

        log.info("{}", JsonUtils.toJson(userForm, true));
        log.info("{}", GsonUtils.toJson(userForm, true));

        log.info("{}", JsonUtils.toJson(this.objectMapper, userForm, true));

        Assertions.assertEquals(userForm.getGender(), GenderEnum.WOMEN);
        Assertions.assertEquals(userForm.getType(), UserType.SA);
        Assertions.assertArrayEquals(userForm.getGenderList().toArray(), Arrays.asList(GenderEnum.MAN, GenderEnum.WOMEN, GenderEnum.UNKNOWN).toArray());
    }


    /**
     * Test
     *
     * @since 1.0.0
     */
    @Test
    @Order(7)
    void test_valueOf() {
        Assertions.assertEquals(GenderEnum.MAN, SerializeEnum.valueOf(GenderEnum.class, 1));
    }

    /**
     * Test 1
     *
     * @since 1.0.0
     */
    @Test
    @Order(8)
    void test_indexOf() {
        GenderEnum genderEnum = EnumUtils.indexOf(GenderEnum.class, 0);
        log.info("{}", genderEnum);
    }

    /**
     * Test 2
     *
     * @since 1.0.0
     */
    @Test
    @Order(9)
    void test_nameOf() {
        GenderEnum genderEnum = EnumUtils.nameOf(GenderEnum.class, "WOMEN");
        log.info("{}", genderEnum);
    }

    /**
     * Test convert
     *
     * @since 1.0.0
     */
    @Test
    void test_convert() {
        log.info("{}", Tools.convert(0, UserStatusEnum.class));
        log.info("{}", Tools.convert("CHECKING", UserStatusEnum.class));
        log.info("{}", Tools.convert(UserStatusEnum.CHECK_FAILED, Integer.class));
        log.info("{}", Tools.convert(UserStatusEnum.CHECK_FAILED, String.class));


        String json = "{\n" +
                      "    \"value\": 1,\n" +
                      "    \"desc\": \"xxxx\"\n" +
                      "}";
        log.info("{}", JsonUtils.parse(json, UserStatusEnum.class));
        log.info("{}", Tools.convert(json, UserStatusEnum.class));
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
        NORMAL(4, "正常状态");

        /** 数据库存储的值 */
        private final Integer value;
        /** 枚举描述 */
        private final String desc;
    }

}
