package sample.doc.restdoc.entity;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.15 22:23
 * @since 1.0.0
 */
public class Pets {
    /**
     * Status is predicate
     *
     * @param status status
     * @return the predicate
     * @since 1.0.0
     */
    public static Predicate<Pet> statusIs(String status) {
        return pet -> Objects.equals(pet.getStatus(), status);
    }

    /**
     * Tags contain predicate
     *
     * @param tag tag
     * @return the predicate
     * @since 1.0.0
     */
    public static Predicate<Pet> tagsContain(String tag) {
        return pet -> pet.getTags().contains(tag);
    }
}
