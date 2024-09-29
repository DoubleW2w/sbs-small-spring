package org.springframework.beans.factory.support;

import org.springframework.beans.factory.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;

/**
 * 实现默认bean创建的抽象bean工厂超类
 *
 * @author: DoubleW2w
 * @date: 2024/9/29
 * @project: sbs-small-spring
 */
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory {
  @Override
  protected Object createBean(String beanName, BeanDefinition beanDefinition) {
    Object bean;
    try {
      bean = beanDefinition.getBeanClass().getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      throw new BeansException("Instantiation of bean failed", e);
    }
    registerSingleton(beanName, bean);
    return bean;
  }
}
