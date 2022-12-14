package info.spark.starter.cache.support;

import com.alicp.jetcache.spi.ValueDecoderRegister;
import com.alicp.jetcache.support.AbstractValueDecoder;
import info.spark.starter.processor.annotation.AutoService;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:
 * todo-dong4j : (2020年02月12日 11:43) [未完成]  </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.02.11 22:23
 * @since 1.0.0
 */
@Slf4j
@AutoService(ValueDecoderRegister.class)
public class ProtobufValueDecoder extends SparkAbstractValueDecoder implements ValueDecoderRegister {

    /** INSTANCE */
    public static final ProtobufValueDecoder INSTANCE = new ProtobufValueDecoder();

    /**
     * Protobuf value decoder
     *
     * @since 1.0.0
     */
    public ProtobufValueDecoder() {
        this(Boolean.parseBoolean(System.getProperty("jetcache.useIdentityNumber", "true")));
    }

    /**
     * Protobuf value decoder
     *
     * @param useIdentityNumber use identity number
     * @since 1.0.0
     */
    private ProtobufValueDecoder(boolean useIdentityNumber) {
        super(useIdentityNumber);
    }

    /**
     * Do apply object
     *
     * @param buffer buffer
     * @return the object
     * @throws Exception exception
     * @since 1.0.0
     */
    @Override
    public Object doApply(byte[] buffer) {
        return null;
    }


    /**
     * Identity number int
     *
     * @return the int
     * @since 1.0.0
     */
    @Override
    public int identityNumber() {
        return 0;
    }

    /**
     * Gets decoder *
     *
     * @return the decoder
     * @since 1.0.0
     */
    @Override
    public AbstractValueDecoder getDecoder() {
        return INSTANCE;
    }
}
