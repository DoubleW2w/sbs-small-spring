package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.BeanDefinition;

/**
 * Bean 定义注册接口
 *
 * @author: DoubleW2w
 * @date: 2024/9/29
 * @project: sbs-small-spring
 */
public interface BeanDefinitionRegistry {
  /**
   * 向注册表中注册 BeanDefinition
   *
   * @param beanName Bean 名称
   * @param beanDefinition Bean 定义
   */
  void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);
}
