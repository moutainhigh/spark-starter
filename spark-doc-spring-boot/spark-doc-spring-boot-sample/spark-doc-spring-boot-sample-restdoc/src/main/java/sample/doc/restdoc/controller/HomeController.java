package sample.doc.restdoc.controller;

import info.spark.starter.basic.Result;
import info.spark.starter.util.core.api.R;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.15 20:48
 * @since 1.0.0
 */
@RestController
public class HomeController {

    /**
     * Greeting map
     *
     * @return the map
     * @since 1.0.0
     */
    @GetMapping("/")
    public Result<Void> greeting() {
        return R.succeed();
    }

}
