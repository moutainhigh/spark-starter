package sample.pay.wrapper;

import info.spark.starter.common.mapstruct.ViewConverterWrapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import sample.pay.entity.dto.CallbackDTO;
import sample.pay.entity.form.CallbackForm;
import sample.pay.entity.vo.CallbackVO;

/**
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.01 22:03
 * @since 1.0.0
 */
@Mapper
public interface CallbackWrapper extends ViewConverterWrapper<CallbackForm, CallbackDTO, CallbackVO> {

    /** INSTANCE */
    CallbackWrapper INSTANCE = Mappers.getMapper(CallbackWrapper.class);
}
