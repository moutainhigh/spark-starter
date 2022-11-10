package info.spark.starter.endpoint.initialization;


import info.spark.starter.common.base.BaseDTO;

import java.math.BigDecimal;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.21 16:59
 * @since 2022.1.1
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class WarmUpRequestDTO extends BaseDTO<String> {
    private static final long serialVersionUID = -946407216751970529L;
    /** Warm up string */
    @NotBlank
    @Pattern(regexp = "warm me up")
    private String warmUpString;

    /** Warm up number */
    @Min(10)
    @Max(20)
    private Integer warmUpNumber;

    /** Warm up enum dto */
    @Valid
    private WarmUpEnum warmUpEnumDto;

    /** Warm up big decimal */
    @NotNull
    private BigDecimal warmUpBigDecimal;

}
