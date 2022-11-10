package info.spark.starter.converter;

import info.spark.starter.common.mapstruct.ServiceConverter;
import info.spark.starter.entity.dto.TestDTO;
import info.spark.starter.entity.po.Test;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * <p>Description: sku 服务层转换器, 提供 po 和 dto 互转 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.10.11 21:33
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface TestServiceConverter extends ServiceConverter<TestDTO, Test> {

    /** INSTANCE */
    TestServiceConverter INSTANCE = Mappers.getMapper(TestServiceConverter.class);
}
