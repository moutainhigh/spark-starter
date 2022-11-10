package info.spark.starter.es.mapper.structure;

import info.spark.starter.basic.util.JsonUtils;
import info.spark.starter.es.entity.document.BaseElasticEntity;
import info.spark.starter.es.support.ElasticsearchUtils;
import info.spark.starter.util.StringUtils;
import info.spark.starter.es.exception.EsErrorCodes;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>Description: 对应 es mapper 结构 </p>
 *
 * @author wanghao
 * @version 1.7.1
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.22 11:42
 * @since 1.7.1
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Mapper implements Serializable {
    /** serialVersionUID */
    private static final long serialVersionUID = 2704105059916829270L;
    /** Name space */
    private String nameSpace;
    /** Index */
    private String index;
    /** Type */
    private Class<? extends BaseElasticEntity<?>> type;
    /** mapperXml */
    private String mapperXml;
    /** MapperMethod */
    private List<MapperMethod> mapperMethods;
    /** 索引映射 */
    @Setter(AccessLevel.PUBLIC)
    private String mapping;
    /** Name type */
    @Builder.Default
    private Map<String, String> nameType = new HashMap<>(8);

    /**
     * Find mapper method
     *
     * @param name name
     * @return the mapper method
     * @since 1.7.1
     */
    public MapperMethod findMapperMethod(String name) {
        Optional<MapperMethod> first = this.mapperMethods.stream().filter(mm -> mm.getName().equals(name)).findFirst();
        return first.orElseThrow(() -> EsErrorCodes.METHOD_FIND_ERROR.newException(name));
    }

    /**
     * 将Class解析成映射JSONString
     *
     * @since 1.8.0
     */
    public void parseMapping() {
        if (StringUtils.isBlank(this.mapping)) {
            Class<? extends BaseElasticEntity<?>> clazz = this.getType();
            this.mapping = JsonUtils.toJson(ElasticsearchUtils.parseMapping(this, clazz));
        }
    }

}
