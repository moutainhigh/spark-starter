package sample.doc.restdoc.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Size;

import lombok.Data;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.15 22:23
 * @since 1.0.0
 */
@Data
public class Category {
    /** Id */
    private Long id;
    /** Name */
    @Size(min = 1, max = 100)
    private String name;

    /**
     * Category
     *
     * @param id   id
     * @param name name
     * @since 1.0.0
     */
    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * JsonCreator的作用是当没有默认的构造函数时,用指定的构造器或者工厂方法来进行反序列化
     *
     * @param id   id
     * @param name name
     * @return category category
     * @since 1.0.0
     */
    @JsonCreator
    public static Category create(@JsonProperty("id") Long id, @JsonProperty("name") String name) {
        return new Category(id, name);
    }
}
