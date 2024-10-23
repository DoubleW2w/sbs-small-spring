package org.springframework.beans.context.event;

import org.springframework.beans.BeansException;
import org.springframework.beans.context.ApplicationEvent;
import org.springframework.beans.context.ApplicationListener;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.util.ClassUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * 对事件广播器的公用方法提取，提供一些基本功能
 *
 * @author: DoubleW2w
 * @date: 2024/10/23
 * @project: sbs-small-spring
 */
public abstract class AbstractApplicationEventMulticaster
    implements ApplicationEventMulticaster, BeanFactoryAware {
  public final Set<ApplicationListener<ApplicationEvent>> applicationListeners =
      new LinkedHashSet<>();

  private BeanFactory beanFactory;

  @Override
  public void addApplicationListener(ApplicationListener<?> listener) {
    applicationListeners.add((ApplicationListener<ApplicationEvent>) listener);
  }

  @Override
  public void removeApplicationListener(ApplicationListener<?> listener) {
    applicationListeners.remove(listener);
  }

  @Override
  public final void setBeanFactory(BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }

  /**
   * 返回与给定事件类型匹配的ApplicationListeners集合。不匹配的监听器会被提前排除。
   *
   * @param event 要传播的事件。允许根据缓存的匹配信息，尽早排除非匹配的监听器。
   * @return 类型匹配的ApplicationListeners集合
   * @see org.springframework.beans.context.ApplicationListener
   */
  protected Collection<ApplicationListener> getApplicationListeners(ApplicationEvent event) {
    LinkedList<ApplicationListener> allListeners = new LinkedList<>();
    for (ApplicationListener<ApplicationEvent> listener : applicationListeners) {
      if (supportsEvent(listener, event)) {
        allListeners.add(listener);
      }
    }
    return allListeners;
  }

  /** 监听器是否对该事件感兴趣 */
  protected boolean supportsEvent(
      ApplicationListener<ApplicationEvent> applicationListener, ApplicationEvent event) {
    Class<? extends ApplicationListener> listenerClass = applicationListener.getClass();

    // 按照 CglibSubclassingInstantiationStrategy、SimpleInstantiationStrategy 不同的实例化类型，需要判断后获取目标 class
    Class<?> targetClass =
        ClassUtils.isCglibProxyClass(listenerClass) ? listenerClass.getSuperclass() : listenerClass;
    Type genericInterface = targetClass.getGenericInterfaces()[0];
    // 实际的泛型参数
    Type actualTypeArgument = ((ParameterizedType) genericInterface).getActualTypeArguments()[0];
    // 实际的泛型参数名称
    String className = actualTypeArgument.getTypeName();
    Class<?> eventClassName;
    try {
      eventClassName = Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new BeansException("wrong event class name: " + className);
    }
    // 判定「此 eventClassName 对象所表示的类或接口」与指定的 「event.getClass() 参数所表示的类或接口」是否相同，或者是否是其超类或超接口。
    // isAssignableFrom是用来判断子类和父类的关系的，或者接口的实现类和接口的关系的，默认所有的类的终极父类都是Object。
    // 如果A.isAssignableFrom(B)结果是true，证明B可以转换成为A,也就是A可以由B转换而来。
    return eventClassName.isAssignableFrom(event.getClass());
  }
}
