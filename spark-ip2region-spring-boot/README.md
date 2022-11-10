# spark-ip2region-spring-boot

Spring Boot Starter For ip2region

### 说明

 > 基于 ip2region 的 Spring Boot Starter 实现

1. 最新IP数据下载地址:  https://github.com/lionsoul2014/ip2region

### Maven

``` xml
<dependency>
	<groupId>info.spark</groupId>
	<artifactId>spark-ip2region-spring-boot-starter</artifactId>
	<version>${last.version}</version>
</dependency>
```

### Sample

```java
@Slf4j
@SparkTest
public class ApplicationTest {
    /** Template */
    @Resource
    private IP2regionTemplate template;

    /**
     * Test template
     *
     * @since 1.7.0
     */
    @SneakyThrows
    @Test
    void test_template() {
        log.info("{}", this.template.btreeSearch("61.94.43.82"));
        log.info("{}", this.template.binarySearch("61.94.43.82"));
        log.info("{}", this.template.binarySearch("127.0.0.1"));
    }
}
```

如果使用外部IP数据, 可自定义配置, 参考如下:

```yaml
spark:
  ip2region:
    external: true
    index-block-size: 4096
    total-header-size: 8192
    location: classpath:includes/ip2region.db
```

