package sample.doc.restdoc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import sample.doc.restdoc.repository.Identifiable;

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
public class Pet implements Identifiable<Long> {
    /** Id */
    private Long id;
    /** Category */
    private Category category;
    /** Name */
    @Size(min = 1, max = 100)
    private String name;
    /** Photo urls */
    private List<String> photoUrls = new ArrayList<>();
    /** Tags */
    private List<Tag> tags = new ArrayList<>();
    /** Status */
    private String status;

    /**
     * Gets status *
     *
     * @return the status
     * @since 1.0.0
     */
    @ApiModelProperty(value = "Pet Status", allowableValues = "available,pending,sold")
    public String getStatus() {
        return this.status;
    }

    /**
     * Gets identifier *
     *
     * @return the identifier
     * @since 1.0.0
     */
    @Override
    @JsonIgnore
    public Long getIdentifier() {
        return this.id;
    }
}
