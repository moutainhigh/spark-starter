package sample.doc.restdoc.entity;

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
public class Tag {
    /** Id */
    private Long id;
    /** Name */
    @Size(min = 1, max = 100)
    private String name;
}

