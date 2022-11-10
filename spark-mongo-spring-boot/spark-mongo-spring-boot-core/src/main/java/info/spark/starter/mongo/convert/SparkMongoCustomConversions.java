package info.spark.starter.mongo.convert;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.04.04 19:53
 * @since 1.0.0
 */
public class SparkMongoCustomConversions extends MongoCustomConversions {

    /**
     * Init list
     *
     * @param customerConverters customer converters
     * @return the list
     * @since 1.0.0
     */
    private static @NotNull List<Object> init(List<Converter<?, ?>> customerConverters) {
        List<Object> converters = new ArrayList<>();
        converters.add(new BooleanToEnumConverter());
        converters.add(new EnumToBooleanConverter());
        converters.add(new EnumToDbConverter());
        converters.add(new DbToEnumConverter());

        converters.addAll(customerConverters);
        return Collections.unmodifiableList(converters);
    }

    /**
     * Spark mongo custom conversions
     *
     * @param converters converters
     * @since 1.0.0
     */
    public SparkMongoCustomConversions(List<Converter<?, ?>> converters) {
        super(init(converters));
    }
}
