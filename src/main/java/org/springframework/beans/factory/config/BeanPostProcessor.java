package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;

/**
 * 用于修改实例化后的bean的修改扩展点
 *
 * @author derekyi
 * @date 2020/11/28
 */
public interface BeanPostProcessor {
  /**
   * 在 Bean 初始化之前执行，即在 Bean 已实例化、属性已设置并调用了 Aware 接口方法后，但在调用 init 方法（或初始化回调）之前。
   *
   * @param bean bean对象
   * @param beanName bean名称
   * @return bean对象
   * @throws BeansException
   */
  Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException;

  /**
   * 在bean执行初始化方法之后执行此方法
   *
   * @param bean
   * @param beanName
   * @return
   * @throws BeansException
   */
  Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException;
}
