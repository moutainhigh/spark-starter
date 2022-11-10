package info.spark.starter.common.mapstruct;

import info.spark.starter.common.entity.dto.UserDTO;
import info.spark.starter.common.entity.enums.GenderEnum;
import info.spark.starter.common.entity.vo.UserVO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.io.Serializable;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:09
 * @since 1.0.0
 */
@Mapper(uses = {
    UserOuterWrapper.GenderEnumConverter.class,
    DeleteEnumConverter.class,
    EnableEnumConverter.class}
)
public interface UserOuterWrapper extends Converter<UserVO, UserDTO> {

    /**
     * vo -> dto: UserOuterWrapper.INSTANCE.to(vo);
     * dto -> vo: UserOuterWrapper.INSTANCE.from(dto);
     */
    UserOuterWrapper INSTANCE = Mappers.getMapper(UserOuterWrapper.class);

    /**
     * 正向转化 source -> tageter
     * 具有二义性, 手动指定 {@link EntityEnumConverter#descToValue(String)}
     *
     * @param s the s
     * @return the t
     * @since 1.0.0
     */
    @Override
    @Mapping(target = "gender", qualifiedByName = {"GenderEnumConverter", "EnumDescToValue"})
    @Mapping(target = "enable", qualifiedByName = {"EnableEnumConverter", "EnumDescToValue"})
    @Mapping(target = "deleted", qualifiedByName = {"DeleteEnumConverter", "EnumDescToValue"})
    UserDTO to(UserVO s);

    /**
     * 逆向转化 tageter -> source
     * 具有二义性, 手动指定 {@link EntityEnumConverter#valutToDesc(Serializable)}
     *
     * @param t the t
     * @return the s
     * @since 1.0.0
     */
    @Override
    @Mapping(target = "gender", qualifiedByName = {"GenderEnumConverter", "EnumValueToDesc"})
    @Mapping(target = "enable", qualifiedByName = {"EnableEnumConverter", "EnumValueToDesc"})
    @Mapping(target = "deleted", qualifiedByName = {"DeleteEnumConverter", "EnumValueToDesc"})
    UserVO from(UserDTO t);

    /**
         * <p>Description:  枚举与 value, desc 转换关系 </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.01.27 18:09
     * @since 1.0.0
     */
    @Named("GenderEnumConverter")
    class GenderEnumConverter extends EntityEnumConverter<GenderEnum, Integer> {
        /**
         * Gender enum converter
         *
         * @since 1.9.0
         */
        public GenderEnumConverter() {
            super(GenderEnum.class);
        }
    }
}
