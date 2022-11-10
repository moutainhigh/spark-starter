package info.spark.starter.common.mapstruct;

import info.spark.starter.common.entity.dto.UserDTO;
import info.spark.starter.common.entity.enums.GenderEnum;
import info.spark.starter.common.entity.po.User;
import info.spark.starter.common.entity.vo.UserVO;

import info.spark.starter.common.enums.SerializeEnum;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * <p>Description:  user 转换器, 默认提供 4 种转换, 根据业务需求重写转换逻辑 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:09
 * @since 1.0.0
 */
@Mapper(uses = {
    UserAllWrapper.GenderEnumConverter.class,
    DeleteEnumConverter.class,
    EnableEnumConverter.class}
)
public interface UserAllWrapper extends BaseWrapper<UserVO, UserDTO, User> {
    /**
     * vo -> dto: UserWrapper.INSTANCE.dto(vo);
     * dto -> vo: UserWrapper.INSTANCE.vo(dto);
     * dto -> po: UserWrapper.INSTANCE.po(dto);
     * po -> dto: UserWrapper.INSTANCE.dto(po);
     */
    UserAllWrapper INSTANCE = Mappers.getMapper(UserAllWrapper.class);

    /**
     * vo 转 dto
     * target: 需要转换的 source 和 target 字段类型不同, 需要自定义转换逻辑, 使用 {@link EntityEnumConverter}
     * 因为有可能存在多个方法签名一样(方法名不一样), 存在二义性, 因此需要指定自定义方法
     * {@link EntityEnumConverter#descToValue(String)}
     *
     * @param vo the vo
     * @return the d
     * @since 1.0.0
     */
    @Override
    @Mapping(target = "gender", qualifiedByName = {"GenderEnumConverter", "EnumDescToValue"})
    @Mapping(target = "enable", qualifiedByName = {"EnableEnumConverter", "EnumDescToValue"})
    @Mapping(target = "deleted", qualifiedByName = {"DeleteEnumConverter", "EnumDescToValue"})
    UserDTO dto(UserVO vo);

    /**
     * dto 转 vo, 同 vo 转 dto
     * 匹配  {@link EntityEnumConverter#valutToDesc(java.io.Serializable)
     *
     * @param dto the dto
     * @param dto dto
     * @return the v
     * @return the user vo
     * @since 1.0.0*
     * @since 1.9.0
     */
    @Override
    @Mapping(target = "gender", qualifiedByName = {"GenderEnumConverter", "EnumValueToDesc"})
    @Mapping(target = "enable", qualifiedByName = {"EnableEnumConverter", "EnumValueToDesc"})
    @Mapping(target = "deleted", qualifiedByName = {"DeleteEnumConverter", "EnumValueToDesc"})
    UserVO vo(UserDTO dto);

    /**
     * dto 转 po
     * 因为每个枚举都不一样, 因此不存在二义性, 直接使用 {@link EntityEnumConverter} 即可
     * 会自动匹配 {@link EntityEnumConverter#fromValue(java.io.Serializable)
     *
     * @param dto the dto
     * @param dto dto
     * @return the user
     * @return the user
     * @since 1.0.0*
     * @since 1.9.0
     */
    @Override
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "gender", qualifiedByName = {"GenderEnumConverter"})
    @Mapping(target = "enable", qualifiedByName = {"EnableEnumConverter"})
    @Mapping(target = "deleted", qualifiedByName = {"DeleteEnumConverter"})
    User po(UserDTO dto);

    /**
     * po 转 dto
     * 因为每个枚举都不一样, 因此不存在二义性, 直接使用 {@link EntityEnumConverter} 即可
     * 会自动匹配 {@link EntityEnumConverter#toValue(SerializeEnum)
     *
     * @param po the po
     * @param po po
     * @return the user dto
     * @return the user dto
     * @since 1.0.0*
     * @since 1.9.0
     */
    @Override
    @Mapping(target = "gender", qualifiedByName = {"GenderEnumConverter"})
    @Mapping(target = "enable", qualifiedByName = {"EnableEnumConverter"})
    @Mapping(target = "deleted", qualifiedByName = {"DeleteEnumConverter"})
    UserDTO dto(User po);

    /**
     * po 转 vo
     * 自动匹配 {@link EntityEnumConverter#toDesc(SerializeEnum)
     *
     * @param po the po
     * @param po po
     * @return the user vo
     * @return the user vo
     * @since 1.0.0*
     * @since 1.9.0
     */
    @Override
    @Mapping(target = "gender", qualifiedByName = {"GenderEnumConverter"})
    @Mapping(target = "enable", qualifiedByName = {"EnableEnumConverter"})
    @Mapping(target = "deleted", qualifiedByName = {"DeleteEnumConverter"})
    UserVO vo(User po);

    /**
     * vo 转 po
     * 自动匹配 {@link EntityEnumConverter#fromDesc(String)}
     *
     * @param vo the vo
     * @return the user
     * @since 1.0.0
     */
    @Override
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "gender", qualifiedByName = {"GenderEnumConverter"})
    @Mapping(target = "enable", qualifiedByName = {"EnableEnumConverter"})
    @Mapping(target = "deleted", qualifiedByName = {"DeleteEnumConverter"})
    User po(UserVO vo);

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
