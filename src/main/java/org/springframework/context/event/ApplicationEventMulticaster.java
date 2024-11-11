package org.springframework.context.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * 事件广播器
 *
 * @author: DoubleW2w
 * @date: 2024/10/23
 * @project: sbs-small-spring
 */
public interface ApplicationEventMulticaster {
  /**
   * Add a listener to be notified of all events.
   *
   * @param listener the listener to add
   */
  void addApplicationListener(ApplicationListener<?> listener);

  /**
   * Remove a listener from the notification list.
   *
   * @param listener the listener to remove
   */
  void removeApplicationListener(ApplicationListener<?> listener);

  /**
   * Multicast the given application event to appropriate listeners.
   *
   * @param event the event to multicast
   */
  void multicastEvent(ApplicationEvent event);
}
