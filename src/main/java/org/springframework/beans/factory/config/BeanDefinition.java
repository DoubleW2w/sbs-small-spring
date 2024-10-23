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

  String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;

  String SCOPE_PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;

  /** bean的class类型 */
  @Setter @Getter private Class beanClass;

  /** 属性值集合合 */
  @Setter @Getter private PropertyValues propertyValues;

  /** 初始化方法 */
  @Setter @Getter private String initMethodName;

  /** 销毁方法 */
  @Setter @Getter private String destroyMethodName;

  @Getter private String scope = SCOPE_SINGLETON;

  @Getter private boolean singleton = true;

  @Getter private boolean prototype = false;

  public BeanDefinition(Class beanClass) {
    this.beanClass = beanClass;
    this.propertyValues = new PropertyValues();
  }

  public BeanDefinition(Class beanClass, PropertyValues propertyValues) {
    this.beanClass = beanClass;
    this.propertyValues = propertyValues != null ? propertyValues : new PropertyValues();
  }

  // 在xml注册Bean定义时，通过scope字段来判断是单例还是原型
  public void setScope(String scope) {
    this.scope = scope;
    this.singleton = SCOPE_SINGLETON.equalsIgnoreCase(scope);
    this.prototype = SCOPE_PROTOTYPE.equalsIgnoreCase(scope);
  }
}
