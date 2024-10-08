package org.springframework.beans.context.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.context.ConfigurableApplicationContext;
import org.springframework.beans.core.io.DefaultResourceLoader;
import org.springframework.beans.factory.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.Map;

/**
 * 抽象应用上下文
 *
 * <p>不强制指定用于配置的存储类型;
 *
 * <p>简单地实现了常见的上下文功能。使用模板方法设计模式，需要具体的子类实现抽象方法。
 *
 * @author: DoubleW2w
 * @date: 2024/10/8
 * @project: sbs-small-spring
 */
public abstract class AbstractApplicationContext extends DefaultResourceLoader
    implements ConfigurableApplicationContext {
  @Override
  public void refresh() throws BeansException {
    // 1. 创建 BeanFactory，并加载 BeanDefinition
    refreshBeanFactory();

    // 2. 获取 BeanFactory
    ConfigurableListableBeanFactory beanFactory = getBeanFactory();

    // 3. 在 Bean 实例化之前，执行 BeanFactoryPostProcessor (Invoke factory processors registered as beans in
    // the context.)
    invokeBeanFactoryPostProcessors(beanFactory);

    // 4. BeanPostProcessor 需要提前于其他 Bean 对象实例化之前执行注册操作
    registerBeanPostProcessors(beanFactory);

    // 5. 提前实例化单例Bean对象
    beanFactory.preInstantiateSingletons();
  }

  @Override
  public void registerShutdownHook() {
    Runtime.getRuntime().addShutdownHook(new Thread(this::close));
  }

  @Override
  public void close() {
    getBeanFactory().destroySingletons();
  }

  /**
   * 创建 BeanFactory，并加载 BeanDefinition
   *
   * @throws BeansException
   */
  protected abstract void refreshBeanFactory() throws BeansException;

  /**
   * 获取 BeanFactory
   *
   * @return
   */
  protected abstract ConfigurableListableBeanFactory getBeanFactory();

  /**
   * bean注册结束后，实例化之前
   *
   * @param beanFactory bean工厂
   */
  private void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
    Map<String, BeanFactoryPostProcessor> beanFactoryPostProcessorMap =
        beanFactory.getBeansOfType(BeanFactoryPostProcessor.class);
    for (BeanFactoryPostProcessor beanFactoryPostProcessor : beanFactoryPostProcessorMap.values()) {
      beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
    }
  }

  /**
   * 注册bean实例化后的处理器
   *
   * @param beanFactory bean工厂
   */
  private void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
    Map<String, BeanPostProcessor> beanPostProcessorMap =
        beanFactory.getBeansOfType(BeanPostProcessor.class);
    for (BeanPostProcessor beanPostProcessor : beanPostProcessorMap.values()) {
      beanFactory.addBeanPostProcessor(beanPostProcessor);
    }
  }

  @Override
  public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
    return getBeanFactory().getBeansOfType(type);
  }

  @Override
  public String[] getBeanDefinitionNames() {
    return getBeanFactory().getBeanDefinitionNames();
  }

  @Override
  public Object getBean(String name) throws BeansException {
    return getBeanFactory().getBean(name);
  }

  @Override
  public Object getBean(String name, Object... args) throws BeansException {
    return getBeanFactory().getBean(name, args);
  }

  @Override
  public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
    return getBeanFactory().getBean(name, requiredType);
  }
}
