package org.springframework.beans.factory.config;

import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.util.StringValueResolver;
import org.springframework.core.convert.ConversionService;

/**
 * 与Bean的作用域（Scope）、生命周期回调、依赖注入等相关的功能
 *
 * @author: DoubleW2w
 * @date: 2024/10/4
 * @project: sbs-small-spring
 */
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {

  String SCOPE_SINGLETON = "singleton";

  String SCOPE_PROTOTYPE = "prototype";

  /**
   * @param beanPostProcessor bean实例化后的处理器
   */
  void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

  /** 销毁单例对象 */
  void destroySingletons();

  /**
   * 为嵌入值添加字符串解析器
   *
   * @param valueResolver 字符串解析器
   * @since 3.0
   */
  void addEmbeddedValueResolver(StringValueResolver valueResolver);

  /**
   * 解析嵌入的值，比如注解属性
   *
   * @param value 待解析的值
   * @since 3.0
   */
  String resolveEmbeddedValue(String value);

  void setConversionService(ConversionService conversionService);

  ConversionService getConversionService();
}
