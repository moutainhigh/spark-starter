package sample.doc.restdoc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

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
public class Order implements Identifiable<Long> {
    /** Id */
    private Long id;
    /** Pet id */
    private Long petId;
    /** Quantity */
    @Min(1)
    @Max(100)
    private Integer quantity;
    /** Ship date */
    private Date shipDate;
    /** Status */
    private String status;
    /** Complete */
    private Boolean complete;

    /**
     * Gets status *
     *
     * @return the status
     * @since 1.0.0
     */
    @ApiModelProperty(value = "Order Status", allowableValues = "placed, approved, delivered")
    public String getStatus() {
        return this.status;
    }

    /**
     * Gets identifier *
     *
     * @return the identifier
     * @since 1.0.0
     */
    @JsonIgnore
    @Override
    public Long getIdentifier() {
        return this.id;
    }
}
