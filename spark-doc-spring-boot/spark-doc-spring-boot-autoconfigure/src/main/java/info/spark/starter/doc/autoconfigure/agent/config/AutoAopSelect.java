package info.spark.starter.doc.autoconfigure.agent.config;

import info.spark.starter.doc.autoconfigure.agent.annotation.EnableAutoAop;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AdviceModeImportSelector;
import org.springframework.context.annotation.AutoProxyRegistrar;

/**
 * <p>Description:  </p>
 *
 * @author wanghao
 * @version 1.7.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.11.27 15:11
 * @since 1.7.0
 */
public class AutoAopSelect extends AdviceModeImportSelector<EnableAutoAop> {

    /**
     * Select imports
     *
     * @param adviceMode advice mode
     * @return the string [ ]
     * @since 1.7.0
     */
    @Override
    protected String[] selectImports(@NotNull AdviceMode adviceMode) {
        if (adviceMode == AdviceMode.PROXY) {
            return new String[] {AutoProxyRegistrar.class.getName(),
                                 AopProxyRegisterAutoConfigure.class.getName()};
        } else {
            return new String[0];
        }

    }
}
