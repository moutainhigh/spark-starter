package info.spark.starter.doc.agent.aop;

import org.jetbrains.annotations.NotNull;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;

/**
 * <p>Description:  </p>
 *
 * @author wanghao
 * @version 1.7.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.11.27 15:21
 * @since 1.7.0
 */
public class PluginAdviser extends AbstractBeanFactoryPointcutAdvisor implements ApplicationContextAware {
    /** serialVersionUID */
    private static final long serialVersionUID = 1938732338676328264L;
    /** ADVISER_NAME */
    public static final String ADVISER_NAME = "agent_doc_adviser";
    /** Application context */
    private ApplicationContext applicationContext;

    /**
     * Producer adviser
     *
     * @since 1.7.0
     */
    public PluginAdviser() {
        this.setAdviceBeanName(PluginAdviser.ADVISER_NAME);
        this.setAdvice(new PluginInterceptor());
        this.setBeanFactory(this.applicationContext);
        this.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    /**
     * Gets pointcut *
     *
     * @return the pointcut
     * @since 1.7.0
     */
    @Override
    public @NotNull Pointcut getPointcut() {
        return new PluginPointCut();
    }

    /**
     * Sets application context *
     *
     * @param applicationContext application context
     * @throws BeansException beans exception
     * @since 1.7.0
     */
    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
