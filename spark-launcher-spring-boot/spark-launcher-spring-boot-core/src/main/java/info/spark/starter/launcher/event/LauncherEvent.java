package info.spark.starter.launcher.event;

import info.spark.starter.common.event.BaseEvent;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:25
 * @since 1.0.0
 */
public class LauncherEvent extends BaseEvent<Object> {
    /** serialVersionUID */
    private static final long serialVersionUID = -8490953221049981401L;

    /**
     * Instantiates a new Launcher event.
     *
     * @param source the source
     * @since 1.0.0
     */
    public LauncherEvent(Object source) {
        super(source);
    }
}
