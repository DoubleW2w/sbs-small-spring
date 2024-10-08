package org.springframework.beans.factory.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.PropertyValues;

/**
 * Bean定义
 *
 * @author: DoubleW2w
 * @date: 2024/9/29
 * @project: sbs-small-spring
 */
public class BeanDefinition {
  /** bean的class类型 */
  @Setter @Getter private Class beanClass;

  /** 属性值集合合 */
  @Setter @Getter private PropertyValues propertyValues;

  /** 初始化方法 */
  @Setter @Getter private String initMethodName;

  /** 销毁方法 */
  @Setter @Getter private String destroyMethodName;

  public BeanDefinition(Class beanClass) {
    this.beanClass = beanClass;
    this.propertyValues = new PropertyValues();
  }

  public BeanDefinition(Class beanClass, PropertyValues propertyValues) {
    this.beanClass = beanClass;
    this.propertyValues = propertyValues != null ? propertyValues : new PropertyValues();
  }
}
