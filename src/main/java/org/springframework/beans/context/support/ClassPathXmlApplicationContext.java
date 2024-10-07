package org.springframework.beans.context.support;

import org.springframework.beans.BeansException;

/**
 * 类路径XML应用上下文，
 *
 * <p>用于从类路径加载 XML 配置文件并创建应用上下文。
 *
 * <p>将普通路径解释为包含包路径的类路径资源名称
 *
 * @author: DoubleW2w
 * @date: 2024/10/8
 * @project: sbs-small-spring
 */
public class ClassPathXmlApplicationContext extends AbstractXmlApplicationContext {
  private String[] configLocations;

  public ClassPathXmlApplicationContext() {}

  /**
   * 从 XML 中加载 BeanDefinition，并刷新上下文
   *
   * @param configLocations
   * @throws BeansException
   */
  public ClassPathXmlApplicationContext(String configLocations) throws BeansException {
    this(new String[] {configLocations});
  }

  /**
   * 从 XML 中加载 BeanDefinition，并刷新上下文
   *
   * @param configLocations
   * @throws BeansException
   */
  public ClassPathXmlApplicationContext(String[] configLocations) throws BeansException {
    this.configLocations = configLocations;
    refresh();
  }

  @Override
  protected String[] getConfigLocations() {
    return configLocations;
  }
}
