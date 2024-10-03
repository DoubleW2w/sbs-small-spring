package org.springframework.beans.factory.support;

import cn.hutool.core.bean.BeanUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanReference;

import java.lang.reflect.Constructor;

/**
 * 实现默认bean创建的抽象bean工厂超类
 *
 * @author: DoubleW2w
 * @date: 2024/9/29
 * @project: sbs-small-spring
 */
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory {

  @Getter @Setter
  private InstantiationStrategy instantiationStrategy = new CglibSubclassingInstantiationStrategy();

  @Override
  protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) {
    Object bean;
    try {
      // 创建Bean
      bean = createBeanInstance(beanDefinition, beanName, args);
      // 填充属性
      applyPropertyValues(beanName, bean, beanDefinition);
    } catch (Exception e) {
      throw new BeansException("Instantiation of bean failed", e);
    }

    registerSingleton(beanName, bean);
    return bean;
  }

  /**
   * 填充属性
   *
   * @param beanName bean名称
   * @param bean bean对象
   * @param beanDefinition bean定义
   */
  private void applyPropertyValues(String beanName, Object bean, BeanDefinition beanDefinition) {
    try{
      PropertyValues propertyValues = beanDefinition.getPropertyValues();
      for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
        String name = propertyValue.getName();
        Object value = propertyValue.getValue();
        // 如果是注入bean依赖
        if (value instanceof BeanReference) {
          // A 依赖 B，获取 B 的实例化
          BeanReference beanReference = (BeanReference) value;
          value = getBean(beanReference.getBeanName());
        }
        // 为bean对象的name属性填充value
        BeanUtil.setFieldValue(bean, name, value);
      }
    }catch (Exception e){
      throw new BeansException("Error setting property values：" + beanName);

    }
  }

  /**
   * 实例化Bean对象
   *
   * @param beanDefinition Bean定义
   * @param beanName bean名称
   * @param args 参数
   * @return 结果
   */
  protected Object createBeanInstance(
      BeanDefinition beanDefinition, String beanName, Object[] args) {
    Constructor constructorToUse = null;
    Class<?> beanClass = beanDefinition.getBeanClass();
    Constructor<?>[] declaredConstructors = beanClass.getDeclaredConstructors();
    for (Constructor ctor : declaredConstructors) {
      if (null != args && ctor.getParameterTypes().length == args.length) {
        constructorToUse = ctor;
        break;
      }
    }
    return getInstantiationStrategy().instantiate(beanDefinition, beanName, constructorToUse, args);
  }
}
