package sample.doc.restdoc.controller;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.15 22:17
 * @since 1.0.0
 */
public class NotFoundException extends RuntimeException {
    /** serialVersionUID */
    private static final long serialVersionUID = 5792446756772820765L;

    /**
     * Not found exception
     *
     * @param i
     * @param s s
     * @since 1.0.0
     */
    public NotFoundException(int i, String s) {
    }
}
