package org.springframework.beans.factory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

/**
 * @author: DoubleW2w
 * @date: 2024/10/4
 * @project: sbs-small-spring
 */
public interface ConfigurableListableBeanFactory
    extends ListableBeanFactory, AutowireCapableBeanFactory, ConfigurableBeanFactory {

  /**
   * 根据名称查找BeanDefinition
   *
   * @param beanName bean名称
   * @return 返回
   * @throws BeansException 如果找不到BeanDefintion
   */
  BeanDefinition getBeanDefinition(String beanName) throws BeansException;

  void preInstantiateSingletons() throws BeansException;
}
