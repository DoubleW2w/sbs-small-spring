package org.springframework.context.event;

/**
 * 上下文关闭事件类
 *
 * @author: DoubleW2w
 * @date: 2024/10/23
 * @project: sbs-small-spring
 */
public class ContextClosedEvent extends ApplicationContextEvent {
  /**
   * Constructs a prototypical Event.
   *
   * @param source the object on which the Event initially occurred
   * @throws IllegalArgumentException if source is null
   */
  public ContextClosedEvent(Object source) {
    super(source);
  }
}
