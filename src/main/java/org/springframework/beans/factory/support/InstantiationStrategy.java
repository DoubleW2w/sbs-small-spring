package org.springframework.beans.factory.support;

import org.springframework.beans.factory.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;

import java.lang.reflect.Constructor;

/**
 * 实例化策略接口
 *
 * @author: DoubleW2w
 * @date: 2024/9/29
 * @project: sbs-small-spring
 */
public interface InstantiationStrategy {
  /**
   * 根据Bean定义来实例化Bean
   *
   * @param beanDefinition Bean定义
   * @param beanName Bean名称
   * @param ctor 构造函数
   * @param args 构造函数参数
   * @return Bean对象
   * @throws BeansException 异常
   */
  Object instantiate(
      BeanDefinition beanDefinition, String beanName, Constructor ctor, Object[] args)
      throws BeansException;
}
