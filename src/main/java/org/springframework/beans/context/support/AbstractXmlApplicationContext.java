package org.springframework.beans.context.support;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

/**
 * 抽象基类 XML 上下文
 *
 * <p>内部使用 XmlBeanDefinitionReader 来读取和解析 XML 配置文件。
 *
 * @author: DoubleW2w
 * @date: 2024/10/8
 * @project: sbs-small-spring
 */
public abstract class AbstractXmlApplicationContext extends AbstractRefreshableApplicationContext {
  @Override
  protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) {
    XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory, this);
    String[] configLocations = getConfigLocations();
    if (null != configLocations) {
      beanDefinitionReader.loadBeanDefinitions(configLocations);
    }
  }

  protected abstract String[] getConfigLocations();
}
