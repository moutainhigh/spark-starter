package info.spark.starter.common.mapstruct;

import info.spark.starter.common.entity.dto.UserDTO;
import info.spark.starter.common.entity.enums.GenderEnum;
import info.spark.starter.common.entity.vo.UserVO;
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
 * @date 2020.01.27 18:23
 * @since 1.0.0
 */
@Slf4j
class UserOuterWrapperTest {
    /**
     * Test vo to dto
     *
     * @since 1.0.0
     */
    @Test
    void testVoToDto() {
        UserVO vo = new UserVO().setGender("男");
        UserDTO dto = UserOuterWrapper.INSTANCE.to(vo);
        log.info("dto = [{}]", dto);
        Assertions.assertEquals(dto.getGender(),
                                EnumUtils.of(GenderEnum.class, g -> g.getDesc().equals(vo.getGender())).orElseThrow(BaseException::new).getValue());
    }

    /**
     * Test dto to vo
     *
     * @since 1.0.0
     */
    @Test
    void testDtoToVo() {
        UserDTO dto = new UserDTO().setGender(1);
        UserVO vo = UserOuterWrapper.INSTANCE.from(dto);
        log.info("vo = [{}]", vo);
        Assertions.assertEquals(vo.getGender(),
         EnumUtils.of(GenderEnum.class, g -> g.getValue().equals(dto.getGender())).orElseThrow(BaseException::new).getDesc());
    }

}
