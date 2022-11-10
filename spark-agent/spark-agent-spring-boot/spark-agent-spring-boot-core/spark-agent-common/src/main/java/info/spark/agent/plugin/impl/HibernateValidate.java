package info.spark.agent.plugin.impl;

import info.spark.agent.plugin.ApiServiceValidate;
import info.spark.agent.validation.Validater;

import javax.validation.Validator;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.31 11:37
 * @since 1.0.0
 */
@Slf4j
public class HibernateValidate extends Validater implements ApiServiceValidate {
    /**
     * Hibernate validate
     *
     * @param validator validator
     * @since 1.9.0
     */
    public HibernateValidate(Validator validator) {
        super(validator);
    }
}
