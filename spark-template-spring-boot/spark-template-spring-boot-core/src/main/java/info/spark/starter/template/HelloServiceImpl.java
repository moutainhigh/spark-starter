package info.spark.starter.template;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.06.04 22:30
 * @since 1.0.0
 */
public class HelloServiceImpl implements HelloService {
    /**
     * Echo
     *
     * @return the string
     * @since 1.0.0
     */
    @Override
    public String echo() {
        return "hello world";
    }
}
