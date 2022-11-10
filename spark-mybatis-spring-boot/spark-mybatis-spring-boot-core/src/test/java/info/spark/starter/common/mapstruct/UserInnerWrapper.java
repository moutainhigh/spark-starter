package info.spark.starter.common.mapstruct;

import info.spark.starter.common.entity.dto.UserDTO;
import info.spark.starter.common.entity.enums.GenderEnum;
import info.spark.starter.common.entity.po.User;

import info.spark.starter.common.enums.SerializeEnum;

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
    UserInnerWrapper.GenderEnumConverter.class,
    DeleteEnumConverter.class,
    EnableEnumConverter.class}
)
public interface UserInnerWrapper extends Converter<User, UserDTO> {

    /**
     * po -> dto: UserInnerWrapper.INSTANCE.to(po);
     * dto -> po: UserInnerWrapper.INSTANCE.from(dto);
     */
    UserInnerWrapper INSTANCE = Mappers.getMapper(UserInnerWrapper.class);

    /**
     * 正向转化 source -> tageter
     * 自动匹配 {@link EntityEnumConverter#toValue(SerializeEnum)
     *
     * @param s the s
     * @return the t
     * @since 1.0.0
     */
    @Override
    @Mapping(target = "gender", qualifiedByName = {"GenderEnumConverter"})
    @Mapping(target = "enable", qualifiedByName = {"EnableEnumConverter"})
    @Mapping(target = "deleted", qualifiedByName = {"DeleteEnumConverter"})
    UserDTO to(User s);

    /**
     * 逆向转化 tageter -> source
     * 自动匹配 {@link EntityEnumConverter#fromValue(Serializable)} (EntityEnum)}
     *
     * @param t the t
     * @return the s
     * @since 1.0.0
     */
    @Override
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "gender", qualifiedByName = {"GenderEnumConverter"})
    @Mapping(target = "enable", qualifiedByName = {"EnableEnumConverter"})
    @Mapping(target = "deleted", qualifiedByName = {"DeleteEnumConverter"})
    User from(UserDTO t);

    /**
         * <p>Description: </p>
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
