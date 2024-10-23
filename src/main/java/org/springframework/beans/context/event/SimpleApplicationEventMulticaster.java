package org.springframework.beans.context.event;

import org.springframework.beans.BeansException;
import org.springframework.beans.context.ApplicationEvent;
import org.springframework.beans.context.ApplicationListener;
import org.springframework.beans.factory.BeanFactory;

/**
 * 简单的 {@link ApplicationEventMulticaster} 接口实现
 *
 * @author: DoubleW2w
 * @date: 2024/10/23
 * @project: sbs-small-spring
 */
public class SimpleApplicationEventMulticaster extends AbstractApplicationEventMulticaster {

  public SimpleApplicationEventMulticaster(BeanFactory beanFactory) {
    setBeanFactory(beanFactory);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void multicastEvent(final ApplicationEvent event) {
    for (final ApplicationListener listener : getApplicationListeners(event)) {
      listener.onApplicationEvent(event);
    }
  }
}
