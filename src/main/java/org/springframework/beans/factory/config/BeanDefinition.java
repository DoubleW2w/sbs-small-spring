package org.springframework.beans.factory.config;

/**
 * Bean定义
 *
 * @author: DoubleW2w
 * @date: 2024/9/29
 * @project: sbs-small-spring
 */
public class BeanDefinition {
  /** bean的class类型 */
  private Class beanClass;

  public BeanDefinition(Class beanClass) {
    this.beanClass = beanClass;
  }

  public Class getBeanClass() {
    return beanClass;
  }

  public void setBeanClass(Class beanClass) {
    this.beanClass = beanClass;
  }
}
