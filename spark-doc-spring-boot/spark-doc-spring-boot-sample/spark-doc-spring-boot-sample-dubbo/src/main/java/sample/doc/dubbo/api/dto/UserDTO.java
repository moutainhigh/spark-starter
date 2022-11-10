package sample.doc.dubbo.api.dto;

import java.io.*;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.26 20:42
 * @since 1.4.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "用户")
public class UserDTO implements Serializable {
    /** serialVersionUID */
    private static final long serialVersionUID = 7332438081649173843L;
    /** Id */
    @ApiModelProperty(name = "id", required = true, example = "1024")
    private Long id;
    /** Name */
    @ApiModelProperty(value = "用户名称", required = true, example = "dong4j")
    private String name;
    /** Site */
    private String site;
}
