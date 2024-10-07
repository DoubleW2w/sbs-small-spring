package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;

/**
 * 专门用于处理依赖注入和自动装配的功能
 *
 * @author: DoubleW2w
 * @date: 2024/10/4
 * @project: sbs-small-spring
 */
public interface AutowireCapableBeanFactory extends BeanFactory {
  /**
   * 执行 BeanPostProcessors 的 postProcessBeforeInitialization 方法
   *
   * @param existingBean 存在的bean对象
   * @param beanName bean名称
   * @return bean对象
   * @throws BeansException bean异常
   */
  Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
      throws BeansException;

  /**
   * 执行 BeanPostProcessors的 postProcessAfterInitialization 方法
   *
   * @param existingBean 存在的bean对象
   * @param beanName bean名称
   * @return bean对象
   * @throws BeansException bean异常
   */
  Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
      throws BeansException;
}
