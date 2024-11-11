package org.springframework.context.event;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

/**
 * 事件上下文
 *
 * @author: DoubleW2w
 * @date: 2024/10/23
 * @project: sbs-small-spring
 */
public class ApplicationContextEvent extends ApplicationEvent {

  /**
   * Constructs a prototypical Event.
   *
   * @param source the object on which the Event initially occurred
   * @throws IllegalArgumentException if source is null
   */
  public ApplicationContextEvent(Object source) {
    super(source);
  }

  /** Get the <code>ApplicationContext</code> that the event was raised for. */
  public final ApplicationContext getApplicationContext() {
    return (ApplicationContext) getSource();
  }
}
