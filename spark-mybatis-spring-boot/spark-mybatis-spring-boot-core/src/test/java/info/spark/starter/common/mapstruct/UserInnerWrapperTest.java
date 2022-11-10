package info.spark.starter.common.mapstruct;

import info.spark.starter.common.entity.dto.UserDTO;
import info.spark.starter.common.entity.enums.GenderEnum;
import info.spark.starter.common.entity.po.User;
import info.spark.starter.util.core.exception.BaseException;
import info.spark.starter.core.util.EnumUtils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.26 20:40
 * @since 1.0.0
 */
@Slf4j
class UserInnerWrapperTest {
    /**
     * Test vo to dto
     *
     * @since 1.0.0
     */
    @Test
    void testVoToDto() {
        User po = new User().setGender(GenderEnum.MAN);
        UserDTO dto = UserInnerWrapper.INSTANCE.to(po);
        log.info("dto = [{}]", dto);
        Assertions.assertEquals(dto.getGender(), po.getGender().getValue());
    }

    /**
     * Test dto to vo
     *
     * @since 1.0.0
     */
    @Test
    void testDtoToVo() {
        UserDTO dto = new UserDTO().setGender(1);
        User po = UserInnerWrapper.INSTANCE.from(dto);
        log.info("po = [{}]", po);
        Assertions.assertEquals(po.getGender(),
                                EnumUtils.of(GenderEnum.class, g -> g.getValue().equals(dto.getGender())).orElseThrow(BaseException::new));
    }
}
