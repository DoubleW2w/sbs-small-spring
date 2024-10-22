package org.springframework.beans.factory.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean工厂的积累
 *
 * @author: DoubleW2w
 * @date: 2024/9/29
 * @project: sbs-small-spring
 */
public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry
    implements ConfigurableBeanFactory {

  /** ClassLoader to resolve bean class names with, if necessary */
  private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

  /** BeanPostProcessors to apply in createBean */
  private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

  @Override
  public Object getBean(String name) throws BeansException {
    return doGetBean(name, null);
  }

  @Override
  public Object getBean(String name, Object... args) throws BeansException {
    return doGetBean(name, args);
  }

  @Override
  public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
    return (T) getBean(name);
  }

  protected <T> T doGetBean(final String name, final Object[] args) {
    Object bean = getSingleton(name);
    if (bean != null) {
      return (T) bean;
    }

    BeanDefinition beanDefinition = getBeanDefinition(name);
    return (T) createBean(name, beanDefinition, args);
  }

  protected abstract BeanDefinition getBeanDefinition(String beanName);

  protected abstract Object createBean(
      String beanName, BeanDefinition beanDefinition, Object[] args);

  @Override
  public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
    this.beanPostProcessors.remove(beanPostProcessor);
    this.beanPostProcessors.add(beanPostProcessor);
  }

  /**
   * Return the list of BeanPostProcessors that will get applied to beans created with this factory.
   */
  public List<BeanPostProcessor> getBeanPostProcessors() {
    return this.beanPostProcessors;
  }

  public ClassLoader getBeanClassLoader() {
    return this.beanClassLoader;
  }
}
