package org.springframework.beans.factory;

/**
 * 该接口由在 {@link BeanFactory} 中使用的对象实现，这些对象本身也是自身的工厂。 如果一个 Bean
 * 实现了这个接口，它将作为一个工厂来创建要暴露的对象，而不是直接作为一个将被暴露的 Bean 实例。
 *
 * @author: DoubleW2w
 * @date: 2024/10/22
 * @project: sbs-small-spring
 */
public interface FactoryBean<T> {
  T getObject() throws Exception;

  Class<?> getObjectType();

  boolean isSingleton();
}
