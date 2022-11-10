package info.spark.starter.cloud.nacos.spi;

import com.google.common.collect.Maps;

import com.alibaba.nacos.client.config.impl.Limiter;
import com.alibaba.nacos.common.utils.MD5Utils;
import info.spark.starter.basic.util.Charsets;
import info.spark.starter.common.dns.internal.InetAddressCacheUtils;
import info.spark.starter.util.core.support.ChainMap;
import info.spark.starter.util.DateUtils;
import info.spark.starter.util.ThreadUtils;

import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.MapPropertySource;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.19 17:01
 * @since 1.0.0
 */
@Slf4j
class CloudNacosLauncherInitiationTest {

    /**
     * Test 1
     *
     * @since 1.0.0
     */
    @Test
    void test_1() {
        CloudNacosLauncherInitiation cloudNacosLauncherInitiation = new CloudNacosLauncherInitiation();
        Map<String, Object> source = Maps.newHashMap();
        source.put("spring.cloud.nacos.config.group", "AAA");

        MapPropertySource mapPropertySource = new MapPropertySource("test", source);
        ChainMap chainMap = ChainMap.build();
        cloudNacosLauncherInitiation.processGroup(mapPropertySource, chainMap);

        assertEquals("AAA", chainMap.get("spring.cloud.nacos.config.group"));
        assertEquals("DEFAULT_GROUP", chainMap.get("spring.cloud.nacos.discovery.group"));
    }

    /**
     * Test 2
     *
     * @since 1.0.0
     */
    @Test
    void test_2() {
        CloudNacosLauncherInitiation cloudNacosLauncherInitiation = new CloudNacosLauncherInitiation();
        Map<String, Object> source = Maps.newHashMap();
        source.put("spring.cloud.nacos.config.group", "AAA");
        source.put("spring.cloud.nacos.discovery.group", "BBB");

        MapPropertySource mapPropertySource = new MapPropertySource("test", source);
        ChainMap chainMap = ChainMap.build();
        cloudNacosLauncherInitiation.processGroup(mapPropertySource, chainMap);

        assertEquals("AAA", chainMap.get("spring.cloud.nacos.config.group"));
        assertEquals("BBB", chainMap.get("spring.cloud.nacos.discovery.group"));
    }

    /**
     * Test 3
     *
     * @since 1.0.0
     */
    @Test
    void test_3() {
        CloudNacosLauncherInitiation cloudNacosLauncherInitiation = new CloudNacosLauncherInitiation();
        Map<String, Object> source = Maps.newHashMap();
        source.put("spring.cloud.nacos.config.group", "AAA");
        source.put("spring.cloud.nacos.discovery.group", "BBB");
        source.put("spark.app.group", "CCC");

        MapPropertySource mapPropertySource = new MapPropertySource("test", source);
        ChainMap chainMap = ChainMap.build();
        cloudNacosLauncherInitiation.processGroup(mapPropertySource, chainMap);

        assertEquals("CCC", chainMap.get("spring.cloud.nacos.config.group"));
        assertEquals("CCC", chainMap.get("spring.cloud.nacos.discovery.group"));
    }

    /**
     * Test 4
     *
     * @since 1.0.0
     */
    @Test
    void test_4() {
        CloudNacosLauncherInitiation cloudNacosLauncherInitiation = new CloudNacosLauncherInitiation();
        Map<String, Object> source = Maps.newHashMap();
        source.put("spring.cloud.nacos.config.group", "AAA");
        source.put("spring.cloud.nacos.discovery.group", "BBB");
        source.put("spark.app.config-group", "CCC");
        source.put("spark.app.discovery-group", "DDD");

        MapPropertySource mapPropertySource = new MapPropertySource("test", source);
        ChainMap chainMap = ChainMap.build();
        cloudNacosLauncherInitiation.processGroup(mapPropertySource, chainMap);

        assertEquals("CCC", chainMap.get("spring.cloud.nacos.config.group"));
        assertEquals("DDD", chainMap.get("spring.cloud.nacos.discovery.group"));
    }

    /**
     * Test 5
     *
     * @since 1.0.0
     */
    @Test
    void test_5() {
        CloudNacosLauncherInitiation cloudNacosLauncherInitiation = new CloudNacosLauncherInitiation();
        Map<String, Object> source = Maps.newHashMap();
        source.put("spark.app.group", "CCC");

        MapPropertySource mapPropertySource = new MapPropertySource("test", source);
        ChainMap chainMap = ChainMap.build();
        cloudNacosLauncherInitiation.processGroup(mapPropertySource, chainMap);

        assertEquals("CCC", chainMap.get("spring.cloud.nacos.config.group"));
        assertEquals("CCC", chainMap.get("spring.cloud.nacos.discovery.group"));
    }

    /**
     * Test 6
     *
     * @since 1.0.0
     */
    @Test
    void test_6() {
        CloudNacosLauncherInitiation cloudNacosLauncherInitiation = new CloudNacosLauncherInitiation();
        Map<String, Object> source = Maps.newHashMap();
        source.put("spring.cloud.nacos.config.group", "AAA");
        source.put("spring.cloud.nacos.discovery.group", "BBB");
        source.put("spark.app.config-group", "");
        source.put("spark.app.discovery-group", "");

        MapPropertySource mapPropertySource = new MapPropertySource("test", source);
        ChainMap chainMap = ChainMap.build();
        cloudNacosLauncherInitiation.processGroup(mapPropertySource, chainMap);

        assertEquals("AAA", chainMap.get("spring.cloud.nacos.config.group"));
        assertEquals("BBB", chainMap.get("spring.cloud.nacos.discovery.group"));
    }

    /**
     * Test limiter
     *
     * @since 1.5.0
     */
    @SneakyThrows
    @Test
    void test_limiter() {
        String url = "http://nacos.server:8848/nacos/v1/cs/configs?dataId=repackage-cloud-module.yml&group=SAMPLE&tenant=dong4j";
        List<String> paramValues = new ArrayList<String>() {
            private static final long serialVersionUID = -7388372395030772604L;

            {
                this.add("dataId");
                this.add("repackage-cloud-module.yml");
                this.add("group");
                this.add("SAMPLE");
                this.add("tenant");
                this.add("dong4j");
            }
        };

        String encodedContent = encodingParams(paramValues);

        ThreadUtils.submit(10,
                           () -> {
                               if (Limiter.isLimit(MD5Utils.md5Hex((url + encodedContent).getBytes(StandardCharsets.UTF_8)))) {
                                   log.error("被限流");
                               } else {
                                   log.info("正常请求");
                               }
                           },
                           () -> {

                           });
    }

    /**
     * Encoding params
     *
     * @param paramValues param values
     * @return the string
     * @throws UnsupportedEncodingException unsupported encoding exception
     * @since 1.5.0
     */
    private static @Nullable String encodingParams(List<String> paramValues)
        throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        if (null == paramValues) {
            return null;
        }

        for (Iterator<String> iter = paramValues.iterator(); iter.hasNext(); ) {
            sb.append(iter.next()).append("=");
            sb.append(URLEncoder.encode(iter.next(), Charsets.UTF_8_NAME));
            if (iter.hasNext()) {
                sb.append("&");
            }
        }
        return sb.toString();
    }

    @SneakyThrows
    @Test
    void test_HttpSimpleClient() {
        String url = "http://nacos.server:8848/nacos/v1/cs/configs?dataId=repackage-cloud-module.yml&group=SAMPLE&tenant=dong4j";
        List<String> paramValues = new ArrayList<String>() {
            private static final long serialVersionUID = -7388372395030772604L;

            {
                this.add("dataId");
                this.add("repackage-cloud-module.yml");
                this.add("group");
                this.add("SAMPLE");
                this.add("tenant");
                this.add("dong4j");
            }
        };

        HttpSimpleClient.HttpResult httpResult = HttpSimpleClient.httpGet(url,
                                                                          new ArrayList<>(),
                                                                          paramValues,
                                                                          Charsets.UTF_8_NAME,
                                                                          5000,
                                                                          false);

        log.info(httpResult.content);
    }

    @SneakyThrows
    @Test
    void test_dns() {
        InetAddressCacheUtils.setInetAddressCache("nacos.server",
                                                  new String[] {"192.168.31.9"},
                                                  DateUtils.plusDays(new Date(), 1).getTime());

        this.test_HttpSimpleClient();

    }

    @Test
    void test_socket() {
        try (Socket socket = new Socket()) {
            SocketAddress addr = new InetSocketAddress("127.0.0.1", 8881);
            socket.connect(addr, 1000);
            String hostToBind = socket.getLocalAddress().getHostAddress();
            log.info("[{}]", hostToBind);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

}
