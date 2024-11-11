package org.springframework.context;

/**
 * 事件发布者接口
 *
 * @author: DoubleW2w
 * @date: 2024/10/23
 * @project: sbs-small-spring
 */
public interface ApplicationEventPublisher {
  /**
   * 将应用程序事件通知注册到此应用程序的所有侦听器。 事件可以是框架事件（如RequestHandledEvent）或应用程序特定的事件。
   *
   * @param event the event to publish
   */
  void publishEvent(ApplicationEvent event);
}
