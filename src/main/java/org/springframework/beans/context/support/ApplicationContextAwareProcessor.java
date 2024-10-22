package org.springframework.beans.context.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.context.ApplicationContext;
import org.springframework.beans.context.ApplicationContextAware;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author: DoubleW2w
 * @date: 2024/10/8
 * @project: sbs-small-spring
 */
public class ApplicationContextAwareProcessor implements BeanPostProcessor {
  private final ApplicationContext applicationContext;

  public ApplicationContextAwareProcessor(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    if (bean instanceof ApplicationContextAware) {
      ((ApplicationContextAware) bean).setApplicationContext(applicationContext);
    }
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }
}
