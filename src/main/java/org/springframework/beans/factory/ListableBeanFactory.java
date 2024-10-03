package org.springframework.beans.factory;

import org.springframework.beans.BeansException;

import java.util.Map;

/**
 * 提供一种批量获取和操作容器中所有Bean的机制，而不仅仅是通过名称或类型获取单个Bean。它特别适合那些需要处理大量Bean定义的场景。
 *
 * @author: DoubleW2w
 * @date: 2024/10/4
 * @project: sbs-small-spring
 */
public interface ListableBeanFactory extends BeanFactory {
  /**
   * 按照类型返回 Bean 实例
   *
   * @param type 类型
   * @param <T>
   * @return 相同类型的Bean
   * @throws BeansException 异常
   */
  <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException;

  /**
   * Return the names of all beans defined in this registry.
   *
   * <p>返回注册表中所有的Bean名称
   */
  String[] getBeanDefinitionNames();
}
