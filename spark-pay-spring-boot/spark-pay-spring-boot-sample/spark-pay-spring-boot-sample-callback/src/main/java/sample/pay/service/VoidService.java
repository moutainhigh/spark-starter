package sample.pay.service;

import org.springframework.stereotype.Service;

import sample.pay.entity.dto.BusinessDTO;
import sample.pay.wrapper.BusinessWrapper;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.04.01 21:15
 * @since 1.0.0
 */
@Service
public class VoidService {
    /**
     * Service
     *
     * @since 1.0.0
     */
    public void service() {
        BusinessWrapper.INSTANCE.po(BusinessDTO.builder().say("hahha").build()).insert();
    }
}
