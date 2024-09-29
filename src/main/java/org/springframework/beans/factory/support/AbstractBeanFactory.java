package org.springframework.beans.factory.support;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;

/**
 * @author: DoubleW2w
 * @date: 2024/9/29
 * @project: sbs-small-spring
 */
public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry
    implements BeanFactory {

  @Override
  public Object getBean(String name) throws BeansException {
    Object bean = getSingleton(name);
    if (bean != null) {
      return bean;
    }

    BeanDefinition beanDefinition = getBeanDefinition(name);
    return createBean(name, beanDefinition);
  }

  /** 获取Bean定义 */
  protected abstract BeanDefinition getBeanDefinition(String beanName);

  /**
   * 根据Bean定义和Bean名称创建Bean
   *
   * @param beanName bean名称
   * @param beanDefinition bean定义
   * @return bean对象
   */
  protected abstract Object createBean(String beanName, BeanDefinition beanDefinition);
}
