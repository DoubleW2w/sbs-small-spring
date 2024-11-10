package org.springframework.beans.factory;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.core.io.DefaultResourceLoader;
import org.springframework.beans.core.io.Resource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;

import java.util.Properties;

/**
 * 属性占位符配置类
 *
 * @author: DoubleW2w
 * @date: 2024/11/10
 * @project: sbs-small-spring
 */
public class PropertyPlaceholderConfigurer implements BeanFactoryPostProcessor {

  public static final String PLACEHOLDER_PREFIX = "${";

  public static final String PLACEHOLDER_SUFFIX = "}";

  /** 配置文件路径 */
  private String location;

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
      throws BeansException {
    // 加载属性配置文件
    Properties properties = loadProperties();

    // 属性值替换占位符
    processProperties(beanFactory, properties);
  }

  /**
   * 加载属性配置文件
   *
   * @return
   */
  private Properties loadProperties() {
    try {
      DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
      Resource resource = resourceLoader.getResource(location);
      Properties properties = new Properties();
      properties.load(resource.getInputStream());
      return properties;
    } catch (Exception e) {
      throw new BeansException("Could not load properties", e);
    }
  }

  /**
   * 属性值替换占位符
   *
   * @param beanFactory bean工厂
   * @param properties 属性
   * @throws BeansException bean异常
   */
  private void processProperties(ConfigurableListableBeanFactory beanFactory, Properties properties)
      throws BeansException {
    String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
    for (String beanName : beanDefinitionNames) {
      BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
      resolvePropertyValues(beanDefinition, properties);
    }
  }

  private void resolvePropertyValues(BeanDefinition beanDefinition, Properties properties) {
    PropertyValues propertyValues = beanDefinition.getPropertyValues();
    for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
      // 属性占位符的值${xxx}
      Object value = propertyValue.getValue();
      if (!(value instanceof String)) continue;
      // TODO 仅简单支持一个占位符的格式
      String strVal = (String) value;
      StringBuffer buf = new StringBuffer(strVal);
      int startIndex = strVal.indexOf(PLACEHOLDER_PREFIX);
      int endIndex = strVal.indexOf(PLACEHOLDER_SUFFIX);
      if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
        String propKey = strVal.substring(startIndex + 2, endIndex); // 属性名称
        String propVal = properties.getProperty(propKey); // 从配置属性获取属性值
        buf.replace(startIndex, endIndex + 1, propVal);
        propertyValues.addPropertyValue(new PropertyValue(propertyValue.getName(), buf.toString()));
      }
    }
  }

  public void setLocation(String location) {
    this.location = location;
  }
}
