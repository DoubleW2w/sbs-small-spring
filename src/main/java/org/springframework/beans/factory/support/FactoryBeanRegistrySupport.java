package org.springframework.beans.factory.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 提供基础类的单例Bean注册，其中这些单例Bean需要处理 {@link FactoryBean} 实例,并且整合了 {@link DefaultSingletonBeanRegistry}'s
 * 单例Bean的注册管理
 *
 * @author: DoubleW2w
 * @date: 2024/10/22
 * @project: sbs-small-spring
 */
public abstract class FactoryBeanRegistrySupport extends DefaultSingletonBeanRegistry {
  /** FactoryBean 创建的单例对象的缓存: FactoryBean name --> object */
  private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>();

  /**
   * 根据Bean名称获取FactoryBean的缓存对象
   *
   * <p>它首先尝试从缓存中获取对象如果对象不存在或者不是特殊标记的NULL_OBJECT，则返回该对象，否则返回null。
   *
   * @param beanName
   * @return
   */
  protected Object getCachedObjectForFactoryBean(String beanName) {
    Object object = this.factoryBeanObjectCache.get(beanName);
    return (object != NULL_OBJECT ? object : null);
  }

  /**
   * 从FactoryBean中获取对象
   *
   * @param factory FactoryBean实例，用于生成对象
   * @param beanName bean的名称
   * @return 返回由FactoryBean生成的对象，如果生成失败则返回null
   */
  protected Object getObjectFromFactoryBean(FactoryBean factory, String beanName) {
    // 检查FactoryBean是否为单例对象
    if (factory.isSingleton()) {
      Object object = this.factoryBeanObjectCache.get(beanName);
      // 如果缓存中没有该对象，则调用方法生成对象
      if (object == null) {
        object = doGetObjectFromFactoryBean(factory, beanName);
        this.factoryBeanObjectCache.put(beanName, (object != null ? object : NULL_OBJECT));
      }
      // 返回缓存的对象，如果对象为占位符NULL_OBJECT，则返回null
      return (object != NULL_OBJECT ? object : null);
    } else {
      // 如果FactoryBean不是单例对象，则直接调用方法生成并返回对象
      return doGetObjectFromFactoryBean(factory, beanName);
    }
  }

  /** 从 FactoryBean 中获取实际的对象实例 */
  private Object doGetObjectFromFactoryBean(final FactoryBean factory, final String beanName) {
    try {
      return factory.getObject();
    } catch (Exception e) {
      throw new BeansException(
          "FactoryBean threw exception on object[" + beanName + "] creation", e);
    }
  }
}
