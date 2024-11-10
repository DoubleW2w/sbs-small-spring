package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;

/**
 * 实例化bean后置处理器
 *
 * @author: DoubleW2w
 * @date: 2024/11/9
 * @project: sbs-small-spring
 */
public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {
  /**
   * 在 Spring 实例化 Bean 对象之前执行。即在 Spring 调用构造方法之前。
   *
   * @param beanClass bean对象的类
   * @param beanName bean名称
   * @return bean对象
   * @throws BeansException bean异常
   */
  Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException;

  /**
   * 在Spring 实例化 Bean对象之后，设置属性之前执行
   *
   * @param pvs 属性值列表
   * @param bean bean对象
   * @param beanName bean名称
   * @return 结果
   * @throws BeansException bean异常
   */
  PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName)
      throws BeansException;
}
