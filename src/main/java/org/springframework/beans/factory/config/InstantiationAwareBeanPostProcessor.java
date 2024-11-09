package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;

/**
 * 实例化bean后置处理器
 *
 * @author: DoubleW2w
 * @date: 2024/11/9
 * @project: sbs-small-spring
 */
public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {
  /**
   * 在bean实例化之前执行
   *
   * @param beanClass bean对象的类
   * @param beanName bean名称
   * @return bean对象
   * @throws BeansException bean异常
   */
  Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException;
}
