package org.springframework.beans.factory;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.core.io.DefaultResourceLoader;
import org.springframework.beans.core.io.Resource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.util.StringValueResolver;

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

    // 往容器中添加字符解析器，供解析@Value注解使用
    StringValueResolver valueResolver = new PlaceholderResolvingStringValueResolver(properties);
    beanFactory.addEmbeddedValueResolver(valueResolver);
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
    // bean定义的属性对
    PropertyValues propertyValues = beanDefinition.getPropertyValues();
    for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
      // bean定义的属性值
      Object value = propertyValue.getValue();
      if (value instanceof String) {
        // 解析占位符并完成替换
        value = resolvePlaceholder((String) value, properties);
        propertyValues.addPropertyValue(new PropertyValue(propertyValue.getName(), value));
      }
    }
  }

  private String resolvePlaceholder(String value, Properties properties) {
    String strVal = value;
    StringBuffer buf = new StringBuffer(strVal);
    int startIndex = strVal.indexOf(PLACEHOLDER_PREFIX);
    int endIndex = strVal.indexOf(PLACEHOLDER_SUFFIX);
    if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
      String propKey = strVal.substring(startIndex + 2, endIndex);
      String propVal = properties.getProperty(propKey);
      buf.replace(startIndex, endIndex + 1, propVal);
    }
    return buf.toString();
  }

  public void setLocation(String location) {
    this.location = location;
  }

  /**
   * 占位符字符串值解析器
   */
  private class PlaceholderResolvingStringValueResolver implements StringValueResolver {

    private final Properties properties;

    public PlaceholderResolvingStringValueResolver(Properties properties) {
      this.properties = properties;
    }

    public String resolveStringValue(String strVal) throws BeansException {
      return PropertyPlaceholderConfigurer.this.resolvePlaceholder(strVal, properties);
    }
  }
}
