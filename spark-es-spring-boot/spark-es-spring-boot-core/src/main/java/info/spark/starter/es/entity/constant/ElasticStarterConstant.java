package info.spark.starter.es.entity.constant;

/**
 * <p>Description:  </p>
 *
 * @author wanghao
 * @version 1.7.1
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.21 19:52
 * @since 1.7.1
 */
public interface ElasticStarterConstant {

    /** SEARCH */
    String SEARCH = "search";
    /** TEMPLATE */
    String TEMPLATE = "template";
    /** TYPE */
    String TYPE = "type";
    /** BEAN_HOLD_SUFFIX */
    String BEAN_HOLD_SUFFIX = "-EsMapper";
    /** WARNING */
    String WARNING = "若需根据 mapper.xml 调用 ClientInterface 中的某些 API，则接口第一个参数必须为 [DynamicMapperClient] 接口";
    /** 组件自己使用的 dsl.xml 文件路径 */
    String BASE_PATH = "elastic/dsl/basic/basic.xml";
    /** ANALYZED */
    String ANALYZED = "analyzed";
    /** es mapper 固定 mapper.xml 存放位置，classpath：es/mapper/Xxx.xml */
    String RESOURCE_PREFIX = "es/mapper/";
}
