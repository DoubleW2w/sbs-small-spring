package org.springframework.beans.factory.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;

/**
 * Bean工厂的积累
 *
 * @author: DoubleW2w
 * @date: 2024/9/29
 * @project: sbs-small-spring
 */
public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry
    implements BeanFactory {

  @Override
  public Object getBean(String name) throws BeansException {
    return doGetBean(name, null);
  }

  @Override
  public Object getBean(String name, Object... args) throws BeansException {
    return doGetBean(name, args);
  }

  @Override
  public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
    return (T) getBean(name);
  }

  protected <T> T doGetBean(final String name, final Object[] args) {
    T bean = (T) getSingleton(name);
    if (bean != null) {
      return bean;
    }

    BeanDefinition beanDefinition = getBeanDefinition(name);
    return (T) createBean(name, beanDefinition, args);
  }

  /** 获取Bean定义 */
  protected abstract BeanDefinition getBeanDefinition(String beanName);

  /**
   * 根据Bean定义和Bean名称创建Bean
   *
   * @param beanName bean名称
   * @param beanDefinition bean定义
   * @param args 构造函数参数
   * @return bean对象
   */
  protected abstract Object createBean(
      String beanName, BeanDefinition beanDefinition, Object[] args);
}
