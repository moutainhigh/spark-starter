package sample.pay.wrapper;

import info.spark.starter.common.mapstruct.BaseWrapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import sample.pay.entity.dto.BusinessDTO;
import sample.pay.entity.po.Business;
import sample.pay.entity.vo.BusinessVO;

/**
 * <p>Description: 其他业务表 转换器, 默认提供 4 种转换, 根据业务需求重写转换逻辑或新增转换接口 </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.01 22:12
 * @since 1.0.0
 */
@Mapper
public interface BusinessWrapper extends BaseWrapper<BusinessVO, BusinessDTO, Business> {

    /** INSTANCE */
    BusinessWrapper INSTANCE = Mappers.getMapper(BusinessWrapper.class);
}
