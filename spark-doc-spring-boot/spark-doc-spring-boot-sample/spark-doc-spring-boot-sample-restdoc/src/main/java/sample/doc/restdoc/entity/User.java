package sample.doc.restdoc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
public class User implements Identifiable<String> {
    /** Id */
    private Long id;
    /** Username */
    @Size(min = 1, max = 100)
    private String username;
    /** Email */
    private String email;
    /** Phone */
    private String phone;
    /** User status */
    private Integer userStatus;

    /**
     * Gets user status *
     *
     * @return the user status
     * @since 1.0.0
     */
    @ApiModelProperty(value = "1-registered,2-active,3-closed", allowableValues = "1,2,3")
    public Integer getUserStatus() {
        return this.userStatus;
    }

    /**
     * Gets identifier *
     *
     * @return the identifier
     * @since 1.0.0
     */
    @JsonIgnore
    @Override
    public String getIdentifier() {
        return this.username;
    }
}
