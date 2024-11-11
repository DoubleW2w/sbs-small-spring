package org.springframework.beans.factory.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.SingletonBeanRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 单例Bean注册接口的默认实现
 *
 * @author: DoubleW2w
 * @date: 2024/9/29
 * @project: sbs-small-spring
 */
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {
  /** null单例对象的内部标记 */
  protected static final Object NULL_OBJECT = new Object();

  // 一级缓存
  private Map<String, Object> singletonObjects = new HashMap<>();

  // 二级缓存
  private Map<String, Object> earlySingletonObjects = new HashMap<>();

  // 三级缓存
  private Map<String, ObjectFactory<?>> singletonFactories =
      new HashMap<String, ObjectFactory<?>>();

  private final Map<String, DisposableBean> disposableBeans = new HashMap<>();

  @Override
  public Object getSingleton(String beanName) {
    // 先从一级缓存中拿
    Object singletonObject = singletonObjects.get(beanName);
    if (singletonObject == null) {
      // 从二级缓存中拿
      singletonObject = earlySingletonObjects.get(beanName);
      if (singletonObject == null) {
        // 从三级缓存中拿
        ObjectFactory<?> singletonFactory = singletonFactories.get(beanName);
        if (singletonFactory != null) {
          singletonObject = singletonFactory.getObject();
          // 从三级缓存放进二级缓存,并移除三级缓存
          earlySingletonObjects.put(beanName, singletonObject);
          singletonFactories.remove(beanName);
        }
      }
    }
    return singletonObject;
  }

  @Override
  public void registerSingleton(String beanName, Object singletonObject) {
    singletonObjects.put(beanName, singletonObject);
    earlySingletonObjects.remove(beanName);
    singletonFactories.remove(beanName);
  }

  protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
    if (!singletonObjects.containsKey(beanName)) {
      singletonFactories.put(beanName, singletonFactory);
      earlySingletonObjects.remove(beanName);
    }
  }

  public void registerDisposableBean(String beanName, DisposableBean bean) {
    disposableBeans.put(beanName, bean);
  }

  public void destroySingletons() {
    Set<String> keySet = this.disposableBeans.keySet();
    Object[] disposableBeanNames = keySet.toArray();

    for (int i = disposableBeanNames.length - 1; i >= 0; i--) {
      Object beanName = disposableBeanNames[i];
      DisposableBean disposableBean = disposableBeans.remove(beanName);
      try {
        disposableBean.destroy();
      } catch (Exception e) {
        throw new BeansException(
            "Destroy method on bean with name '" + beanName + "' threw an exception", e);
      }
    }
  }
}
