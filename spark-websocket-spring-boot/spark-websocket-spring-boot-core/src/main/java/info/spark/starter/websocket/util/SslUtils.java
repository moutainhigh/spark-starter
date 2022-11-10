package info.spark.starter.websocket.util;

import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.09 20:24
 * @since 2022.1.1
 */
@SuppressWarnings("all")
public final class SslUtils {

    /**
     * Create ssl context
     *
     * @param keyPassword        key password
     * @param keyStoreResource   key store resource
     * @param keyStoreType       key store type
     * @param keyStorePassword   key store password
     * @param trustStoreResource trust store resource
     * @param trustStoreType     trust store type
     * @param trustStorePassword trust store password
     * @return the ssl context
     * @throws SSLException ssl exception
     * @since 2022.1.1
     */
    public static SslContext createSslContext(String keyPassword,
                                              String keyStoreResource,
                                              String keyStoreType,
                                              String keyStorePassword,
                                              String trustStoreResource,
                                              String trustStoreType,
                                              String trustStorePassword) throws SSLException {
        SslContextBuilder sslBuilder = SslContextBuilder
            .forServer(getKeyManagerFactory(keyStoreType, keyStoreResource, keyPassword, keyStorePassword))
            .trustManager(getTrustManagerFactory(trustStoreType, trustStoreResource, trustStorePassword));
        return sslBuilder.build();
    }

    /**
     * Gets key manager factory *
     *
     * @param type             type
     * @param resource         resource
     * @param keyPassword      key password
     * @param keyStorePassword key store password
     * @return the key manager factory
     * @since 2022.1.1
     */
    private static KeyManagerFactory getKeyManagerFactory(String type, String resource, String keyPassword, String keyStorePassword) {
        try {
            KeyStore keyStore = loadKeyStore(type, resource, keyStorePassword);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory
                .getInstance(KeyManagerFactory.getDefaultAlgorithm());
            char[] keyPasswordBytes = (!StringUtils.isEmpty(keyPassword)
                                       ? keyPassword.toCharArray() : null);
            if (keyPasswordBytes == null && !StringUtils.isEmpty(keyStorePassword)) {
                keyPasswordBytes = keyStorePassword.toCharArray();
            }
            keyManagerFactory.init(keyStore, keyPasswordBytes);
            return keyManagerFactory;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Gets trust manager factory *
     *
     * @param trustStoreType     trust store type
     * @param trustStoreResource trust store resource
     * @param trustStorePassword trust store password
     * @return the trust manager factory
     * @since 2022.1.1
     */
    private static TrustManagerFactory getTrustManagerFactory(String trustStoreType,
                                                              String trustStoreResource,
                                                              String trustStorePassword) {
        try {
            KeyStore store = loadKeyStore(trustStoreType, trustStoreResource, trustStorePassword);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(store);
            return trustManagerFactory;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Load key store
     *
     * @param type     type
     * @param resource resource
     * @param password password
     * @return the key store
     * @throws Exception exception
     * @since 2022.1.1
     */
    private static KeyStore loadKeyStore(String type, String resource, String password)
        throws Exception {
        type = (StringUtils.isEmpty(type) ? "JKS" : type);
        if (StringUtils.isEmpty(resource)) {
            return null;
        }
        KeyStore store = KeyStore.getInstance(type);
        URL url = ResourceUtils.getURL(resource);
        store.load(url.openStream(), StringUtils.isEmpty(password) ? null : password.toCharArray());
        return store;
    }
}
