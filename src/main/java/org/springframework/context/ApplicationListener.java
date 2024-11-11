package org.springframework.context;

import java.util.EventListener;

/**
 * 由应用程序事件监听器实现的接口，基于 EventListener 实现的观察者模式
 *
 * @author: DoubleW2w
 * @date: 2024/10/23
 * @project: sbs-small-spring
 */
public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {
  /**
   * Handle an application event.
   *
   * @param event the event to respond to
   */
  void onApplicationEvent(E event);
}
