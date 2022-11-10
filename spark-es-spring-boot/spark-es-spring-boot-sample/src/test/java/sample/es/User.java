package sample.es;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.07.13 18:13
 * @since 1.5.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class User {
    /** Name */
    private String name;
    /** Age */
    private int age;
}
