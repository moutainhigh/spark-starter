package info.spark.starter.websocket.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.09 19:55
 * @since 2022.1.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ServerEndpoint {

    /**
     * Value
     *
     * @return the string
     * @since 2022.1.1
     */
    @AliasFor("path")
    String value() default "/";

    /**
     * Path
     *
     * @return the string
     * @since 2022.1.1
     */
    @AliasFor("value")
    String path() default "/";

    /**
     * Host
     *
     * @return the string
     * @since 2022.1.1
     */
    String host() default "0.0.0.0";

    /**
     * Port
     *
     * @return the string
     * @since 2022.1.1
     */
    String port() default "80";

    /**
     * Boss loop group threads
     *
     * @return the string
     * @since 2022.1.1
     */
    String bossLoopGroupThreads() default "1";

    /**
     * Worker loop group threads
     *
     * @return the string
     * @since 2022.1.1
     */
    String workerLoopGroupThreads() default "0";

    /**
     * Use compression handler
     *
     * @return the string
     * @since 2022.1.1
     */
    String useCompressionHandler() default "false";

    //------------------------- option -------------------------

    /**
     * Option connect timeout millis
     *
     * @return the string
     * @since 2022.1.1
     */
    String optionConnectTimeoutMillis() default "30000";

    /**
     * Option so backlog
     *
     * @return the string
     * @since 2022.1.1
     */
    String optionSoBacklog() default "128";

    //------------------------- childOption -------------------------

    /**
     * Child option write spin count
     *
     * @return the string
     * @since 2022.1.1
     */
    String childOptionWriteSpinCount() default "16";

    /**
     * Child option write buffer high water mark
     *
     * @return the string
     * @since 2022.1.1
     */
    String childOptionWriteBufferHighWaterMark() default "65536";

    /**
     * Child option write buffer low water mark
     *
     * @return the string
     * @since 2022.1.1
     */
    String childOptionWriteBufferLowWaterMark() default "32768";

    /**
     * Child option so rcvbuf
     *
     * @return the string
     * @since 2022.1.1
     */
    String childOptionSoRcvbuf() default "-1";

    /**
     * Child option so sndbuf
     *
     * @return the string
     * @since 2022.1.1
     */
    String childOptionSoSndbuf() default "-1";

    /**
     * Child option tcp nodelay
     *
     * @return the string
     * @since 2022.1.1
     */
    String childOptionTcpNodelay() default "true";

    /**
     * Child option so keepalive
     *
     * @return the string
     * @since 2022.1.1
     */
    String childOptionSoKeepalive() default "false";

    /**
     * Child option so linger
     *
     * @return the string
     * @since 2022.1.1
     */
    String childOptionSoLinger() default "-1";

    /**
     * Child option allow half closure
     *
     * @return the string
     * @since 2022.1.1
     */
    String childOptionAllowHalfClosure() default "false";

    //------------------------- idleEvent -------------------------

    /**
     * Reader idle time seconds
     *
     * @return the string
     * @since 2022.1.1
     */
    String readerIdleTimeSeconds() default "0";

    /**
     * Writer idle time seconds
     *
     * @return the string
     * @since 2022.1.1
     */
    String writerIdleTimeSeconds() default "0";

    /**
     * All idle time seconds
     *
     * @return the string
     * @since 2022.1.1
     */
    String allIdleTimeSeconds() default "0";

    //------------------------- handshake -------------------------

    /**
     * Max frame payload length
     *
     * @return the string
     * @since 2022.1.1
     */
    String maxFramePayloadLength() default "65536";

    //------------------------- eventExecutorGroup -------------------------

    /**
     * Use event executor group
     *
     * @return the string
     * @since 2022.1.1
     */
    String useEventExecutorGroup() default "true"; //use EventExecutorGroup(another thread pool) to perform time-consuming synchronous
    // business logic

    /**
     * Event executor group threads
     *
     * @return the string
     * @since 2022.1.1
     */
    String eventExecutorGroupThreads() default "16";

    //------------------------- ssl (refer to spring Ssl) -------------------------

    /**
     * {@link org.springframework.boot.web.server.Ssl}
     *
     * @return the string
     * @since 2022.1.1
     */
    String sslKeyPassword() default "";

    /**
     * Ssl key store
     *
     * @return the string
     * @since 2022.1.1
     */
    String sslKeyStore() default "";            //e.g. classpath:server.jks

    /**
     * Ssl key store password
     *
     * @return the string
     * @since 2022.1.1
     */
    String sslKeyStorePassword() default "";

    /**
     * Ssl key store type
     *
     * @return the string
     * @since 2022.1.1
     */
    String sslKeyStoreType() default "";        //e.g. JKS

    /**
     * Ssl trust store
     *
     * @return the string
     * @since 2022.1.1
     */
    String sslTrustStore() default "";

    /**
     * Ssl trust store password
     *
     * @return the string
     * @since 2022.1.1
     */
    String sslTrustStorePassword() default "";

    /**
     * Ssl trust store type
     *
     * @return the string
     * @since 2022.1.1
     */
    String sslTrustStoreType() default "";

    //------------------------- cors (refer to spring CrossOrigin) -------------------------

    /**
     * {@link org.springframework.web.bind.annotation.CrossOrigin}
     *
     * @return the string [ ]
     * @since 2022.1.1
     */
    String[] corsOrigins() default {};

    /**
     * Cors allow credentials
     *
     * @return the string
     * @since 2022.1.1
     */
    String corsAllowCredentials() default "";


}
