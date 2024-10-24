package org.springframework.beans.factory.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
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
public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport
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
    Object sharedInstance = getSingleton(name);
    if (sharedInstance != null) {
      // 如果是 FactoryBean，则需要调用 FactoryBean#getObject
      return (T) getObjectForBeanInstance(sharedInstance, name);
    }

    BeanDefinition beanDefinition = getBeanDefinition(name);
    Object bean = createBean(name, beanDefinition, args);
    return (T) getObjectForBeanInstance(bean, name);
  }

  private Object getObjectForBeanInstance(Object beanInstance, String beanName) {
    // bean实例不是 FactoryBean 类型就直接返回bean实例
    if (!(beanInstance instanceof FactoryBean)) {
      return beanInstance;
    }

    Object object = getCachedObjectForFactoryBean(beanName);
    // 如果缓存中没有找到对象，则通过FactoryBean创建
    if (object == null) {
      FactoryBean<?> factoryBean = (FactoryBean<?>) beanInstance;
      object = getObjectFromFactoryBean(factoryBean, beanName);
    }

    return object;
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
