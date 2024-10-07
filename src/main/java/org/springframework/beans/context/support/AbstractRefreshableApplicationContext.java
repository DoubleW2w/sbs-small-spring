package org.springframework.beans.context.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * 抽象基类刷新应用上下文
 *
 * <p>Base class for {@link org.springframework.beans.context.ApplicationContext}
 *
 * <p>实现被认为支持对{@link AbstractApplicationContext#refresh()}的多次调用，每次创建一个新的内部bean工厂实例。
 *
 * <p>Typically (but not necessarily), such a context will be driven by a set of config locations to
 * load bean definitions from.
 *
 * @author: DoubleW2w
 * @date: 2024/10/8
 * @project: sbs-small-spring
 */
public abstract class AbstractRefreshableApplicationContext extends AbstractApplicationContext {
  private DefaultListableBeanFactory beanFactory;

  /**
   * 加载bean定义
   *
   * @param beanFactory bean工厂
   */
  protected abstract void loadBeanDefinitions(DefaultListableBeanFactory beanFactory);

  @Override
  protected void refreshBeanFactory() throws BeansException {
    DefaultListableBeanFactory beanFactory = createBeanFactory();
    loadBeanDefinitions(beanFactory);
    this.beanFactory = beanFactory;
  }

  /**
   * 创建bean工厂
   *
   * @return
   */
  private DefaultListableBeanFactory createBeanFactory() {
    return new DefaultListableBeanFactory();
  }

  @Override
  protected ConfigurableListableBeanFactory getBeanFactory() {
    return beanFactory;
  }
}
