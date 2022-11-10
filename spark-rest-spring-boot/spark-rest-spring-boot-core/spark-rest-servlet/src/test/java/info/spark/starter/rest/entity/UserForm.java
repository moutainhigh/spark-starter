package info.spark.starter.rest.entity;

import java.io.*;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:17
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserForm implements Serializable {
    /** serialVersionUID */
    private static final long serialVersionUID = 3457453077473375609L;

    /** Date */
    private Date date;

    /** Gender */
    private GenderEnum gender;
    /** Type */
    private UserType type;
    /** Gender list */
    private List<GenderEnum> genderList;
}
