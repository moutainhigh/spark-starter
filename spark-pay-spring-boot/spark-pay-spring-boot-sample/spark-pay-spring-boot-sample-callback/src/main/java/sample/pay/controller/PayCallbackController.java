package sample.pay.controller;

import info.spark.starter.basic.Result;
import info.spark.starter.rest.base.BaseController;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import sample.pay.callback.PayCallback;
import sample.pay.entity.form.CallbackForm;
import sample.pay.wrapper.CallbackWrapper;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.04.01 22:15
 * @since 1.0.0
 */
@RestController
public class PayCallbackController extends BaseController {
    /** Pay callback */
    @Resource
    public PayCallback payCallback;

    /**
     * Callback result
     *
     * @param callbackForm callback form
     * @return the result
     * @since 1.0.0
     */
    @PostMapping("/callback")
    public Result callback(@RequestBody CallbackForm callbackForm) {
        return this.ok(this.payCallback.callback(CallbackWrapper.INSTANCE.dto(callbackForm)));
    }
}
