package org.springframework.beans.factory;

/**
 * bean工厂：负责注册bean和获取bean
 *
 * @author derekyi
 * @date 2020/11/22
 * @project: sbs-small-spring
 */
public interface BeanFactory {

  /**
   * 返回 Bean 的实例对象
   *
   * @param name 要检索的bean的名称
   * @return 实例化的 Bean 对象
   * @throws BeansException 不能获取 Bean 对象，则抛出异常
   */
  Object getBean(String name) throws BeansException;

  /**
   * 返回含构造函数的 Bean 实例对象
   *
   * @param name 要检索的bean的名称
   * @param args 构造函数入参
   * @return 实例化的 Bean 对象
   * @throws BeansException 不能获取 Bean 对象，则抛出异常
   */
  Object getBean(String name, Object... args) throws BeansException;
}
