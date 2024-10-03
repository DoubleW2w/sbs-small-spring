package org.springframework.beans.factory.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认的Bean工厂实现类
 *
 * @author: DoubleW2w
 * @date: 2024/9/29
 * @project: sbs-small-spring
 */
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory
    implements BeanDefinitionRegistry {

  private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

  @Override
  protected BeanDefinition getBeanDefinition(String beanName) {
    BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
    if (beanDefinition == null)
      throw new BeansException("No bean named '" + beanName + "' is defined");
    return beanDefinition;
  }

  @Override
  public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
    beanDefinitionMap.put(beanName, beanDefinition);
  }

}
