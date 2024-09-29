package org.springframework.beans.factory.config;

/**
 * 单例 Bean 注册表：提供一个管理单例 Bean 的统一接口，简化单例Bean的注册和访问
 *
 * @author: DoubleW2w
 * @date: 2024/9/29
 * @project: sbs-small-spring
 */
public interface SingletonBeanRegistry {
  /**
   * 返回在给定名称下注册的（原始）单例对象。
   *
   * @param beanName 要查找的bean的名称
   * @return 返回注册的单例对象
   */
  Object getSingleton(String beanName);

  /**
   * 注册单利对象
   *
   * @param beanName Bean 对象名称
   * @param singletonObject Bean 对象
   */
  void registerSingleton(String beanName, Object singletonObject);
}
